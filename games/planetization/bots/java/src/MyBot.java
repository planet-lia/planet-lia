import com.google.gson.Gson;
import core.*;
import core.api.*;
import core.api.commands.UnitType;

import java.util.ArrayList;
import java.util.Random;

/** Example Java bot implementation for Planetization game. */
public class MyBot implements Bot {

    InitialData data;

    // Called only once before the match starts. It holds
    // the data that you may need to setup your bot.
    @Override
        public void setup(InitialData data) {
        this.data = data;
    }

    // Called repeatedly while the match is generating. Read game state using
    // state parameter and issue your commands with response parameter.
    @Override
    public void update(MatchState state, Response response) {

        // Iterate through all the planets you own
        for (PlanetData planet : state.yourPlanets) {

            // If the planet has enough resources, spawn new unit on it
            if (planet.canSpawnNewUnit) {
                UnitType type = (Math.random() < 0.5f) ? UnitType.WARRIOR : UnitType.WORKER;
                response.spawnUnit(planet.id, type);
            }

            // Only a certain number of workers can mine resources on
            // a planet. Let's send the rest of them somewhere else.
            for (int i = data.maxActiveWorkersPerPlanet; i < planet.idsOfUnitsOnPlanet.size(); i++) {
                int unitId = planet.idsOfUnitsOnPlanet.get(i);

                // Randomly select if you will send the unit to a free or opponent planet
                ArrayList<PlanetData> planets = (Math.random() < 0.5f) ? state.freePlanets : state.opponentPlanets;

                if (!planets.isEmpty()) {
                     // Select a random planet in planets and send your unit there
                     int destinationPlanetIndex = (int) (Math.random() * planets.size());
                     int destinationPlanetId = planets.get(destinationPlanetIndex).id;
                     response.sendUnit(unitId, destinationPlanetId);
                }
             }
        }
    }

    // Connects your bot to match generator, don't change it.
    public static void main(String[] args) throws Exception {
        NetworkingClient.connectNew(args, new MyBot());
    }
}
