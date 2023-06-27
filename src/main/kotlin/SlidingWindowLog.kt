import java.util.*

class SlidingWindowLog constructor(maxRequestPerSec: Int) : RateLimiter(maxRequestPerSec) {
    private val windowLog: Queue<Long> = LinkedList()
    override fun allow(): Boolean {
        val now = System.currentTimeMillis()
        val boundary = now - 1000
        synchronized(windowLog) {
            while (!windowLog.isEmpty() && windowLog.element() <= boundary) {
                windowLog.poll()
            }
            windowLog.add(now)
            println("current time=$now, log size=${windowLog.size}")
            return windowLog.size <= maxRequestPerSec
        }
    }
}