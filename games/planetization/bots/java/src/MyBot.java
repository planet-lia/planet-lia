import com.google.gson.Gson;
import core.*;
import core.api.*;
import core.api.commands.UnitType;

import java.util.ArrayList;
import java.util.Random;

/**
 * Example Java bot implementation for Planetization game.
 */
public class MyBot implements Bot {

    InitialData data;

    // Called only once before the match starts. It holds the data that you
    // may need to setup your bot.
    @Override
        public void setup(InitialData data) {
        this.data = data;
    }

    // Called repeatedly while the match is generating. Each time you receive
    // the current match state and can use response object to issue your commands.
    @Override
    public void update(MatchState state, Response response) {

        // Iterate through all of the planets that you currently own
        for (PlanetData planet : state.yourPlanets) {

            // If the planet has enough resources, spawn new unit on it, choose unit type randomly
            if (planet.canSpawnNewUnit) {
                UnitType type = (Math.random() < 0.5f) ? UnitType.WARRIOR : UnitType.WORKER;
                response.spawnUnit(planet.id, type);
            }

            // Send redundant units to new planets. Only maxActiveWorkersPerPlanet workers help to collect
            // resources other simply sit on the planet.
            for (int i = data.maxActiveWorkersPerPlanet - 1; i < planet.idsOfUnitsOnPlanet.size(); i++) {
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
