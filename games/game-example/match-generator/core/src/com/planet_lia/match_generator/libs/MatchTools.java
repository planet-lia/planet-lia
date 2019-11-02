package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.utils.viewport.Viewport;

public class MatchTools {
    public BotDetails[] botsDetails;
    public BotServer server;
    public Viewport gameViewport;
    public EntityDetailsSystem entityDetailsSystem;

    public MatchTools(BotDetails[] botsDetails,
                      BotServer server,
                      Viewport gameViewport,
                      EntityDetailsSystem entityDetailsSystem) {
        this.botsDetails = botsDetails;
        this.server = server;
        this.gameViewport = gameViewport;
        this.entityDetailsSystem = entityDetailsSystem;
    }
}
