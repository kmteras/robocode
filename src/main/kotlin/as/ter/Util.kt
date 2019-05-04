package `as`.ter

class Util {
    companion object {
        fun positionByBearing(x: Double, y: Double, bearing: Double, distance: Double): Pair<Double, Double> {
            return Pair(x + distance * Math.sin(bearing), y + distance * Math.cos(bearing))
        }

        fun bulletVelocity(firepower: Double): Double {
            return 20 - 3 * firepower
        }

        fun positionsDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
            return vectorLength(x1 - x2, y1 - y2)
        }

        fun angleDifference(x1: Double, y1: Double, x2: Double, y2: Double): Double {
            return Math.atan2(x1 - x2, y1 - y2)
        }

        fun vectorLength(vx: Double, vy: Double): Double {
            return Math.sqrt(Math.pow(vx, 2.0) + Math.pow(vy, 2.0))
        }
    }
}
