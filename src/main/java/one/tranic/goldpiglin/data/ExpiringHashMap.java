package one.tranic.goldpiglin.data;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExpiringHashMap<K, V> {
    private final long expirationTime;
    private final Map<K, V> map;
    private final Map<K, Long> expirationMap;
    private final boolean fastutil;

    public ExpiringHashMap(long expirationTime, long expirationScannerTime) {
        boolean fastutil = false;
        try {
            Class.forName("it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap");
            fastutil = true;
        } catch (ClassNotFoundException ignored) {
        }

        this.fastutil = fastutil;

        this.map = this.fastutil ? new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>() : new HashMap<>();
        this.expirationMap = this.fastutil ? new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>() : new HashMap<>();

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

        if (expirationMap.isEmpty()) return;
        List<K> expiredKeys = getList();

        for (Map.Entry<K, Long> entry : expirationMap.entrySet()) {
            if (entry.getValue() < currentTime) {
                expiredKeys.add(entry.getKey());
            }
        }

        for (K key : expiredKeys) {
            remove(key);
        }
    }

    private <T> List<T> getList() {
        return this.fastutil ? new it.unimi.dsi.fastutil.objects.ObjectArrayList<>() : new ArrayList<>();
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
        List<Map.Entry<K, V>> validEntries = getList();
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            Long expiration = expirationMap.get(entry.getKey());
            if (expiration != null && currentTime <= expiration) {
                validEntries.add(new SimpleEntry<>(entry.getKey(), entry.getValue()));
            }
        }

        return validEntries.iterator();
    }

    public List<Map.Entry<K, V>> filter(java.util.function.Predicate<Map.Entry<K, V>> predicate) {
        List<Map.Entry<K, V>> filteredEntries = getList();
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            Long expiration = expirationMap.get(entry.getKey());
            if (expiration != null && currentTime <= expiration) {
                Map.Entry<K, V> validEntry = new SimpleEntry<>(entry.getKey(), entry.getValue());
                if (predicate.test(validEntry)) {
                    filteredEntries.add(validEntry);
                }
            }
        }

        return filteredEntries;
    }

    public boolean isEmpty() {
        removeExpiredEntries();
        return map.isEmpty();
    }

    public int size() {
        removeExpiredEntries();
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
