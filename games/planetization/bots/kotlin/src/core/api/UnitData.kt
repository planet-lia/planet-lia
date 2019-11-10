package core.api

class UnitData(
        var id: Int,
        var type: UnitType,
        var x: Float,
        var y: Float,
        var rotation: Float,
        var health: Float,
        var currentPlanetId: Int,
        var destinationPlanetId: Int
)
