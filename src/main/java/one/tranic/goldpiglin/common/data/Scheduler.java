package one.tranic.goldpiglin.common.data;

import java.util.concurrent.*;

public class Scheduler {
    private static final ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
    private static final ExecutorService singleExecutor = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(15),
            Thread.ofVirtual().factory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void execute(Runnable runnable) {
        exec.submit(runnable);
    }

    public static void singleExecute(Runnable runnable) {
        singleExecutor.submit(runnable);
    }

    public static void shutdown() {
        exec.shutdown();
        singleExecutor.shutdown();
    }
}
