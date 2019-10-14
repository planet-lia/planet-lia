package com.planet_lia.match_generator.logic.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.planet_lia.match_generator.libs.Timer;
import com.planet_lia.match_generator.logic.Assets;
import com.planet_lia.match_generator.logic.GameConfig;

public class Background {

    private Sprite sprite;

    public Background() {
        Texture texture = Assets.get(Assets.tile, Texture.class);
        if (texture != null) {
            sprite = new Sprite(texture);
        }
    }


    public void draw(SpriteBatch batch) {
        float tileSize = GameConfig.values.backgroundTileSize;

        for (int x = 0; x < GameConfig.values.mapWidth; x += tileSize) {
            for (int y = 0; y < GameConfig.values.mapHeight; y += tileSize) {
                batch.draw(sprite, x, y, tileSize, tileSize);
            }
        }
    }
}
