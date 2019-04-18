package `as`.ter.components

import robocode.AdvancedRobot
import robocode.BulletHitEvent

open class MovementComponent(robot: AdvancedRobot) : BaseComponent(robot) {
    override fun onBulletHit(event: BulletHitEvent) {
        robot.setAhead(100.0)
        robot.setFire(100.0)
    }
}
