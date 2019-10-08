package core.api

import java.util.HashMap

class InitialData(
        // TODO If you provided your own implementation for the game, uncomment this:
        // var __matchDetails: Array<MatchDetails>
) : HashMap<String, Any>() {
    // TODO remove extends HashMap and add fields specific to your game

    // TODO If you provided your own implementation for the game then create
    //      normal __uid field and return it here
    fun getUid(): Int {
        return (get("__uid") as Double).toInt()
    }
}
