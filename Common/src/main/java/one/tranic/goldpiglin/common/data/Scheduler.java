package one.tranic.goldpiglin.common.data;

import one.tranic.t.thread.T2hread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    private static final ExecutorService executor = Executors.newCachedThreadPool(T2hread.newVirtualThreadFactoryOrDefault());

    public static void execute(Runnable runnable) {
        executor.submit(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
