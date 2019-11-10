package core.api

import java.util.ArrayList

class MatchState(var __uid: Int,
                 var time: Float,
                 var yourPlanets: ArrayList<PlanetData>,
                 var freePlanets: ArrayList<PlanetData>,
                 var opponentPlanets: ArrayList<PlanetData>,
                 var yourWorkers: ArrayList<UnitData>,
                 var opponentWorkers: ArrayList<UnitData>,
                 var yourWarriors: ArrayList<UnitData>,
                 var opponentWarriors: ArrayList<UnitData>
)
