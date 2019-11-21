package com.planet_lia.match_generator.game.api;

import com.planet_lia.match_generator.game.entities.Saw;

public class SawData {
    public int x;
    public int y;
    public Saw.SawDirection direction;

    public SawData(int x, int y, Saw.SawDirection direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}
