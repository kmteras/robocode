package `as`.ter.components

import `as`.ter.TerasBot
import `as`.ter.Util
import `as`.ter.Wave
import `as`.ter.events.EnemyFiredBulletEvent
import robocode.BulletHitEvent
import robocode.HitByBulletEvent
import robocode.ScannedRobotEvent
import java.awt.Color
import java.awt.Graphics2D

open class MovementComponent(robot: TerasBot) : BaseComponent(robot) {
    var waves = ArrayList<Wave>()

    override fun onHitByBullet(event: HitByBulletEvent) {
        robot.energyDelta += event.power
        println("Got hit: " + event.power)

        if (waves.size > 0) {
            var closestWaveIndex = 0
            var closestWaveDistance = Double.MAX_VALUE

            waves.forEachIndexed { i, it ->
                val distance = it.distance(event.bullet.x, event.bullet.y, robot.time)

                if (distance < closestWaveDistance) {
                    closestWaveDistance = distance
                    closestWaveIndex = i
                }
            }


            // TODO: compare current and shot heading and add to history
            waves.removeAt(closestWaveIndex)
        }
    }

    override fun onBulletHit(event: BulletHitEvent) {
        robot.setAhead(100.0)
        robot.setFire(100.0)
    }

    override fun onScannedRobot(event: ScannedRobotEvent) {
        super.onScannedRobot(event)

        val currentEnergy = event.energy - robot.energyDelta

        if (robot.lastEnergy - currentEnergy > 0) {
            val esimatedVelocity = Util.bulletVelocity(robot.lastEnergy - event.energy)
            val estimatedPosition = Util.positionByBearing(robot.x, robot.y, robot.lastRobotHeading + robot.lastBearing, robot.lastDistance)
            val wave = Wave(estimatedPosition.first, estimatedPosition.second, esimatedVelocity, robot.time)
            println(robot.lastEnergy - event.energy)
            robot.onEnemyBulletFired(EnemyFiredBulletEvent())
            waves.add(wave)
            println("Fired")
        }
    }

    override fun onPaint(g: Graphics2D) {
        super.onPaint(g)
        g.color = Color(0, 150, 100, 100)
        val position = Util.positionByBearing(robot.x, robot.y, robot.lastRobotHeading + robot.lastBearing, robot.lastDistance)
        g.fillArc(position.first.toInt() - 20, position.second.toInt() - 20, 40, 40, 0, 360)
        g.fillArc(robot.x.toInt() - 20, robot.y.toInt() - 20, 40, 40, 0, 360)
        g.drawLine(robot.x.toInt(), robot.y.toInt(), position.first.toInt(), position.second.toInt())

        g.color = Color(255, 0, 0, 200)
        for (w: Wave in waves) {
            val currentSize = w.currentSize(robot.time)
            g.drawArc((w.x - currentSize / 2).toInt(), (w.y - currentSize / 2).toInt(), currentSize.toInt(), currentSize.toInt(), 0, 360)
        }
    }
}
