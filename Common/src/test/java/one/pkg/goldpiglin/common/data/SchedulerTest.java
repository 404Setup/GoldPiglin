package one.pkg.goldpiglin.common.data;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulerTest {

    @Test
    void testAsyncExecutorConcurrency() throws InterruptedException {
        int numberOfTasks = 10;
        CountDownLatch startLatch = new CountDownLatch(numberOfTasks);
        CountDownLatch finishLatch = new CountDownLatch(1);
        AtomicInteger runningTasks = new AtomicInteger(0);

        for (int i = 0; i < numberOfTasks; i++) {
            Scheduler.asyncExecute(() -> {
                runningTasks.incrementAndGet();
                startLatch.countDown();
                try {
                    if (!finishLatch.await(5, TimeUnit.SECONDS)) {
                        System.err.println("Task timed out waiting for finishLatch");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        boolean allStarted = startLatch.await(5, TimeUnit.SECONDS);

        assertTrue(allStarted, "Not all tasks started concurrently. Potential thread pool exhaustion or limit. Running tasks: " + runningTasks.get());
        assertEquals(numberOfTasks, runningTasks.get(), "Should have " + numberOfTasks + " tasks running.");

        finishLatch.countDown();
    }
}
