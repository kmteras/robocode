package `as`.ter

import robocode.AdvancedRobot
import java.awt.Color

class TerasBot() : AdvancedRobot() {
    private fun init() {
        setBodyColor(Color(0, 0, 255))
    }

    override fun run() {
        init()
        println("test")
    }
}
