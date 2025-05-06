package one.tranic.goldpiglin.common.data;

import one.tranic.t.thread.T2hread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(T2hread.newVirtualThreadFactoryOrDefault());
    private static final ExecutorService asyncExecutor = Executors.newFixedThreadPool(3, T2hread.newVirtualThreadFactoryOrDefault());

    private Scheduler() {}

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
