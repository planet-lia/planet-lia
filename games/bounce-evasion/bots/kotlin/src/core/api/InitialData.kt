package core.api

import core.MatchDetails

class InitialData(
        var __uid: Int,
        var __matchDetails: MatchDetails,
        var sawSpawnDelay: Int,
        var mapWidth: Int,
        var mapHeight: Int,
        var map: Array<BooleanArray>,
        var boxSize: Int,
        var yourUnit: Unit,
        var opponentUnit: Unit,
        var saws: Array<Saw>
)
