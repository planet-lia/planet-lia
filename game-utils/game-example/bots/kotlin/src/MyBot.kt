import com.google.gson.Gson
import core.*
import core.api.*

/**
 * Example Kotlin bot implementation for Planet Lia game-example.
 * To change this so that it can be used in some other Planet Lia game:
 * 1. Change InitialData, MatchState and Response objects to fit
 *   the API format of that game
 * 2. Update the basic MyBot implementation with basic bot logic
 */
class MyBot : Bot {

    lateinit var data: InitialData;

    // Called only once before the match starts. It holds the
    // data that you may need before the game starts.
    override fun setup(data: InitialData) {
        println(Gson().toJson(data))
        this.data = data
        println("There are ${data.__matchDetails.botsDetails.size} bots in the match.")
    }

    // Called repeatedly while the match is generating. Each
    // time you receive the current match state and can use
    // response object to issue your commands.
    override fun update(state: MatchState, response: Response) {
        println(Gson().toJson(state))

        // Move in one of the four directions every update call
        val randomNumber = Math.random()
        when {
            randomNumber < 0.25 -> response.moveUnit(Direction.LEFT)
            randomNumber < 0.5 -> response.moveUnit(Direction.RIGHT)
            randomNumber < 0.75 -> response.moveUnit(Direction.UP)
            else -> response.moveUnit(Direction.DOWN)
        }
    }

    // Connects your bot to match generator, don't change it.
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
        }
    }
}
