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
    var enemyShootAngles = ArrayList<Double>()

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

            analyzeWave(waves[closestWaveIndex], robot.x, robot.y)
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
        val energyDelta = robot.lastEnergy - currentEnergy

        if (energyDelta in 0.1..3.0) {
            val estimatedVelocity = Util.bulletVelocity(energyDelta)
            val estimatedPosition = Util.positionByBearing(robot.x, robot.y, robot.lastRobotHeading + robot.lastBearing, robot.lastDistance)
            val wave = Wave(estimatedPosition.first, estimatedPosition.second, estimatedVelocity, event.time, robot.lastRobotX, robot.lastRobotY)
            robot.onEnemyBulletFired(EnemyFiredBulletEvent())
            waves.add(wave)
            println("Fired with " + (robot.lastEnergy - event.energy))
        }

        removePassedWaves()
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
            val currentSize = w.currentSize(robot.time) * 2
            g.drawArc((w.x - currentSize / 2).toInt(), (w.y - currentSize / 2).toInt(), currentSize.toInt(), currentSize.toInt(), 0, 360)
        }

        val dw = dangerWave()
        val dangerWaveSize = dw.currentSize(robot.time) * 2
        g.color = Color(255, 255, 0, 200)
        g.drawArc((dw.x - dangerWaveSize / 2).toInt(), (dw.y - dangerWaveSize / 2).toInt(), dangerWaveSize.toInt(), dangerWaveSize.toInt(), 0, 360)
    }

    private fun analyzeWave(w: Wave, x: Double, y: Double) {
        val angleDifference = Util.angleDifference(w.robotX, w.robotY, x, y)
        println(angleDifference)
        enemyShootAngles.add(angleDifference)
    }

    private fun removePassedWaves() {
        var i = 0
        while (i < waves.size) {
            if (waveHitOrPassed(waves[i])) {
                println("Dodged ${waves[i]}")
                waves.removeAt(i)
                i--
            }
            i++
        }
    }

    private fun waveHitOrPassed(w: Wave, time: Long = robot.time): Boolean {
        return w.distance(robot.x, robot.y, time) < -50
    }

    private fun calculatePosition() {

    }

    private fun wallSmoothing() {

    }

    private fun dangerWave(): Wave {
        var future = 0

        while (future < 300) {
            waves.forEach {
                if (waveHitOrPassed(it, robot.time + future)) {
                    return it
                }
            }
            future++
        }
        return waves[0]
    }
}
