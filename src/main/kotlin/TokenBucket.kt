import kotlin.math.min

class TokenBucket(private var tokens: Int) : RateLimiter(tokens) {
    private val capacity: Int = tokens
    private var lastRefillTime: Long

    init {
        lastRefillTime = scaledTime()
    }

    override fun allow(): Boolean {
        synchronized(this) {
            refillTokens()
            if (tokens == 0) {
                return false
            }
            tokens--
            return true
        }
    }

    private fun refillTokens() {
        val now = scaledTime()
        if (now > lastRefillTime) {
            val elapsedTime = (now - lastRefillTime).toDouble()
            val refill = (elapsedTime * tokens).toInt()
            tokens = min(tokens + refill, capacity)
            lastRefillTime = now
        }
    }

    private fun scaledTime(): Long {
        return System.currentTimeMillis() / 1000
    }
}