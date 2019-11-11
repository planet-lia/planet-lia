import core.Bot
import core.NetworkingClient
import core.api.InitialData
import core.api.MatchState
import core.api.Response
import core.api.UnitType
import java.util.*

/** Example Kotlin bot implementation for Planetization game. */
class MyBot : Bot {

    var rand = Random(12)
    lateinit var data: InitialData;

    // Called only once before the match starts. It holds the
    // data that you may need to setup your bot.
    override fun setup(data: InitialData) {
        this.data = data
    }

    // Called repeatedly while the match is generating. Read game state using
    // state parameter and issue your commands with response parameter.
    override fun update(state: MatchState, response: Response) {

        // Iterate through all the planets you own
        for (planet in state.yourPlanets) {

            // If the planet has enough resources, spawn new unit on it
            if (planet.canSpawnNewUnit) {
                val type = if (rand.nextFloat() < 0.5f) UnitType.WARRIOR else UnitType.WORKER
                response.spawnUnit(planet.id, type)
            }

            // Only a certain number of workers can mine resources on
            // a planet. Let's send the rest of them somewhere else.
            for (i in data.maxActiveWorkersPerPlanet until planet.idsOfUnitsOnPlanet.size) {
                val unitId = planet.idsOfUnitsOnPlanet[i]

                // Randomly select if you will send the unit to a free or opponent planet
                val planets = if (rand.nextFloat() < 0.5f) state.freePlanets else state.opponentPlanets

                if (planets.isNotEmpty()) {
                    // Select a random planet in planets and send your unit there
                    val destinationPlanetIndex = (rand.nextFloat() * planets.size).toInt()
                    val destinationPlanetId = planets[destinationPlanetIndex].id
                    response.sendUnit(unitId, destinationPlanetId)
                }
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
