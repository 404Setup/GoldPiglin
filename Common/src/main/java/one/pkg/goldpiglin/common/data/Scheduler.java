package one.pkg.goldpiglin.common.data;


import one.pkg.tinyutils.jvm.JVMThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(JVMThread.newVirtualThreadFactoryOrDefault());
    private static final ExecutorService asyncExecutor = Executors.newCachedThreadPool(JVMThread.newVirtualThreadFactoryOrDefault());
    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(JVMThread.newVirtualThreadFactoryOrDefault());

    private Scheduler() {
    }

    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return scheduledExecutor.schedule(runnable, delay, unit);
    }

    public static void execute(Runnable runnable) {
        executor.submit(runnable);
    }

    public static void asyncExecute(Runnable runnable) {
        asyncExecutor.submit(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
        asyncExecutor.shutdown();
        scheduledExecutor.shutdown();
    }
}
