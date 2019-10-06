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

public class GameLogic {

    Args args;
    GameConfig gameConfig;
    GeneralConfig generalConfig;
    BotDetails[] botsDetails;
    BotServer server;

    SpriteBatch batch;
    Texture texture;

    int textureWidth;

    public GameLogic(Args args, GameConfig gameConfig, BotDetails[] botsDetails, BotServer server) {
        this.args = args;
        this.gameConfig = gameConfig;
        this.botsDetails = botsDetails;
        this.server = server;
        generalConfig = gameConfig.generalConfig;
        textureWidth = gameConfig.mapWidth;
    }

    public void setupGraphics() {
        batch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal(GameConfig.PATH_TO_ASSETS + "/images/block.png"));
    }

    public void update(Timer timer, float delta) {
        if (textureWidth == gameConfig.mapWidth) {
            textureWidth *= 0.5;
        } else {
            textureWidth *= 2;
        }
    }

    public void draw(Camera camera, Viewport viewport) {
        // Draw your game here
        // IMPORTANT: Do not update any logic here as this method will
        //            not be called when the debug mode is not enabled

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(texture,0, 0, textureWidth, gameConfig.mapHeight);
        batch.end();
    }
}
