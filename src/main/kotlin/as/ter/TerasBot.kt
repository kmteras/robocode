package `as`.ter

import `as`.ter.components.BaseComponent
import `as`.ter.components.MovementComponent
import `as`.ter.components.RadarComponent
import `as`.ter.events.EnemyFiredBulletEvent
import robocode.*
import java.awt.Color

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

    private var botFound = false
    private var botDissapeared = true
    private val foundTurnTate = 1.0

    private var components = ArrayList<BaseComponent>()

    override fun run() {
        init()

        components.add(RadarComponent(this))
        components.add(MovementComponent(this))

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

        botFound = true

        fire(1.0)
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
}
