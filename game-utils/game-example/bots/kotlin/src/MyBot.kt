import core.*
import core.api.*

/**
 * The default Kotlin bot implementation that is not specific
 * to any Planet Lia game but instead serves as a base for
 * all game specific bot implementations.
 */
class MyBot : Bot {

    // Called only once before the match starts. It holds the
    // data that you may need before the game starts.
    override fun setup(data: InitialData) {
        // TODO
        // This is the default bot that is not configured
        // for any specific game.
        // See how to support a specific game:
        // - <TODO_LINK>
        println(data)
    }

    // Called repeatedly while the match is generating. Each
    // time you receive the current match state and can use
    // response object to issue your commands.
    override fun update(state: MatchState, response: Response) {
        // TODO
        // This is the default bot that is not configured
        // for any specific game.
        // See how to support a specific game:
        // - <TODO_LINK>
        println(state)
    }

    // Connects your bot to match generator, don't change it.
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
        }
    }
}
