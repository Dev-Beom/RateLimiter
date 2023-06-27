class LeakyBucket(
    maxRequestPerSec: Int
) :
    RateLimiter(maxRequestPerSec) {
    private val capacity: Long
    private var used: Long = 0
    private val leakInterval: Long
    private var lastLeakTime: Long

    init {
        capacity = maxRequestPerSec.toLong()
        leakInterval = (1000 / maxRequestPerSec).toLong()
        lastLeakTime = System.currentTimeMillis()
    }

    override fun allow(): Boolean {
        leak()
        synchronized(this) {
            used++
            return if (used >= capacity) {
                false
            } else true
        }
    }

    private fun leak() {
        val now = System.currentTimeMillis()
        if (now > lastLeakTime) {
            val millisSinceLastLeak = now - lastLeakTime
            val leaks = millisSinceLastLeak / leakInterval
            if (leaks > 0) {
                if (used <= leaks) {
                    used = 0
                } else {
                    used -= leaks.toInt().toLong()
                }
                lastLeakTime = now
            }
        }
    }
}