package core.api

import java.util.ArrayList

class PlanetData(
    var id: Int,
    var owner: Owner,
    var x: Float,
    var y: Float,
    var idsOfUnitsOnPlanet: ArrayList<Int>,
    var resources: Float,
    var canSpawnNewUnit: Boolean
)

enum class Owner {
    NONE, RED, GREEN
}
