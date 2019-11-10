package core.api;

import java.util.ArrayList;

public class PlanetData {
    public int id;
    public Owner owner;
    public float x, y;
    public ArrayList<Integer> idsOfUnitsOnPlanet;
    public float resources;
    public boolean canSpawnNewUnit;
}
