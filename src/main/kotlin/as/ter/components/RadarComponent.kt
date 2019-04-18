package `as`.ter.components

import robocode.AdvancedRobot
import robocode.ScannedRobotEvent
import robocode.util.Utils

open class RadarComponent(robot: AdvancedRobot) : BaseComponent(robot) {
    override fun onScannedRobot(event: ScannedRobotEvent) {
        super.onScannedRobot(event)
        robot.setTurnRadarRightRadians(
                1.9 * Utils.normalRelativeAngle(robot.headingRadians + event.bearingRadians - robot.radarHeadingRadians))
    }
}
