package com.planet_lia.match_generator.logic;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.planet_lia.match_generator.libs.BotDetails;
import com.planet_lia.match_generator.libs.BotServer;
import com.planet_lia.match_generator.libs.EntityDetailsSystem;

public class MatchTools {
    public Args args;
    public GameConfig gameConfig;
    public BotDetails[] botsDetails;
    public BotServer server;
    public Viewport gameViewport;
    public EntityDetailsSystem entityDetailsSystem;

    public MatchTools(Args args, GameConfig gameConfig, BotDetails[] botsDetails, BotServer server, Viewport gameViewport, EntityDetailsSystem entityDetailsSystem) {
        this.args = args;
        this.gameConfig = gameConfig;
        this.botsDetails = botsDetails;
        this.server = server;
        this.gameViewport = gameViewport;
        this.entityDetailsSystem = entityDetailsSystem;
    }
}
