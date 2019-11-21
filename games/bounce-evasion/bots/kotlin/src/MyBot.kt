import com.google.gson.Gson
import core.*
import core.api.*

/**
 * Example Kotlin bot implementation for Bounce Evasion game.
 */
class MyBot : Bot {

    lateinit var data: InitialData;

    // Called only once before the match starts. It holds the
    // data that you may need before the game starts.
    override fun setup(data: InitialData) {
        println(Gson().toJson(data))
        this.data = data

        // Print out the map
        for (y in data.mapHeight - 1 downTo 0) {
            for (x in 0 until data.mapWidth) {
                print(if (data.map[y][x]) "_" else "#")
            }
            println()
        }
    }

    // Called repeatedly while the match is generating. Each
    // time you receive the current match state and can use
    // response object to issue your commands.
    override fun update(state: MatchState, response: Response) {
        // Find and send your unit to a random direction that
        // moves it to a valid field on the map
        // TODO: Remove this code and implement a proper path finding!
        while (true) {
            val rand = Math.random()

            // Pick a random direction to move
            val direction: Direction = when {
                rand < 0.25 -> Direction.LEFT
                rand < 0.5 -> Direction.RIGHT
                rand < 0.75 -> Direction.UP
                else -> Direction.DOWN
            }

            // Find on which position this move will send your unit
            val newX = when (direction) {
                Direction.LEFT -> state.yourUnit.x - 1
                Direction.RIGHT -> state.yourUnit.x + 1
                else -> state.yourUnit.x
            }
            val newY = when (direction) {
                Direction.UP -> state.yourUnit.y + 1
                Direction.DOWN -> state.yourUnit.y - 1
                else -> state.yourUnit.y
            }

            // If the new position is on the map then send the unit towards
            // that direction and break the loop, else try again
            if (newX >= 0 && newY >= 0 && newX < data.mapWidth && newY < data.mapHeight && data.map[newY][newX]) {
                response.moveUnit(direction)
                break
            }
        }
    }

    // Connects your bot to match generator, don't change it.
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
        }
    }
}
