package com.planet_lia.match_generator.game.api;

public class UnitData {
    public int x;
    public int y;
    public int points;
    public int lives;

    public UnitData(int x, int y, int points, int lives) {
        this.x = x;
        this.y = y;
        this.points = points;
        this.lives = lives;
    }
}
