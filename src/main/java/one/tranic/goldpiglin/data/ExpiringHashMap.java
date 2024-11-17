package one.tranic.goldpiglin.data;

import java.util.*;
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

        if (expirationMap.isEmpty()) return;
        List<K> expiredKeys = Util.newArrayList();

        for (Map.Entry<K, Long> entry : expirationMap.entrySet()) {
            if (entry.getValue() < currentTime) {
                expiredKeys.add(entry.getKey());
            }
        }

        for (K key : expiredKeys) {
            remove(key);
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

        for (Map.Entry<K, V> entry : map.entrySet()) {
            Long expiration = expirationMap.get(entry.getKey());
            if (expiration != null && currentTime <= expiration) {
                validEntries.add(new SimpleEntry<>(entry.getKey(), entry.getValue()));
            }
        }

        return validEntries.iterator();
    }

    public List<Map.Entry<K, V>> filter(java.util.function.Predicate<Map.Entry<K, V>> predicate) {
        List<Map.Entry<K, V>> filteredEntries = Util.newArrayList();
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
