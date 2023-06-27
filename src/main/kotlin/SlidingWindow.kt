import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

class SlidingWindow constructor(maxRequestPerSec: Int, private val windowSizeInMs: Int) :
    RateLimiter(maxRequestPerSec) {
    private val windows: ConcurrentMap<Long, AtomicInteger> = ConcurrentHashMap()
    override fun allow(): Boolean {
        val now = System.currentTimeMillis()
        val curWindowKey = now / windowSizeInMs
        windows.putIfAbsent(curWindowKey, AtomicInteger(0))
        val preWindowKey = curWindowKey - 1000
        val preCount: AtomicInteger = windows.getOrDefault(preWindowKey, AtomicInteger(0))
        val preWeight = 1 - (now - curWindowKey) / 1000.0
        val count = (preCount.get() * preWeight + windows[curWindowKey]?.incrementAndGet()!!)
        return count <= maxRequestPerSec
    }
}