package com.planet_lia.match_generator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.planet_lia.match_generator.libs.*;
import com.planet_lia.match_generator.logic.Args;
import com.planet_lia.match_generator.logic.GameConfig;

public class MatchGenerator extends ApplicationAdapter {

    Args args;
    GameConfig gameConfig;
    GeneralConfig generalConfig;
    BotDetails[] botsDetails;

    Timer timer = new Timer();
    BotServer server;

    boolean isFirstUpdate = true;
    FPSLimiter fpsLimiter;

    Camera gameCamera;
    Viewport gameViewport;
    Camera logsCamera;
    Viewport logsViewport;

    LogsStage logsStage;
    Game game;

    public MatchGenerator(Args args, GameConfig gameConfig, BotDetails[] botsDetails) {
        this.args = args;
        this.gameConfig = gameConfig;
        this.botsDetails = botsDetails;
        generalConfig = gameConfig.generalConfig;

        server = new BotServer(generalConfig, timer, args.port, botsDetails);
        //server.waitForBotsToConnect();

        game = new Game(args, gameConfig, botsDetails, server);
    }

    @Override
    public void create() {
        if (args.debug) {
            VisUI.load();

            // Create game camera and viewport
            gameCamera = new OrthographicCamera();
            gameViewport = new FitViewport(gameConfig.mapWidth, gameConfig.mapHeight, gameCamera);
            gameCamera.position.x = gameViewport.getWorldWidth() * 0.5f;
            gameCamera.position.y = gameViewport.getWorldHeight() * 0.5f;

            // Create logs camera and viewport
            logsCamera = new OrthographicCamera();
            logsViewport = new FitViewport(
                    getLogsViewWidth(),
                    Gdx.graphics.getHeight(),
                    logsCamera);
            logsCamera.position.x = logsViewport.getWorldWidth() * 0.5f;
            logsCamera.position.y = logsViewport.getWorldHeight() * 0.5f;

            logsStage = new LogsStage(logsViewport, timer, botsDetails);
            Gdx.input.setInputProcessor(logsStage);

            // Will make sure that the debug view will run
            // with appropriate frame rate
            fpsLimiter = new FPSLimiter();

            game.setupGraphics();

            server.setLogsStage(logsStage);
        }
    }

    @Override
    public void render() {
        float delta = getDelta();

        timer.time += delta;
        game.update(timer, delta);

        // Only render to screen when debug view is enabled
        if (args.debug) {
           draw();
            fpsLimiter.sync(generalConfig.ticksPerSecond);
        }
    }

    private int getLogsViewWidth() {
        return gameConfig.generalConfig.debugWindow.getLogsViewWidth(Gdx.graphics.getHeight());
    }

    private int getGameViewWidth() {
        return gameConfig.generalConfig.debugWindow.getGameViewWidth(Gdx.graphics.getHeight());
    }

    private float getDelta() {
        if (isFirstUpdate) {
            isFirstUpdate = false;
            return 0;
        }
        return 1.0f / generalConfig.ticksPerSecond;
    }

    private void draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Render game
        gameCamera.update();
        gameViewport.apply();
        game.draw(gameCamera, gameViewport);

        // Render logs view
        logsCamera.update();
        logsViewport.apply();
        logsStage.act();
        logsStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (args.debug) {
            gameViewport.update(getGameViewWidth(), height);
            logsViewport.update(getLogsViewWidth(), height);
            logsViewport.setScreenX(getGameViewWidth());
        }
    }

    @Override
    public void dispose() {
        if (args.debug) {
            VisUI.dispose();
        }
    }
}
