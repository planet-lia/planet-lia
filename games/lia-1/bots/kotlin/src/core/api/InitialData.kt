package core.api

import core.MatchDetails

class InitialData(
        var __uid: Int,
        var __matchDetails: Array<MatchDetails>,
        var constants: Constants
) {
    fun getUid(): Int {
        return __uid
    }
}
