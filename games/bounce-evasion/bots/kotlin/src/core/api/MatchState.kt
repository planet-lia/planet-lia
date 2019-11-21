package core.api

class MatchState(
    var __uid: Int,
    var time: Float,
    var yourUnit: Unit,
    var opponentUnit: Unit,
    var coins: Array<Coin>,
    var saws: Array<Saw>
)

class Unit(
    var x: Int,
    var y: Int,
    var points: Int,
    var lives: Int
)

class Coin(
        var x: Int,
        var y: Int
)

class Saw(
        var x: Int,
        var y: Int,
        var direction: SawDirection
)

enum class SawDirection {
    UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
}
