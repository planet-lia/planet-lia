package com.planet_lia.match_generator_base.logic;

import com.planet_lia.match_generator_base.libs.GeneralConfig;

// TODO load from a replay file
public class GameConfig {
    public String gameName;
    public int debugViewWidth;
    public int debugViewHeight;
    public GeneralConfig generalConfig;

    public GameConfig() {
        this.gameName = "game-name";
        this.debugViewWidth = 1440;
        this.debugViewHeight = 810;

        this.generalConfig = new GeneralConfig();
        this.generalConfig.ticksPerSecond = 30;
        this.generalConfig.connectingBotsTimeout = 10;
        this.generalConfig.botFirstResponseTimeout = 2;
        this.generalConfig.botResponseTimeout = 0.5f;
        this.generalConfig.maxTimeoutsPerBot = 8;
        this.generalConfig.gameUpdatesPerBotsUpdate = 3;
        this.generalConfig.botResponseTotalDurationMax = 30;
    }
}