package core.api

import core.MatchDetails
import java.util.ArrayList

class InitialData(
        var __uid: Int,
        var __matchDetails: MatchDetails,
        var mapWidth: Int,
        var mapHeight: Int,
        var unitSize: Float,
        var planetDiameter: Float,
        var unitSpeed: Float,
        var unitRotationSpeed: Float,
        var numberOfWorkersOnStart: Int,
        var unitCost: Int,
        var resourceGenerationSpeed: Float,
        var maxActiveWorkersPerPlanet: Int,
        var maxNumberOfUnitsPerTeam: Int,
        var maxMatchDuration: Float,
        var workerHealth: Float,
        var workerAttack: Float,
        var warriorHealth: Float,
        var warriorAttack: Float,
        var damageReductionRatioOnDefence: Float,

        var yourPlanets: ArrayList<PlanetData>,
        var freePlanets: ArrayList<PlanetData>,
        var opponentPlanets: ArrayList<PlanetData>,
        var yourWorkers: ArrayList<UnitData>,
        var opponentWorkers: ArrayList<UnitData>,
        var yourWarriors: ArrayList<UnitData>,
        var opponentWarriors: ArrayList<UnitData>
)
