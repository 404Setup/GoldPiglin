package one.pkg.goldpiglin.common.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.lang.ref.WeakReference;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ExpiringHashMapLeakTest {

    @AfterAll
    public static void tearDown() {
    }

    private WeakReference<ExpiringHashMap<String, String>> createAndGetWeakRef() {
        ExpiringHashMap<String, String> map = new ExpiringHashMap<>(5, 1);
        map.put("test", "data");
        return new WeakReference<>(map);
    }

    @Test
    public void testMapIsGarbageCollected() throws InterruptedException {
        WeakReference<ExpiringHashMap<String, String>> weakRef = createAndGetWeakRef();

        // Suggest garbage collection multiple times
        for (int i = 0; i < 5; i++) {
            System.gc();
            Thread.sleep(200);
            if (weakRef.get() == null) {
                break;
            }
        }

        // Weak reference should be null, meaning the object was garbage collected
        assertNull(weakRef.get(), "ExpiringHashMap instance was not garbage collected, indicating a memory leak.");
    }
}
