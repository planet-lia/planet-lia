package core.api

import java.util.HashMap

class MatchState : HashMap<String, Any>() {
    // TODO remove extends HashMap and add fields specific to your game

    // TODO If you provided your own implementation for the game then create
    //      normal __uid field and return it here
    fun getUid(): Int {
        return (get("__uid") as Double).toInt()
    }
}
