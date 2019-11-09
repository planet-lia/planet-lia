package com.planet_lia.match_generator.game.api;

import com.planet_lia.match_generator.game.entities.Unit;

public class UnitData {

    public int id;
    public Unit.Type type;
    public float x, y;
    public float rotation;
    public float health;
    public int currentPlanetId;
    public int destinationPlanetId;

    public UnitData(int id,
                    Unit.Type type,
                    float x,
                    float y,
                    float rotation,
                    float health,
                    int currentPlanetId,
                    int destinationPlanetId) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.health = health;
        this.currentPlanetId = currentPlanetId;
        this.destinationPlanetId = destinationPlanetId;
    }
}
