package one.tranic.goldpiglin.common.data;

import one.tranic.t.utils.Collections;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("unused")
public class ExpiringHashMap<K, V> implements Map<K, V> {
    private final long expirationTime;
    private final Map<K, V> map;
    private final Map<K, Long> expirationMap;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ExpiringHashMap(long expirationTime, long expirationScannerTime) {
        this.map = new HashMap<>();
        this.expirationMap = new HashMap<>();

        this.expirationTime = expirationTime;

        Scheduler.asyncExecute(() -> {
            try {
                for (; ; ) {
                    if (Thread.currentThread().isInterrupted())
                        return;
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
        List<K> keysToRemove = Collections.newArrayList();

        lock.readLock().lock();
        try {
            for (Entry<K, Long> entry : expirationMap.entrySet()) {
                if (entry.getValue() < currentTime) {
                    keysToRemove.add(entry.getKey());
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        if (!keysToRemove.isEmpty()) {
            lock.writeLock().lock();
            try {
                for (K key : keysToRemove) {
                    expirationMap.remove(key);
                    map.remove(key);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        syncMaps();
    }

    private void syncMaps() {
        // Two-way balance to avoid strange problems
        lock.writeLock().lock();
        try {
            if (map.size() != expirationMap.size()) {
                if (map.size() > expirationMap.size()) {
                    map.keySet().removeIf(key -> !expirationMap.containsKey(key));
                } else {
                    expirationMap.keySet().removeIf(key -> !map.containsKey(key));
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isExpired(Object key, Long expiration) {
        return expiration != null && System.currentTimeMillis() > expiration;
    }

    @Override
    public V put(K key, V value) {
        lock.writeLock().lock();
        try {
            expirationMap.put(key, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expirationTime));
            return map.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        if (m.isEmpty()) return;

        lock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            long expirationTimeMillis = TimeUnit.SECONDS.toMillis(expirationTime);

            for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
                K key = entry.getKey();
                V value = entry.getValue();
                map.put(key, value);
                expirationMap.put(key, currentTime + expirationTimeMillis);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(Object key) {
        lock.readLock().lock();
        try {
            Long expiration = expirationMap.get(key);
            if (expiration == null || System.currentTimeMillis() <= expiration)
                return map.get(key);
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            Long expiration = expirationMap.get(key);
            if (expiration == null || System.currentTimeMillis() <= expiration) {
                return map.get(key);
            }
            remove(key);
            return null;
        } finally {
            lock.writeLock().unlock();
        }

    }

    public Iterator<Entry<K, V>> iterator() {
        return entrySet().iterator();
    }

    public List<Map.Entry<K, V>> filter(java.util.function.Predicate<Map.Entry<K, V>> predicate) {
        List<Entry<K, V>> filteredEntries = Collections.newArrayList();
        long currentTime = System.currentTimeMillis();

        lock.readLock().lock();
        try {
            expirationMap.forEach((key, expiration) -> {
                if (expiration > currentTime) {
                    V value = map.get(key);
                    if (value != null) {
                        Map.Entry<K, V> validEntry = new SimpleEntry<>(key, value);
                        if (predicate.test(validEntry)) {
                            filteredEntries.add(validEntry);
                        }
                    }
                }
            });
        } finally {
            lock.readLock().unlock();
        }

        return filteredEntries;
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return map.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        lock.readLock().lock();
        try {
            long currentTime = System.currentTimeMillis();

            for (Map.Entry<K, V> entry : map.entrySet()) {
                K key = entry.getKey();
                Long expiration = expirationMap.get(key);

                if (expiration != null && expiration > currentTime) {
                    V val = entry.getValue();
                    if (value == null ? val == null : value.equals(val)) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return map.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V remove(Object key) {
        lock.writeLock().lock();
        try {
            expirationMap.remove(key);
            return map.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        lock.readLock().lock();
        try {
            return map.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            map.clear();
            expirationMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public @NotNull Set<K> keySet() {
        lock.readLock().lock();
        try {
            Set<K> validEntries = Collections.newHashSet();
            long currentTime = System.currentTimeMillis();

            expirationMap.forEach((key, expiration) -> {
                if (expiration > currentTime)
                    validEntries.add(key);
            });

            return validEntries;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public @NotNull Collection<V> values() {
        lock.readLock().lock();
        try {
            List<V> validEntries = Collections.newArrayList();
            long currentTime = System.currentTimeMillis();

            expirationMap.forEach((key, expiration) -> {
                if (expiration > currentTime) {
                    V value = map.get(key);
                    if (value != null) {
                        validEntries.add(value);
                    }
                }
            });

            return validEntries;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        lock.readLock().lock();
        try {
            Set<Entry<K, V>> validEntries = new HashSet<>();
            long currentTime = System.currentTimeMillis();

            expirationMap.forEach((key, expiration) -> {
                if (expiration > currentTime) {
                    V value = map.get(key);
                    if (value != null) {
                        validEntries.add(new SimpleEntry<>(key, value));
                    }
                }
            });

            return validEntries;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpiringHashMap<?, ?> that = (ExpiringHashMap<?, ?>) o;

        lock.readLock().lock();
        try {
            return expirationTime == that.expirationTime &&
                    Objects.equals(map, that.map) &&
                    Objects.equals(expirationMap, that.expirationMap);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.readLock().lock();
        try {
            return Objects.hash(expirationTime, map, expirationMap);
        } finally {
            lock.readLock().unlock();
        }
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
