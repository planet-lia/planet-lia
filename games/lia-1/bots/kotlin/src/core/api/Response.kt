package core.api

import java.util.ArrayList

interface Command

class Response {
    var __uid: Int = 0
    private val commands = ArrayList<Command>()

    /** Change thrust speed of a unit  */
    fun setSpeed(unitId: Int, speed: Speed) {
        commands.add(SpeedEvent(unitId, speed))
    }

    /** Change rotation speed of a unit  */
    fun setRotation(unitId: Int, rotation: Rotation) {
        commands.add(RotationEvent(unitId, rotation))
    }

    /** Make a unit shoot  */
    fun shoot(unitId: Int) {
        commands.add(ShootEvent(unitId))
    }

    /** Start navigation  */
    fun navigationStart(unitId: Int, x: Float, y: Float, moveBackwards: Boolean) {
        commands.add(NavigationStartEvent(unitId, x, y, moveBackwards))
    }

    fun navigationStart(unitId: Int, x: Float, y: Float) {
        commands.add(NavigationStartEvent(unitId, x, y, false))
    }

    /** Stop navigation  */
    fun navigationStop(unitId: Int) {
        commands.add(NavigationStopEvent(unitId))
    }

    /** Make your unit say something  */
    fun saySomething(unitId: Int, text: String) {
        commands.add(SaySomethingEvent(unitId, text))
    }

    fun spawnUnit(type: UnitType) {
        commands.add(SpawnUnitEvent(type))
    }
}

class SpeedEvent(var unitId: Int, var speed: Speed) : Command {
    var __type = SpeedEvent::class.java.simpleName
}

class SpawnUnitEvent(var type: UnitType) : Command {
    var __type = SpawnUnitEvent::class.java.simpleName
}

class ShootEvent(var unitId: Int) : Command {
    var __type = ShootEvent::class.java.simpleName
}

class RotationEvent(var unitId: Int, var rotation: Rotation) : Command {
    var __type = RotationEvent::class.java.simpleName
}

class SaySomethingEvent(var unitId: Int, var text: String) : Command {
    var __type = SaySomethingEvent::class.java.simpleName
}

class NavigationStopEvent(var unitId: Int) : Command {
    var __type = NavigationStopEvent::class.java.simpleName
}

class NavigationStartEvent(var unitId: Int, var x: Float, var y: Float, var moveBackwards: Boolean) : Command {
    var __type = NavigationStartEvent::class.java.simpleName
}