package `as`.ter.components

import `as`.ter.TerasBot
import `as`.ter.events.EnemyFiredBulletEvent
import robocode.*
import java.awt.Graphics2D

open class BaseComponent(val robot: TerasBot) {
    open fun onScannedRobot(event: ScannedRobotEvent) {}
    open fun onBulletHit(event: BulletHitEvent) {}
    open fun onBulletMissed(event: BulletMissedEvent) {}
    open fun onBulletHitBullet(event: BulletHitBulletEvent) {}
    open fun onHitByBullet(event: HitByBulletEvent) {}
    open fun onEnemyFiredBullet(event: EnemyFiredBulletEvent) {}
    open fun onHitRobot(event: HitRobotEvent) {}
    open fun onRoundEnded(event: RoundEndedEvent) {}
    open fun onWin(event: WinEvent) {}
    open fun onPaint(g: Graphics2D) {}
}
