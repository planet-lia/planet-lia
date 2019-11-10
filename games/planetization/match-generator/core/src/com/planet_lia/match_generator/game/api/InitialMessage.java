package com.planet_lia.match_generator.game.api;

import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.game.entities.Owner;
import com.planet_lia.match_generator.game.entities.Planet;
import com.planet_lia.match_generator.game.entities.Unit;
import com.planet_lia.match_generator.libs.BaseInitialMessage;
import com.planet_lia.match_generator.libs.BotMessageType;

import java.util.ArrayList;

public class InitialMessage extends BaseInitialMessage {

    // Here add data specific to the game
    public int mapWidth;
    public int mapHeight;
    public float unitSize;
    public int planetDiameter;
    public float unitSpeed;
    public float unitRotationSpeed;
    public int numberOfWorkersOnStart;
    public int unitCost;
    public int resourceGenerationSpeed;
    public int maxActiveWorkersPerPlanet;
    public float maxNumberOfUnitsPerTeam;
    public float maxMatchDuration;
    public float workerHealth;
    public float workerAttack;
    public float warriorHealth;
    public float warriorAttack;
    public float damageReductionRatioOnDefence;

    public ArrayList<PlanetData> yourPlanets;
    public ArrayList<PlanetData> freePlanets;
    public ArrayList<PlanetData> opponentPlanets;
    public ArrayList<UnitData> yourWorkers;
    public ArrayList<UnitData> opponentWorkers;
    public ArrayList<UnitData> yourWarriors;
    public ArrayList<UnitData> opponentWarriors;

    public static InitialMessage create(Owner owner,
                                        ArrayList<Planet> planets,
                                        ArrayList<Unit> redUnits,
                                        ArrayList<Unit> greenUnits) {
        InitialMessage msg = new InitialMessage();
        msg.__type = BotMessageType.INITIAL;

        msg.mapWidth = GameConfig.values.mapWidth;
        msg.mapHeight = GameConfig.values.mapHeight;
        msg.unitSize = GameConfig.values.unitSize;
        msg.planetDiameter = GameConfig.values.planetSize;
        msg.unitSpeed = GameConfig.values.unitSpeed;
        msg.unitRotationSpeed = GameConfig.values.unitRotationSpeed;
        msg.numberOfWorkersOnStart = GameConfig.values.numberOfWorkersOnStart;
        msg.unitCost = GameConfig.values.unitCost;
        msg.resourceGenerationSpeed = GameConfig.values.resourceGenerationSpeed;
        msg.maxActiveWorkersPerPlanet = GameConfig.values.maxActiveWorkersPerPlanet;

        msg.maxNumberOfUnitsPerTeam = GameConfig.values.maxNumberOfUnitsPerTeam;
        msg.maxMatchDuration = GameConfig.values.maxMatchDuration;
        msg.workerHealth = GameConfig.values.workerHealth;
        msg.workerAttack = GameConfig.values.workerAttack;
        msg.warriorHealth = GameConfig.values.warriorHealth;
        msg.warriorAttack = GameConfig.values.warriorAttack;
        msg.damageReductionRatioOnDefence = GameConfig.values.damageReductionRatioOnDefence;

        MatchStateMessage matchMsg = MatchStateMessage.create(0f, owner, planets, redUnits, greenUnits);
        msg.yourPlanets = matchMsg.yourPlanets;
        msg.freePlanets = matchMsg.freePlanets;
        msg.opponentPlanets = matchMsg.opponentPlanets;
        msg.yourWarriors = matchMsg.yourWarriors;
        msg.yourWorkers = matchMsg.yourWorkers;
        msg.opponentWarriors = matchMsg.opponentWarriors;
        msg.opponentWorkers = matchMsg.opponentWorkers;

        return msg;
    }
}
