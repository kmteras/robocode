package `as`.ter.components

import `as`.ter.events.EnemyFiredBulletEvent
import robocode.*

open class BaseComponent(val robot: AdvancedRobot) {
    open fun onScannedRobot(event: ScannedRobotEvent) {}
    open fun onBulletHit(event: BulletHitEvent) {}
    open fun onBulletMissed(event: BulletMissedEvent) {}
    open fun onBulletHitBullet(event: BulletHitBulletEvent) {}
    open fun onEnemyFiredBullet(event: EnemyFiredBulletEvent) {}
    open fun onHitRobot(event: HitRobotEvent) {}
    open fun onRoundEnded(event: RoundEndedEvent) {}
    open fun onWin(event: WinEvent) {}
}
