package core.api

import core.MatchDetails

class InitialData(
        var __uid: Int,
        var __matchDetails: MatchDetails,
        var mapWidth: Int,
        var mapHeight: Int,
        var sawSpawnDelay: Int,
        var map: Array<BooleanArray>,
        var yourUnit: Unit,
        var opponentUnit: Unit,
        var coins: Array<Coin>,
        var saws: Array<Saw>
)
