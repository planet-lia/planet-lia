package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.planet_lia.match_generator.game.GameConfig;

public class MatchGenerator extends ApplicationAdapter {

    DefaultArgs args;
    BotDetails[] botsDetails;

    Timer timer = new Timer();
    BotServer server;

    // Will make sure that the debug view will run
    // with appropriate frame rate
    FPSLimiter fpsLimiter = new FPSLimiter();
    boolean isFirstUpdate = true;

    OrthographicCamera gameCamera = new OrthographicCamera();
    OrthographicCamera logsCamera  = new OrthographicCamera();
    OrthographicCamera controlsCamera  = new OrthographicCamera();

    Viewport gameViewport;
    Viewport logsViewport;
    Viewport controlsViewport;

    DebugGuiStage debugGuiStage;
    ControlsStage controlsStage;
    EntityDetailsSystem entityDetailsSystem;

    GameLogicBase gameLogic;

    ShapeRenderer shapeRenderer;

    public MatchGenerator(DefaultArgs args, BotDetails[] botsDetails, GameLogicBase gameLogic) throws Exception {
        this.args = args;
        this.botsDetails = botsDetails;
        this.gameLogic = gameLogic;

        server = new BotServer(GameConfig.values.general, timer, args.port, botsDetails);
        server.start();
        server.waitForBotsToConnect();
    }

    @Override
    public void create() {
        // Create game camera and viewport
        if (GameConfig.values.cameraViewWidth / GameConfig.values.cameraViewHeight != 16/9f) {
            throw new Error("cameraViewWidth / cameraViewHeight should be in 16/9 ratio");
        }
        gameViewport = new FitViewport(GameConfig.values.cameraViewWidth, GameConfig.values.cameraViewHeight, gameCamera);
        // gameCamera is be positioned in GameLogic

        // Create logs camera and viewport
        logsViewport = new FitViewport(getLogsViewWidth(), Gdx.graphics.getHeight(), logsCamera);
        centerCamera(logsCamera, logsViewport);

        // Create controls camera and viewport
        controlsViewport = new FitViewport(getGameViewWidth(), getControlsViewHeight(), controlsCamera);
        centerCamera(controlsCamera, controlsViewport);

        if (args.debug) {
            VisUI.load();
            shapeRenderer = new ShapeRenderer();

            debugGuiStage = new DebugGuiStage(logsViewport, botsDetails, GameConfig.values.general);
            controlsStage = new ControlsStage(controlsViewport, timer);

            entityDetailsSystem = new EntityDetailsSystem(gameViewport, debugGuiStage);

            // Set input processor
            InputMultiplexer mx = new InputMultiplexer(debugGuiStage, controlsStage, entityDetailsSystem);
            Gdx.input.setInputProcessor(mx);

            server.setDebugGuiStage(debugGuiStage);
        }

        gameLogic.setup(new MatchTools(botsDetails, server, gameViewport, entityDetailsSystem));
    }

    private void centerCamera(Camera camera, Viewport viewport) {
        camera.position.x = viewport.getWorldWidth() * 0.5f;
        camera.position.y = viewport.getWorldHeight() * 0.5f;
    }

    private float carry = 0;

    @Override
    public void render() {
        double delta = getDelta();

        if (args.debug) {
            // When generation is paused but step was clicked
            if (controlsStage.isStep()) {
                controlsStage.disableStep();

                timer.add(delta);
                gameLogic.update(timer, (float) delta);
                if (isFirstUpdate) {
                    isFirstUpdate = false;
                }
            }
            // Match is generating, update the match
            else {
                float logicFps = GameConfig.values.general.gameUpdatesPerSecond * controlsStage.getSpeed();
                float drawFps = GameConfig.values.general.debugWindow.framesPerSecond;
                float updatesPerDraw = logicFps / drawFps;

                // Adjust how many times update logic is called based on the
                // default ticks per second of the match, taking in consideration
                // custom speed of the game and interval of drawing match to the screen
                for (int i = 1; i <= updatesPerDraw + carry; i++) {
                    timer.add(delta);
                    gameLogic.update(timer, (float) delta);
                    if (isFirstUpdate) {
                        isFirstUpdate = false;
                    }
                }
                carry = updatesPerDraw + carry - (int) updatesPerDraw;
                carry -= (int) carry;
            }

            entityDetailsSystem.update();

            // Render the scene every render call
            draw();
            controlsStage.setTime(timer.getTime());
            fpsLimiter.sync(GameConfig.values.general.debugWindow.framesPerSecond);
        }
        else {
            timer.add(delta);
            gameLogic.update(timer, (float) delta);
            if (isFirstUpdate) {
                isFirstUpdate = false;
            }
        }
    }

    private int getLogsViewWidth() {
        return GameConfig.values.general.debugWindow.getLogsViewWidth(Gdx.graphics.getHeight());
    }

    private int getGameViewWidth() {
        return GameConfig.values.general.debugWindow.getGameViewWidth(Gdx.graphics.getHeight());
    }

    private int getControlsViewHeight() {
        return GameConfig.values.general.debugWindow.getControlsViewHeight(Gdx.graphics.getHeight());
    }

    private int getGameViewHeight() {
        return GameConfig.values.general.debugWindow.getGameViewHeight(Gdx.graphics.getHeight());
    }

    private double getDelta() {
        if (isFirstUpdate) {
            return 0;
        }
        return 1.0 / GameConfig.values.general.gameUpdatesPerSecond;
    }

    private void draw() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw game view
        gameCamera.update();
        gameViewport.apply();
        // Draw black background below game view
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        // Draw custom logic
        gameLogic.draw();

        // Draw debug gui view
        logsCamera.update();
        logsViewport.apply();
        debugGuiStage.act();
        debugGuiStage.draw();

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
            gameLogic.dispose();
            VisUI.dispose();
        }
    }
}
