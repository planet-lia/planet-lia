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

    Random rand = new Random();
    InitialData data;

    // Called only once before the match starts. It holds the data that you
    // may need before the game starts.
    @Override
        public void setup(InitialData data) {
        System.out.println((new Gson()).toJson(data));
        this.data = data;
    }

    // Called repeatedly while the match is generating. Each time you receive
    // the current match state and can use response object to issue your commands.
    @Override
    public void update(MatchState state, Response response) {

        // Spawn new worker on your planets if possible
        for (PlanetData planet : state.yourPlanets) {
             if (planet.canSpawnNewUnit) {
                 if (state.time < 30) response.spawnUnit(planet.id, UnitType.WORKER);
                 else response.spawnUnit(planet.id, UnitType.WARRIOR);

//                 response.spawnUnit(planet.id, UnitType.WORKER);
             }

             // Send redundant units to new planets
             for (int i = data.maxActiveWorkersPerPlanet - 1; i < planet.idsOfUnitsOnPlanet.size(); i++) {
//                 if (planet.id == 22) continue;

                 if (!state.freePlanets.isEmpty()) {
                     int destinationPlanetIndex = (int) (rand.nextFloat() * state.freePlanets.size());
                     int destinationPlanetId = state.freePlanets.get(destinationPlanetIndex).id;
                     response.sendUnit(planet.idsOfUnitsOnPlanet.get(i), destinationPlanetId);
                 }
                 else {
                     ArrayList<PlanetData> allPlanets = new ArrayList<>();
                     allPlanets.addAll(state.opponentPlanets);
                     allPlanets.addAll(state.yourPlanets);

                     int destinationPlanetIndex = (int) (rand.nextFloat() * allPlanets.size());
                     int destinationPlanetId = allPlanets.get(destinationPlanetIndex).id;
                     response.sendUnit(planet.idsOfUnitsOnPlanet.get(i), destinationPlanetId);
                 }
             }
        }

    }

    // Connects your bot to match generator, don't change it.
    public static void main(String[] args) throws Exception {
        NetworkingClient.connectNew(args, new MyBot());
    }
}
