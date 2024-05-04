import java.util.concurrent.Executors

object Scheduler {
    private val dispatcher = Executors.newVirtualThreadPerTaskExecutor()

    fun sendTask(block: Runnable) {
        dispatcher.submit(block)
    }

    fun shutdown() {
        dispatcher.shutdown()
    }
}