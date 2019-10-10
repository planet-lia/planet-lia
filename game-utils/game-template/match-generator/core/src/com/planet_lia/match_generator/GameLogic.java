package com.planet_lia.match_generator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.planet_lia.match_generator.libs.BaseEntity;
import com.planet_lia.match_generator.libs.Timer;
import com.planet_lia.match_generator.logic.Assets;
import com.planet_lia.match_generator.logic.BoxEntity;
import com.planet_lia.match_generator.logic.MatchTools;

public class GameLogic {

    MatchTools tools;
    SpriteBatch batch;

    BaseEntity[] entities = new BaseEntity[3];

    public GameLogic(MatchTools tools) {
        this.tools = tools;

        if (tools.args.debug) {
            setupGraphics();
        }

        entities[0] = new BoxEntity(
                tools.gameConfig.cameraViewWidth * 0.5f,
                tools.gameConfig.cameraViewHeight * 0.5f,
                tools.gameConfig.cameraViewWidth,
                tools.gameConfig.cameraViewHeight,
                2);
        entities[1] = new BoxEntity(
                tools.gameConfig.cameraViewWidth * 0.52f,
                tools.gameConfig.cameraViewHeight * 0.5f,
                tools.gameConfig.cameraViewWidth,
                tools.gameConfig.cameraViewHeight,
                1);
        entities[2] = new BoxEntity(
                tools.gameConfig.cameraViewWidth * 0.8f,
                tools.gameConfig.cameraViewHeight * 0.8f,
                tools.gameConfig.cameraViewWidth,
                tools.gameConfig.cameraViewHeight,
                1);

        if (tools.entityDetailsSystem != null) {
            for (BaseEntity entity : entities) {
                tools.entityDetailsSystem.registerEntity(entity);
            }
        }
    }

    private void setupGraphics() {
        Assets.load(tools.gameConfig);
        batch = new SpriteBatch();
    }

    public void update(Timer timer, float delta) {
        for (BaseEntity entity : entities) {
            entity.update(timer, delta);
        }
    }

    public void draw() {
        // Draw your game here
        // IMPORTANT: Do not update any logic here as this method will
        //            not be called when the debug mode is not enabled

        batch.setProjectionMatrix(tools.gameViewport.getCamera().combined);

        batch.begin();
        for (BaseEntity entity : entities) {
            entity.draw(batch);
        }
        batch.end();
    }

    /**
     * Cleanup necessary stuff before exiting
     */
    public void dispose() {
        Assets.dispose();
    }
}
