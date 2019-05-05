package `as`.ter.components

import `as`.ter.TerasBot
import `as`.ter.Util
import `as`.ter.Wave
import `as`.ter.events.EnemyFiredBulletEvent
import robocode.BulletHitEvent
import robocode.HitByBulletEvent
import robocode.ScannedRobotEvent
import robocode.util.Utils
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

open class MovementComponent(robot: TerasBot) : BaseComponent(robot) {
    private var waves = ArrayList<Wave>()
    private val wallSpace = 50.0

    private val gf = ArrayList<Double>(49)
    private val directionHistory = ArrayList<Int>()
    private val directionAngleHistory = ArrayList<Double>()

    init {
        for (i in 0 until 49) {
            gf.add(0.0)
        }
    }

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

        val vel = robot.velocity * Math.sin(event.bearingRadians)
        val bearing = event.bearingRadians + robot.headingRadians

        if (vel >= 0) {
            directionHistory.add(0, 1)
        } else {
            directionHistory.add(0, -1)
        }

        directionAngleHistory.add(0, bearing + Math.PI)

        if (energyDelta in 0.1..3.0 && directionHistory.size > 2) {
            val estimatedVelocity = Util.bulletVelocity(energyDelta)
            val estimatedPosition = Util.positionByBearing(robot.x, robot.y, robot.lastRobotHeading + robot.lastBearing, robot.lastDistance)
            val wave = Wave(
                    estimatedPosition.first,
                    estimatedPosition.second,
                    estimatedVelocity,
                    event.time,
                    robot.lastRobotX,
                    robot.lastRobotY,
                    directionHistory[2],
                    directionAngleHistory[2])
            robot.onEnemyBulletFired(EnemyFiredBulletEvent())
            waves.add(wave)
            println("Fired with " + (robot.lastEnergy - event.energy))
        }

        removePassedWaves()
        surf()
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
        if (dw != null) {
            val dangerWaveSize = dw.currentSize(robot.time) * 2
            g.color = Color(255, 255, 0, 200)
            g.drawArc((dw.x - dangerWaveSize / 2).toInt(), (dw.y - dangerWaveSize / 2).toInt(), dangerWaveSize.toInt(), dangerWaveSize.toInt(), 0, 360)
        }
    }

    private fun analyzeWave(w: Wave, x: Double, y: Double) {
        val index = getGFIndex(w, x, y)

        for (i in 0 until gf.size) {
            gf[i] += 1.0 / (Math.pow((index - i).toDouble(), 2.0) + 1)
        }
    }

    private fun getGFIndex(w: Wave, x: Double, y: Double): Int {
        val dif = Util.angleDifference(w.x, w.y, x, y) - w.robotAngle
        val factor = Utils.normalRelativeAngle(dif) / Util.maxEscapeAngle(w.velocity) * w.robotDirection

        val index = factor * (gf.size - 1) / 2 + (gf.size - 1) / 2

        return Math.min(0, Math.max(index.toInt(), gf.size - 1))
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

    private fun danger(w: Wave, direction: Int): Double {
        val pos = calculatePosition(w, direction)
        return gf[getGFIndex(w, pos.first, pos.second)]
    }

    private fun surf() {
        val dw = dangerWave() ?: return

        val dl = danger(dw, -1)
        val dr = danger(dw, 1)

        var angle = Util.angleDifference(dw.x, dw.y, robot.x, robot.y)
        var direction = 1

        if (dl > dr) {
            val smoothing = wallSmoothing(1, angle + Math.PI / 2, robot.x, robot.y)
            angle = smoothing.first
            direction = smoothing.second
        }
        else {
            val smoothing = wallSmoothing(-1, angle - Math.PI / 2, robot.x, robot.y)
            angle = smoothing.first
            direction = smoothing.second
        }

        val normalAngle = Utils.normalRelativeAngle(angle - robot.headingRadians)

        if (direction > 1) {
            if (normalAngle < 0) {
                robot.setTurnRightRadians(Math.PI + normalAngle)
            }
            else {
                robot.setTurnRightRadians(Math.PI - normalAngle)
            }

            robot.setAhead(100.0)
        }
        else {
            if (normalAngle < 0) {
                robot.setTurnRightRadians(-1 * normalAngle)
            }
            else {
                robot.setTurnRightRadians(normalAngle)
            }

            robot.setBack(100.0)
        }
    }

    private fun waveHitOrPassed(w: Wave, time: Long = robot.time, x: Double = robot.x, y: Double = robot.y): Boolean {
        return w.distance(x, y, time) < -50
    }

    private fun wallSmoothing(direction: Int, absoluteBearing: Double, x: Double, y: Double): Pair<Double, Int> {
        var moveDirection = absoluteBearing - Math.PI / 2 * direction

        val fieldRect = Rectangle2D.Double(wallSpace, wallSpace, robot.battleFieldWidth - wallSpace, robot.battleFieldHeight - wallSpace)

        var iterations = 0

        while (!fieldRect.contains(x + Math.sin(moveDirection) * 120, y + Math.cos(moveDirection) * 120) && iterations < 50) {
            moveDirection += direction * 0.1
            iterations++
        }

        var turn = Utils.normalRelativeAngle(moveDirection - robot.headingRadians)
        var forward = -1

        if (Math.abs(turn) > Math.PI / 2) {
            turn = Utils.normalRelativeAngle(turn + Math.PI)
        } else {
            forward = 1
        }

        return Pair(turn, forward)
    }

    private fun calculatePosition(w: Wave, direction: Int): Pair<Double, Double> {
        var posX = robot.x
        var posY = robot.y

        var moveVelocity = robot.velocity
        var moveHeading = robot.headingRadians
        var moveAngle: Double
        var moveDirection: Int
        var maxTurning: Double

        var future = 0
        var hit = false

        while (!hit && future < 300) {
            val smoothing = wallSmoothing(direction, Util.angleDifference(w.x, w.y, posX, posY) + (direction * Math.PI / 2), posX, posY)
            moveAngle = smoothing.first - moveHeading
            moveDirection = smoothing.second

            maxTurning = Math.PI / 720.0 * (40.0 - 3.0 * Math.abs(moveVelocity))
            moveHeading = Utils.normalRelativeAngle(moveHeading + Math.min(-maxTurning, Math.max(moveAngle, maxTurning)))

            var moveVelocityDirection = moveDirection

            if (moveVelocity < 0) {
                moveVelocityDirection = 2 * moveDirection
            }

            moveVelocity += moveVelocity * moveVelocityDirection

            posX += Math.sin(moveHeading) * moveVelocity
            posY += Math.cos(moveHeading) * moveVelocity

            future++

            hit = waveHitOrPassed(w, robot.time + future, posX, posY)
        }

        return Pair(posX, posY)
    }

    private fun dangerWave(): Wave? {
        var future = 0

        while (future < 300) {
            waves.forEach {
                if (waveHitOrPassed(it, robot.time + future)) {
                    return it
                }
            }
            future++
        }

        return if (waves.size > 0) {
            waves[0]
        } else {
            null
        }
    }
}
