package com.planet_lia.match_generator;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planet_lia.match_generator.libs.BotDetails;
import com.planet_lia.match_generator.libs.BotServer;
import com.planet_lia.match_generator.libs.GeneralConfig;
import com.planet_lia.match_generator.libs.Timer;
import com.planet_lia.match_generator.logic.Args;
import com.planet_lia.match_generator.logic.GameConfig;

public class Game {

    Args args;
    GameConfig gameConfig;
    GeneralConfig generalConfig;
    BotDetails[] botsDetails;
    BotServer server;

    SpriteBatch batch;
    Texture texture;

    public Game(Args args, GameConfig gameConfig, BotDetails[] botsDetails, BotServer server) {
        this.args = args;
        this.gameConfig = gameConfig;
        this.botsDetails = botsDetails;
        this.server = server;
        generalConfig = gameConfig.generalConfig;
    }

    public void setupGraphics() {
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal(GameConfig.PATH_TO_ASSETS + "/images/block.png"));
    }

    public void update(Timer timer, float delta) {

    }

    public void draw(Camera camera, Viewport viewport) {
        // Render game
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(texture,0, 0, gameConfig.mapWidth, gameConfig.mapHeight);
        batch.end();
    }
}
