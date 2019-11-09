package core.api;

import core.MatchDetails;

import java.util.ArrayList;

public class InitialData {
    public int __uid;
    public MatchDetails __matchDetails;

    public int mapWidth;
    public int mapHeight;
    public float unitSize;
    public int planetDiameter;
    public float unitSpeed;
    public float unitRotationSpeed;
    public int numberOfWorkersOnStart;
    public int resourcesForNewUnit;
    public int resourcesGenerationSpeed;
    public int maxActiveWorkersPerPlanet;

    public ArrayList<PlanetData> yourPlanets;
    public ArrayList<PlanetData> freePlanets;
    public ArrayList<PlanetData> opponentPlanets;
    public ArrayList<UnitData> yourWorkers;
    public ArrayList<UnitData> opponentWorkers;
    public ArrayList<UnitData> yourWarriors;
    public ArrayList<UnitData> opponentWarriors;
}
