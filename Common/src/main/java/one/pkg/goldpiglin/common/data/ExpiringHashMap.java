package one.pkg.goldpiglin.common.data;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ExpiringHashMap<K, V> implements Map<K, V> {
    private final long expirationTime;
    private final ConcurrentHashMap<K, V> map;
    private final ConcurrentHashMap<K, Long> expirationMap;

    public ExpiringHashMap(long expirationTime, long expirationScannerTime) {
        this.map = new ConcurrentHashMap<>();
        this.expirationMap = new ConcurrentHashMap<>();
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

        Set<K> keysToRemove = expirationMap.entrySet().stream()
                .filter(entry -> entry.getValue() < currentTime)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (!keysToRemove.isEmpty())
            keysToRemove.forEach(key -> {
                expirationMap.remove(key);
                map.remove(key);
            });

        syncMaps();
    }

    private void syncMaps() {
        // Two-way balance to avoid strange problems
        if (map.size() != expirationMap.size()) {
            map.keySet().removeIf(key -> !expirationMap.containsKey(key));
            expirationMap.keySet().removeIf(key -> !map.containsKey(key));
        }
    }

    @Override
    public V put(K key, V value) {
        long expirationTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expirationTime);
        expirationMap.put(key, expirationTimeMillis);
        return map.put(key, value);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        if (m.isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        long expirationTimeMillis = TimeUnit.SECONDS.toMillis(expirationTime);

        m.forEach((key, value) -> {
            map.put(key, value);
            expirationMap.put(key, currentTime + expirationTimeMillis);
        });
    }

    @Override
    public V get(Object key) {
        Long expiration = expirationMap.get(key);
        if (expiration == null) {
            return null;
        }

        if (System.currentTimeMillis() > expiration) {
            remove(key);
            return null;
        }

        return map.get(key);
    }

    public Iterator<Entry<K, V>> iterator() {
        return entrySet().iterator();
    }

    public List<Map.Entry<K, V>> filter(java.util.function.Predicate<Map.Entry<K, V>> predicate) {
        return entryStream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        long currentTime = System.currentTimeMillis();

        return map.entrySet().stream()
                .anyMatch(entry -> {
                    K key = entry.getKey();
                    Long expiration = expirationMap.get(key);

                    if (expiration != null && expiration > currentTime) {
                        V val = entry.getValue();
                        return Objects.equals(value, val);
                    }
                    return false;
                });
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
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean containsKey(Object key) {
        Long expiration = expirationMap.get(key);
        return expiration != null && System.currentTimeMillis() <= expiration && map.containsKey(key);
    }

    @Override
    public void clear() {
        map.clear();
        expirationMap.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        long currentTime = System.currentTimeMillis();

        return expirationMap.entrySet().stream()
                .filter(entry -> entry.getValue() > currentTime)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull Collection<V> values() {
        long currentTime = System.currentTimeMillis();

        return expirationMap.entrySet().stream()
                .filter(entry -> entry.getValue() > currentTime)
                .map(entry -> map.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    private final class EntrySet extends AbstractSet<Entry<K, V>> {
        @Override
        public @NotNull Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e))
                return false;
            Object key = e.getKey();
            V value = get(key);
            return value != null && Objects.equals(value, e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e))
                return false;
            Object key = e.getKey();
            V value = get(key);
            if (value != null && Objects.equals(value, e.getValue())) {
                ExpiringHashMap.this.remove(key);
                return true;
            }
            return false;
        }

        @Override
        public int size() {
            long currentTime = System.currentTimeMillis();
            return (int) expirationMap.entrySet().stream()
                    .filter(entry -> entry.getValue() > currentTime)
                    .count();
        }

        @Override
        public void clear() {
            ExpiringHashMap.this.clear();
        }
    }

    private final class EntryIterator implements Iterator<Entry<K, V>> {
        private final Iterator<Entry<K, Long>> iter = expirationMap.entrySet().iterator();
        private SimpleEntry<K, V> nextEntry;
        private SimpleEntry<K, V> currentEntry;

        EntryIterator() {
            advance();
        }

        private void advance() {
            long currentTime = System.currentTimeMillis();
            nextEntry = null;
            while (iter.hasNext()) {
                Entry<K, Long> e = iter.next();
                if (e.getValue() > currentTime) {
                    V val = map.get(e.getKey());
                    if (val != null) {
                        nextEntry = new SimpleEntry<>(e.getKey(), val);
                        break;
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return nextEntry != null;
        }

        @Override
        public Entry<K, V> next() {
            if (nextEntry == null) throw new NoSuchElementException();
            currentEntry = nextEntry;
            advance();
            return currentEntry;
        }

        @Override
        public void remove() {
            if (currentEntry == null) throw new IllegalStateException();
            ExpiringHashMap.this.remove(currentEntry.getKey());
            currentEntry = null;
        }
    }

    public Stream<SimpleEntry<K, V>> entryStream() {
        long currentTime = System.currentTimeMillis();

        return expirationMap.entrySet().stream()
                .filter(entry -> entry.getValue() > currentTime)
                .map(entry -> {
                    K key = entry.getKey();
                    V value = map.get(key);
                    return (value != null) ? new SimpleEntry<>(key, value) : null;
                })
                .filter(Objects::nonNull);
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

    public record SimpleEntry<K, V>(K key, V value) implements Map.Entry<K, V> {
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
