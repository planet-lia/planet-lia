package com.planet_lia.match_generator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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

    // Will make sure that the debug view will run
    // with appropriate frame rate
    FPSLimiter fpsLimiter = new FPSLimiter();
    boolean isFirstUpdate = true;

    Camera gameCamera = new OrthographicCamera();
    Camera logsCamera  = new OrthographicCamera();
    Camera controlsCamera  = new OrthographicCamera();

    Viewport gameViewport;
    Viewport logsViewport;
    Viewport controlsViewport;

    LogsStage logsStage;
    ControlsStage controlsStage;

    GameLogic gameLogic;

    public MatchGenerator(Args args, GameConfig gameConfig, BotDetails[] botsDetails) {
        this.args = args;
        this.gameConfig = gameConfig;
        this.botsDetails = botsDetails;
        generalConfig = gameConfig.generalConfig;

        server = new BotServer(generalConfig, timer, args.port, botsDetails);
        //server.waitForBotsToConnect();

        gameLogic = new GameLogic(args, gameConfig, botsDetails, server);
    }

    @Override
    public void create() {
        if (args.debug) {
            VisUI.load();

            // Create game camera and viewport
            gameViewport = new FitViewport(gameConfig.mapWidth, gameConfig.mapHeight, gameCamera);
            centerCamera(gameCamera, gameViewport);

            // Create logs camera and viewport
            logsViewport = new FitViewport(getLogsViewWidth(), Gdx.graphics.getHeight(), logsCamera);
            centerCamera(logsCamera, logsViewport);

            // Create controls camera and viewport
            controlsViewport = new FitViewport(getGameViewWidth(), getControlsViewHeight(), controlsCamera);
            centerCamera(controlsCamera, controlsViewport);

            logsStage = new LogsStage(logsViewport, botsDetails);
            controlsStage = new ControlsStage(controlsViewport, timer);

            // Set input processor
            InputMultiplexer mx = new InputMultiplexer(logsStage, controlsStage);
            Gdx.input.setInputProcessor(mx);

            gameLogic.setupGraphics();

            server.setLogsStage(logsStage);
        }
    }

    private void centerCamera(Camera camera, Viewport viewport) {
        camera.position.x = viewport.getWorldWidth() * 0.5f;
        camera.position.y = viewport.getWorldHeight() * 0.5f;
    }

    float carry = 0;

    @Override
    public void render() {
        float delta = getDelta();

        if (args.debug) {
            float logicFps = generalConfig.ticksPerSecond * controlsStage.getSpeed();
            float drawFps = generalConfig.debugWindow.framesPerSecond;
            float updatesPerDraw = logicFps / drawFps;

            // Adjust how many times update logic is called based on the
            // default ticks per second of the match, taking in consideration
            // custom speed of the game and interval of drawing match to the screen
            for (int i = 1; i <= updatesPerDraw + carry; i++) {
                timer.time += delta;
                gameLogic.update(timer, delta);
            }
            carry = updatesPerDraw + carry - (int) updatesPerDraw;
            carry -= (int) carry;

            // Render the scene every render call
            draw();
            controlsStage.setTime(timer.time);
            fpsLimiter.sync(generalConfig.debugWindow.framesPerSecond);
        }
        else {
            timer.time += delta;
            gameLogic.update(timer, delta);
        }
    }

    private int getLogsViewWidth() {
        return gameConfig.generalConfig.debugWindow.getLogsViewWidth(Gdx.graphics.getHeight());
    }

    private int getGameViewWidth() {
        return gameConfig.generalConfig.debugWindow.getGameViewWidth(Gdx.graphics.getHeight());
    }

    private int getControlsViewHeight() {
        return gameConfig.generalConfig.debugWindow.getControlsViewHeight(Gdx.graphics.getHeight());
    }

    private int getGameViewHeight() {
        return gameConfig.generalConfig.debugWindow.getGameViewHeight(Gdx.graphics.getHeight());
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

        // Draw game
        gameCamera.update();
        gameViewport.apply();
        gameLogic.draw(gameCamera, gameViewport);

        // Draw logs view
        logsCamera.update();
        logsViewport.apply();
        logsStage.act();
        logsStage.draw();

        // Draw controls
        controlsCamera.update();
        controlsViewport.apply();
        controlsStage.act();
        controlsStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (args.debug) {
            gameViewport.update(getGameViewWidth(), getGameViewHeight());
            gameViewport.setScreenY(getControlsViewHeight());
            controlsViewport.update(getGameViewWidth(), getControlsViewHeight());
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
