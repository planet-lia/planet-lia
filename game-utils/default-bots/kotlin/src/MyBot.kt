import core.*
import core.api.*

/**
 * The default Java bot implementation that is not specific
 * to any Planet Lia game but instead servers as a base for
 * all game specific bot implementations.
 */
class MyBot : Bot {

    // Called only once before the match starts. It holds the
    // data that you may need before the game starts.
    override fun setup(data: InitialData) {
        // TODO
        // This is the default Java bot version that is not
        // configured for any specific game.
        // Implement classes in "core/api" with game specific
        // data in order to use this language for a chosen game.
        // Check this guide: <TODO_LINK>
        println(data)
    }

    // Called repeatedly while the match is generating. Each
    // time you receive the current match state and can use
    // response object to issue your commands.
    override fun update(state: MatchState, response: Response) {
        // TODO
        // This is the default Java bot version that is not
        // configured for any specific game.
        // Implement classes in "core/api" with game specific
        // data in order to use this language for a chosen game.
        // Check this guide: <TODO_LINK>
        println(state)
    }

    // Connects your bot to Lia game engine, don't change it.
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
        }
    }
}
