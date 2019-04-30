package `as`.ter

class Wave(val x: Double, val y: Double, val velocity: Double, val startTick: Long) {
    fun currentSize(tick: Long): Double {
        return 2 * velocity * (tick - (startTick - 1))
    }

    fun distance(x: Double, y: Double, tick: Long): Double {
        return Util.positionsDistance(this.x, this.y, x, y) - currentSize(tick)
    }
}
