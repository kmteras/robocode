package `as`.ter.components

import `as`.ter.TerasBot
import robocode.ScannedRobotEvent
import robocode.util.Utils

open class GunComponent(robot: TerasBot) : BaseComponent(robot) {
    override fun onScannedRobot(event: ScannedRobotEvent) {
        super.onScannedRobot(event)
        robot.setTurnGunRightRadians(
                1.9 * Utils.normalRelativeAngle(robot.headingRadians + event.bearingRadians - robot.gunHeadingRadians))
        robot.fire(1.0)
    }
}
