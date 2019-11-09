package com.planet_lia.match_generator.game.api;

import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.game.entities.Owner;
import com.planet_lia.match_generator.game.entities.Planet;
import com.planet_lia.match_generator.game.entities.Unit;
import com.planet_lia.match_generator.libs.ApiMessage;
import com.planet_lia.match_generator.libs.BotMessageType;

import java.util.ArrayList;

public class MatchStateMessage extends ApiMessage {

    public float time;

    public ArrayList<PlanetData> yourPlanets;
    public ArrayList<PlanetData> freePlanets;
    public ArrayList<PlanetData> opponentPlanets;
    public ArrayList<UnitData> yourWorkers;
    public ArrayList<UnitData> opponentWorkers;
    public ArrayList<UnitData> yourWarriors;
    public ArrayList<UnitData> opponentWarriors;

    public static MatchStateMessage create(float time,
                                        Owner owner,
                                        ArrayList<Planet> planets,
                                        ArrayList<Unit> redUnits,
                                        ArrayList<Unit> greenUnits) {
        MatchStateMessage msg = new MatchStateMessage();
        msg.__type = BotMessageType.UPDATE;
        msg.time = time;

        // Setup planets
        msg.yourPlanets = new ArrayList<>();
        msg.opponentPlanets = new ArrayList<>();
        msg.freePlanets = new ArrayList<>();
        for (Planet planet : planets) {
            if (planet.owner == owner) {
                msg.yourPlanets.add(toPlanetData(planet));
            }
            else if (planet.owner == Owner.NONE) {
                msg.freePlanets.add(toPlanetData(planet));
            }
            else {
                msg.opponentPlanets.add(toPlanetData(planet));
            }
        }

        // Setup units
        msg.yourWorkers = new ArrayList<>();
        msg.opponentWorkers = new ArrayList<>();
        msg.yourWarriors = new ArrayList<>();
        msg.opponentWarriors = new ArrayList<>();

        if (owner == Owner.RED) {
            for (Unit unit : redUnits) {
                if (unit.type == Unit.Type.WORKER) msg.yourWorkers.add(toUnitData(unit));
                else if (unit.type == Unit.Type.WARRIOR) msg.yourWarriors.add(toUnitData(unit));
            }
            for (Unit unit : greenUnits) {
                if (unit.type == Unit.Type.WORKER) msg.opponentWorkers.add(toUnitData(unit));
                else if (unit.type == Unit.Type.WARRIOR) msg.opponentWarriors.add(toUnitData(unit));
            }
        }
        else {
            for (Unit unit : greenUnits) {
                if (unit.type == Unit.Type.WORKER) msg.yourWorkers.add(toUnitData(unit));
                else if (unit.type == Unit.Type.WARRIOR) msg.yourWarriors.add(toUnitData(unit));
            }
            for (Unit unit : redUnits) {
                if (unit.type == Unit.Type.WORKER) msg.opponentWorkers.add(toUnitData(unit));
                else if (unit.type == Unit.Type.WARRIOR) msg.opponentWarriors.add(toUnitData(unit));
            }
        }

        return msg;
    }

    private static UnitData toUnitData(Unit unit) {
        return new UnitData(unit.unitId, unit.type, unit.x, unit.y, unit.rotation, 100,
                (unit.currentPlanet != null) ? unit.currentPlanet.planetId : -1,
                (unit.destinationPlanet != null) ? unit.destinationPlanet.planetId : -1
        );
    }

    private static PlanetData toPlanetData(Planet planet) {
        ArrayList<Integer> idsOfUnitsOnPlanet = new ArrayList<>(planet.unitsOnPlanet.size());
        for (Unit unit : planet.unitsOnPlanet) {
            idsOfUnitsOnPlanet.add(unit.unitId);
        }

        return new PlanetData(planet.planetId, planet.owner, planet.x, planet.y, idsOfUnitsOnPlanet,
                planet.resources, planet.resources >= GameConfig.values.resourcesForNewUnit);
    }
}
