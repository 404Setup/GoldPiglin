package one.tranic.goldpiglin.data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    private static final ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();

    public static void execute(Runnable runnable) {
        exec.submit(runnable);
    }

    public static void shutdown() {
        exec.shutdown();
    }
}
