package core.api

interface Command

class Response {
    var __uid: Int = 0
    private val commands = ArrayList<Command>()

    fun spawnUnit(planetId: Int, type: UnitType) {
        commands.add(SpawnUnitCommand(planetId, type))
    }

    fun sendUnit(unitId: Int, destinationPlanetId: Int) {
        commands.add(SendUnitCommand(unitId, destinationPlanetId))
    }
}

class SendUnitCommand(var unitId: Int, var destinationPlanetId: Int) : Command {
    var __type = this.javaClass.simpleName
}

class SpawnUnitCommand(var planetId: Int, var type: UnitType) : Command {
    var __type = this.javaClass.simpleName
}

enum class UnitType {
    WORKER, WARRIOR
}
