package one.pkg.goldpiglin.common.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExpiringHashMapTest {

    private ExpiringHashMap<String, String> map;

    @BeforeEach
    void setUp() {
        map = new ExpiringHashMap<>(5, 1);
    }

    @Test
    void testEntrySetReturnsCorrectEntries() {
        map.put("key1", "value1");
        map.put("key2", "value2");

        Set<Map.Entry<String, String>> entries = map.entrySet();
        assertEquals(2, entries.size());

        boolean foundKey1 = false;
        boolean foundKey2 = false;

        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().equals("key1")) {
                assertEquals("value1", entry.getValue());
                foundKey1 = true;
            } else if (entry.getKey().equals("key2")) {
                assertEquals("value2", entry.getValue());
                foundKey2 = true;
            }
        }

        assertTrue(foundKey1);
        assertTrue(foundKey2);
    }

    @Test
    void testEntrySetExcludesExpiredEntries() throws InterruptedException {
        ExpiringHashMap<String, String> shortMap = new ExpiringHashMap<>(1, 1);
        shortMap.put("key1", "value1");

        Thread.sleep(1500);

        Set<Map.Entry<String, String>> entries = shortMap.entrySet();
        assertTrue(entries.isEmpty(), "Entry set should be empty after expiration");

        assertEquals(0, entries.size());
    }

    @Test
    void testEntrySetRemove() {
        map.put("key1", "value1");
        Set<Map.Entry<String, String>> entries = map.entrySet();

        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        assertTrue(iterator.hasNext());
        Map.Entry<String, String> entry = iterator.next();
        assertEquals("key1", entry.getKey());

        iterator.remove();
        assertTrue(map.isEmpty());
    }

    @Test
    void testEntrySetRemoveViaSet() {
        map.put("key1", "value1");
        Set<Map.Entry<String, String>> entries = map.entrySet();

        Map.Entry<String, String> entryToRemove = new ExpiringHashMap.SimpleEntry<>("key1", "value1");

        assertTrue(entries.remove(entryToRemove));
        assertTrue(map.isEmpty());
    }

    @Test
    void testEntrySetContains() {
        map.put("key1", "value1");
        Set<Map.Entry<String, String>> entries = map.entrySet();

        Map.Entry<String, String> entry = new ExpiringHashMap.SimpleEntry<>("key1", "value1");
        assertTrue(entries.contains(entry));

        Map.Entry<String, String> wrongEntry = new ExpiringHashMap.SimpleEntry<>("key1", "value2");
        assertFalse(entries.contains(wrongEntry));

        Map.Entry<String, String> wrongKey = new ExpiringHashMap.SimpleEntry<>("key2", "value1");
        assertFalse(entries.contains(wrongKey));
    }
}
