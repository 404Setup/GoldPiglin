package one.tranic.goldpiglin.common.data;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ExpiringHashMapTest {

    @Test
    public void testSyncMapsPerformanceAndCorrectness() throws Exception {
        // Create an instance. The constructor starts a thread, but for the benchmark we ignore it.
        ExpiringHashMap<String, String> map = new ExpiringHashMap<>(1000, 1000);
        Method syncMapsMethod = ExpiringHashMap.class.getDeclaredMethod("syncMaps");
        syncMapsMethod.setAccessible(true);

        Field mapField = ExpiringHashMap.class.getDeclaredField("map");
        mapField.setAccessible(true);
        Map<String, String> internalMap = (Map<String, String>) mapField.get(map);

        Field expirationMapField = ExpiringHashMap.class.getDeclaredField("expirationMap");
        expirationMapField.setAccessible(true);
        Map<String, Long> internalExpirationMap = (Map<String, Long>) expirationMapField.get(map);

        int size = 200000;
        System.out.println("Populating map with " + size + " entries...");
        for (int i = 0; i < size; i++) {
            map.put("key" + i, "value" + i);
        }

        // Simulate desync
        // Remove 10% of entries from internalMap only (simulating drift)
        // syncMaps logic:
        // mapKeys = map.keySet()
        // expirationKeys = expirationMap.keySet()
        // mapKeys.stream().filter(!expirationMap.containsKey).forEach(map::remove) -> removes keys in map but not in expirationMap
        // expirationKeys.stream().filter(!map.containsKey).forEach(expirationMap::remove) -> removes keys in expirationMap but not in map

        // If we remove from internalMap, they are in expirationMap but not in map. expirationMap should remove them.
        for (int i = 0; i < size / 10; i++) {
            internalMap.remove("key" + i);
        }

        // If we remove from expirationMap, they are in map but not in expirationMap. map should remove them.
        for (int i = size / 10; i < 2 * (size / 10) + 1; i++) {
            internalExpirationMap.remove("key" + i);
        }

        System.out.println("Map size: " + internalMap.size());
        System.out.println("Expiration map size: " + internalExpirationMap.size());

        System.out.println("Running syncMaps benchmark...");

        long startTime = System.nanoTime();
        syncMapsMethod.invoke(map);
        long endTime = System.nanoTime();

        System.out.println("Time taken: " + (endTime - startTime) / 1_000_000.0 + " ms");

        // correctness check
        if (internalMap.size() != internalExpirationMap.size()) {
             throw new RuntimeException("Map sizes do not match after syncMaps: " + internalMap.size() + " vs " + internalExpirationMap.size());
        }

        // verify keys are removed
        // we removed "key" + i from internalMap for i in [0, size/10)
        // syncMaps should remove them from internalExpirationMap
        for (int i = 0; i < size / 10; i++) {
             if (internalExpirationMap.containsKey("key" + i)) {
                 throw new RuntimeException("Key " + i + " should have been removed from expirationMap");
             }
        }
    }
}
