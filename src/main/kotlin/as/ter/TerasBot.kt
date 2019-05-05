package `as`.ter

import `as`.ter.components.BaseComponent
import `as`.ter.components.GunComponent
import `as`.ter.components.MovementComponent
import `as`.ter.components.RadarComponent
import `as`.ter.events.EnemyFiredBulletEvent
import robocode.*
import java.awt.Color
import java.awt.Graphics2D

class TerasBot : AdvancedRobot() {
    private fun init() {
        val blue = Color(0, 114, 206)
        val black = Color(0, 0, 0)
        val white = Color(255, 255, 255)
        setRadarColor(black)
        setGunColor(black)
        setBodyColor(blue)
        setScanColor(white)
        isAdjustGunForRobotTurn = true
        isAdjustRadarForGunTurn = true
    }

    private var components = ArrayList<BaseComponent>()
    var lastEnergy: Double = 0.0
    var lastVelocity: Double = 0.0
    var lastBearing: Double = 0.0
    var lastHeading: Double = 0.0
    var lastDistance: Double = 0.0

    var energyDelta: Double = 0.0

    var lastRobotHeading: Double = 0.0
    var lastRobotX: Double = 0.0
    var lastRobotY: Double = 0.0

    override fun run() {
        init()

        components.add(RadarComponent(this))
        components.add(MovementComponent(this))
        components.add(GunComponent(this))

        turnRadarRightRadians(Double.POSITIVE_INFINITY)
        do {
            scan()
            execute()
        } while (true)
    }

    override fun onScannedRobot(event: ScannedRobotEvent?) {
        super.onScannedRobot(event)

        if (event == null) {
            return
        }

        components.forEach {
            it.onScannedRobot(event)
        }

        lastEnergy = event.energy
        lastVelocity = event.velocity
        lastBearing = event.bearingRadians
        lastHeading = event.headingRadians
        lastDistance = event.distance

        lastRobotHeading = headingRadians
        lastRobotX = x
        lastRobotY = y
        energyDelta = 0.0
    }

    fun onEnemyBulletFired(event: EnemyFiredBulletEvent?) {
        //TODO: implement
    }

    override fun onBulletHit(event: BulletHitEvent?) {
        super.onBulletHit(event)

        if (event == null) {
            return
        }

        components.forEach {
            it.onBulletHit(event)
        }
    }

    override fun onHitByBullet(event: HitByBulletEvent?) {
        super.onHitByBullet(event)
        if (event == null) {
            return
        }

        components.forEach {
            it.onHitByBullet(event)
        }
    }

    override fun onBulletMissed(event: BulletMissedEvent?) {
        super.onBulletMissed(event)
    }

    override fun onBulletHitBullet(event: BulletHitBulletEvent?) {
        super.onBulletHitBullet(event)
    }

    override fun onRoundEnded(event: RoundEndedEvent?) {
        super.onRoundEnded(event)
    }

    override fun onWin(event: WinEvent?) {
        super.onWin(event)
    }

    override fun onHitRobot(event: HitRobotEvent?) {
        super.onHitRobot(event)
    }

    override fun onStatus(e: StatusEvent?) {
        super.onStatus(e)
    }

    override fun onPaint(g: Graphics2D?) {
        super.onPaint(g)

        if (g == null) {
            return
        }

        components.forEach {
            it.onPaint(g)
        }
    }
}
