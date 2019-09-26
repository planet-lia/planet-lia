package com.planet_lia.match_generator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.planet_lia.match_generator.libs.*;
import com.planet_lia.match_generator.logic.Args;
import com.planet_lia.match_generator.logic.GameConfig;

public class MatchGenerator extends ApplicationAdapter {

    Args args;
    GameConfig gameConfig;
    GeneralConfig generalConfig;
    Timer timer = new Timer();
    BotServer server;

    boolean isFirstUpdate = true;
    FPSLimiter fpsLimiter;

    public MatchGenerator(Args args, GameConfig gameConfig, BotDetails[] botsDetails) throws Exception {
        this.args = args;
        this.gameConfig = gameConfig;
        this.generalConfig = gameConfig.generalConfig;

        if (args.debug) {
            // Will make sure that the debug view will run
            // with appropriate frame rate
            this.fpsLimiter = new FPSLimiter();
        }

        server = new BotServer(this.generalConfig, timer, args.port, botsDetails);
        //server.waitForBotsToConnect();
    }

    @Override
    public void create() {}

    @Override
    public void render() {
        float delta = getDelta();

        this.timer.time += delta;

        // Only render to screen when debug view is enabled
        if (this.args.debug) {
           draw();
           fpsLimiter.sync(this.generalConfig.ticksPerSecond);
        }
    }

    private float getDelta() {
        if (isFirstUpdate) {
            isFirstUpdate = false;
            return 0;
        }
        return 1.0f / this.generalConfig.ticksPerSecond;
    }

    private void draw() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {}
}
