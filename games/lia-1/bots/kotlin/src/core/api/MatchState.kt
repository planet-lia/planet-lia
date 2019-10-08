package core.api

class MatchState(
        var __uid: Int,
        var time: Float,
        var numberOfOpponentUnits: Int,
        var resources: Int,
        var canSaySomething: Boolean,
        var units: Array<UnitData>
) {

    fun getUid(): Int {
        return __uid;
    }
}

class UnitData(
        var id: Int,
        var type: UnitType,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientationAngle: Float,
        var speed: Speed,
        var rotation: Rotation,
        var canShoot: Boolean,
        var nBullets: Int,
        var opponentsInView: Array<OpponentInView>,
        var opponentBulletsInView: Array<BulletInView>,
        var resourcesInView: Array<ResourceInView>,
        var navigationPath: Array<Point>
)

enum class Speed {
    NONE, FORWARD, BACKWARD
}

enum class Rotation {
    NONE, LEFT, RIGHT, SLOW_LEFT, SLOW_RIGHT
}

enum class UnitType {
    WORKER, WARRIOR
}

class OpponentInView(
        var id: Int,
        var type: UnitType,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientationAngle: Float,
        var speed: Speed,
        var rotation: Rotation
)

class BulletInView(
        var x: Float,
        var y: Float,
        var orientation: Float,
        var velocity: Float
)

class ResourceInView(
        var x: Float,
        var y: Float
)

class Point(
        var x: Float,
        var y: Float
)
