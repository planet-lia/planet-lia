package com.planet_lia.match_generator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.planet_lia.match_generator.libs.BotDetails;
import com.planet_lia.match_generator.libs.BotServer;
import com.planet_lia.match_generator.libs.Timer;
import com.planet_lia.match_generator.logic.Args;
import com.planet_lia.match_generator.logic.GameConfig;

public class MatchGenerator extends ApplicationAdapter {

    private Args args;
    private GameConfig gameConfig;
    private Timer timer = new Timer();
    private BotServer server;

    public MatchGenerator(Args args, GameConfig gameConfig, BotDetails[] botsDetails) throws Exception {
        this.args = args;
        this.gameConfig = gameConfig;

        server = new BotServer(gameConfig.generalConfig, timer, args.port, botsDetails);
        //server.waitForBotsToConnect();
    }

    @Override
    public void create() {}

    @Override
    public void render() {
        if (this.args.debug) {
            Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
    }

    @Override
    public void dispose() {}
}
