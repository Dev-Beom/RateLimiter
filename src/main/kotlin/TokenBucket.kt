import kotlin.math.min

class TokenBucket(
    maxRequestPerSec: Int,
    private var tokens: Int,
    private var capacity: Int,
    private var lastRefillTime: Long
) : RateLimiter(maxRequestPerSec) {
    constructor(maxRequestPerSec: Int) : this(
        maxRequestPerSec,
        maxRequestPerSec,
        maxRequestPerSec,
        scaledTime()
    )

    override fun allow(): Boolean {
        synchronized(this) {
            refillTokens()
            if (this.tokens == 0) {
                return false
            }
            tokens--
            return true
        }
    }

    private fun refillTokens() {
        val now = scaledTime()
        if (now > this.lastRefillTime) {
            val elapsedTime = (now - lastRefillTime)
            val refill = (elapsedTime * maxRequestPerSec).toInt()
            tokens = min(tokens + refill, capacity)
            lastRefillTime = now
        }
    }

    companion object {
        private fun scaledTime(): Long {
            return System.currentTimeMillis() / 1000
        }
    }
}