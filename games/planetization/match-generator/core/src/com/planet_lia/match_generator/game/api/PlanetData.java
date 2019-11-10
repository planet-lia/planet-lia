package com.planet_lia.match_generator.game.api;

import com.planet_lia.match_generator.game.entities.Owner;

import java.util.ArrayList;

public class PlanetData {

    public int id;
    public Owner owner;
    public float x, y;
    public ArrayList<Integer> idsOfUnitsOnPlanet;
    public float resources;
    public boolean canSpawnNewUnit;

    public PlanetData(int id,
                      Owner owner,
                      float x,
                      float y,
                      ArrayList<Integer> idsOfUnitsOnPlanet,
                      float resources,
                      boolean canSpawnNewUnit) {
        this.id = id;
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.idsOfUnitsOnPlanet = idsOfUnitsOnPlanet;
        this.resources = resources;
        this.canSpawnNewUnit = canSpawnNewUnit;
    }
}
