import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

class FixedWindowCounter constructor(maxRequestPerSec: Int, private val windowSizeInMs: Int) :
    RateLimiter(maxRequestPerSec) {
    private val windows: ConcurrentMap<Long, AtomicInteger> = ConcurrentHashMap()
    override fun allow(): Boolean {
        val windowKey = System.currentTimeMillis() / windowSizeInMs
        windows.putIfAbsent(windowKey, AtomicInteger(0))
        return windows[windowKey]?.incrementAndGet()?.let {
            it <= maxRequestPerSec
        } ?: false
    }

    override fun toString(): String {
        val sb = StringBuilder("")
        for ((key, value) in windows.entries) {
            sb.append(key)
            sb.append(" --> ")
            sb.append(value)
            sb.append("\n")
        }
        return sb.toString()
    }
}