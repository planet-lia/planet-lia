package com.planet_lia.match_generator.game.entities;

import com.badlogic.gdx.math.Vector2;

public class UnitIndicator {
    public String eid;
    public Vector2 location;
    public Owner owner;
    public float size;

    public UnitIndicator(String eid, Vector2 location, Owner owner, float size) {
        this.eid = eid;
        this.location = location;
        this.owner = owner;
        this.size = size;
    }
}
