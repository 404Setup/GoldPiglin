package one.tranic.goldpiglin.common.data;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExpiringHashMap<K, V> implements Map<K, V> {
    private final long expirationTime;
    private final Map<K, V> map;
    private final Map<K, Long> expirationMap;

    public ExpiringHashMap(long expirationTime, long expirationScannerTime) {
        this.map = Collections.newHashMap();
        this.expirationMap = Collections.newHashMap();

        this.expirationTime = expirationTime;

        Scheduler.execute(() -> {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(expirationScannerTime);
                    removeExpiredEntries();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void removeExpiredEntries() {
        long currentTime = System.currentTimeMillis();

        Collections.removeIf(expirationMap, entry -> {
            if (entry.getValue() < currentTime) {
                map.remove(entry.getKey());
                return true;
            }
            return false;
        });
        /*expirationMap.entrySet().removeIf(entry -> {
            if (entry.getValue() < currentTime) {
                map.remove(entry.getKey());
                return true;
            }
            return false;
        });*/

        // Two-way balance to avoid strange problems
        if (map.size() != expirationMap.size()) {
            if (map.size() > expirationMap.size()) {
                map.keySet().removeIf(key -> !expirationMap.containsKey(key));
            } else {
                expirationMap.keySet().removeIf(key -> !map.containsKey(key));
            }
        }
    }

    @Override
    public V put(K key, V value) {
        map.put(key, value);
        expirationMap.put(key, System.currentTimeMillis() + expirationTime);
        return value;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        long currentTime = System.currentTimeMillis();
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            map.put(key, value);
            expirationMap.put(key, currentTime + expirationTime);
        }
    }

    @Override
    public V get(Object key) {
        Long expiration = expirationMap.get(key);
        if (expiration != null && System.currentTimeMillis() > expiration) {
            remove(key);
            return null;
        }
        return map.get(key);
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return entrySet().iterator();
    }

    public List<Map.Entry<K, V>> filter(java.util.function.Predicate<Map.Entry<K, V>> predicate) {
        List<Map.Entry<K, V>> filteredEntries = Collections.newArrayList();
        long currentTime = System.currentTimeMillis();

        expirationMap.forEach((key, expiration) -> {
            if (expiration > currentTime) {
                Map.Entry<K, V> validEntry = new SimpleEntry<>(key, map.get(key));
                if (predicate.test(validEntry)) {
                    filteredEntries.add(validEntry);
                }
            }
        });

        return filteredEntries;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    public int size() {
        return map.size();
    }

    @Override
    public V remove(Object key) {
        expirationMap.remove(key);
        return map.remove(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public void clear() {
        map.clear();
        expirationMap.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        Set<K> validEntries = Collections.newHashSet();
        long currentTime = System.currentTimeMillis();

        expirationMap.forEach((key, expiration) -> {
            if (expiration > currentTime)
                validEntries.add(key);
        });

        return validEntries;
    }

    @Override
    public @NotNull Collection<V> values() {
        List<V> validEntries = Collections.newArrayList();
        long currentTime = System.currentTimeMillis();

        expirationMap.forEach((key, expiration) -> {
            if (expiration > currentTime)
                validEntries.add(map.get(key));
        });

        return validEntries;
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> validEntries = new HashSet<>();
        long currentTime = System.currentTimeMillis();

        expirationMap.forEach((key, expiration) -> {
            if (expiration > currentTime) {
                validEntries.add(new SimpleEntry<>(key, map.get(key)));
            }
        });

        return validEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpiringHashMap<?, ?> that = (ExpiringHashMap<?, ?>) o;
        return expirationTime == that.expirationTime &&
                Objects.equals(map, that.map) &&
                Objects.equals(expirationMap, that.expirationMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expirationTime, map, expirationMap);
    }

    public void set(K key, V value) {
        put(key, value);
    }

    private record SimpleEntry<K, V>(K key, V value) implements Map.Entry<K, V> {
        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("setValue is not supported");
        }
    }
}
