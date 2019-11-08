package com.planet_lia.match_generator.libs;

public abstract class GameLogicBase {

    public MatchTools tools;

    public void setup(MatchTools tools) {
        this.tools = tools;
    }
    public abstract void update(Timer timer, float delta);
    public abstract void draw();
    public abstract void dispose();
}
