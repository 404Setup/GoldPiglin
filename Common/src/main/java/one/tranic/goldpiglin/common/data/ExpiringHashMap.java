package one.tranic.goldpiglin.common.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExpiringHashMap<K, V> {
    private final long expirationTime;
    private final Map<K, V> map;
    private final Map<K, Long> expirationMap;

    public ExpiringHashMap(long expirationTime, long expirationScannerTime) {
        this.map = Util.newHashMap();
        this.expirationMap = Util.newHashMap();

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

        Util.removeIf(expirationMap, entry -> {
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

    private void put(K key, V value) {
        map.put(key, value);
        expirationMap.put(key, System.currentTimeMillis() + expirationTime);
    }

    public V get(K key) {
        Long expiration = expirationMap.get(key);
        if (expiration != null && System.currentTimeMillis() > expiration) {
            remove(key);
            return null;
        }
        return map.get(key);
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        List<Map.Entry<K, V>> validEntries = Util.newArrayList();
        long currentTime = System.currentTimeMillis();

        expirationMap.forEach((key, expiration) -> {
            if (expiration > currentTime) {
                validEntries.add(new SimpleEntry<>(key, map.get(key)));
            }
        });

        return validEntries.iterator();
    }

    public List<Map.Entry<K, V>> filter(java.util.function.Predicate<Map.Entry<K, V>> predicate) {
        List<Map.Entry<K, V>> filteredEntries = Util.newArrayList();
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

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    public void remove(K key) {
        map.remove(key);
        expirationMap.remove(key);
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public void clear() {
        map.clear();
        expirationMap.clear();
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
