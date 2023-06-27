abstract class RateLimiter(
    val maxRequestPerSec: Int
) {
    abstract fun allow(): Boolean
}