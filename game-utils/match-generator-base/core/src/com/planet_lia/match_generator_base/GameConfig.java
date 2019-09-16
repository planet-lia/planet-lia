package com.planet_lia.match_generator_base;

public class GameConfig {
    public String gameName;
    public int debugViewWidth;
    public int debugViewHeight;

    public GameConfig(String gameName, int debugViewWidth, int debugViewHeight) {
        this.gameName = gameName;
        this.debugViewWidth = debugViewWidth;
        this.debugViewHeight = debugViewHeight;
    }
}
