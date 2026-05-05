package one.pkg.goldpiglin.common.data;


import one.pkg.tinyutils.jvm.JVMThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(JVMThread.newVirtualThreadFactoryOrDefault());
    private static final ExecutorService asyncExecutor = Executors.newCachedThreadPool(JVMThread.newVirtualThreadFactoryOrDefault());

    private Scheduler() {
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
    }
}
