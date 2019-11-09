package core.api;

import java.util.ArrayList;

public class MatchState {
    public int __uid;
    public float time;

    public ArrayList<PlanetData> yourPlanets;
    public ArrayList<PlanetData> freePlanets;
    public ArrayList<PlanetData> opponentPlanets;
    public ArrayList<UnitData> yourWorkers;
    public ArrayList<UnitData> opponentWorkers;
    public ArrayList<UnitData> yourWarriors;
    public ArrayList<UnitData> opponentWarriors;
}
