package stream.fset.plugin.goldpiglin

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Scheduler {
    companion object {
        private val pool: ExecutorService = Executors.newCachedThreadPool()

        fun sendTask(r: Runnable) {
            pool.submit(r)
        }

        fun shutdown() {
            pool.shutdownNow()
        }
    }

}
