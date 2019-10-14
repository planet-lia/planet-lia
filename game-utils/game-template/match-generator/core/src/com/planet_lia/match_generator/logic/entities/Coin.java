package com.planet_lia.match_generator.logic.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.planet_lia.match_generator.libs.Clickable;
import com.planet_lia.match_generator.libs.Timer;
import com.planet_lia.match_generator.logic.Assets;
import com.planet_lia.match_generator.logic.GameConfig;

public class Coin implements Clickable {

    public float x;
    public float y;
    public float size;
    private int layer = 1;

    private Sprite sprite;

    private float previousPosChangeTime = 0f;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Coin(Unit[] units) {
        this.size = GameConfig.values.coinSize;
        changePosition(units);

        Texture tex = Assets.get(Assets.coin, Texture.class);
        if (tex != null) {
            sprite = new Sprite(tex);
        }
    }

    public void update(Timer timer, Unit[] units) {
        if (timer.getTime() >= previousPosChangeTime + GameConfig.values.coinPositionChangePeriod) {
            changePosition(units);
            previousPosChangeTime = timer.getTime();
        }
    }

    private void changePosition(Unit[] units) {
        float tileSize = GameConfig.values.backgroundTileSize;
        int mapWidth = GameConfig.values.mapWidth;
        int mapHeight = GameConfig.values.mapHeight;

        for (int i = 0; i < 100; i++) {
            float newX = MathUtils.random(0, mapWidth - 1) + tileSize / 2f;
            float newY = MathUtils.random(0, mapHeight - 1) + tileSize / 2f;

            for (Unit unit : units) {
                if (unit.getX() != newX || unit.getY() != newY) {
                    x = newX;
                    y = newY;
                    return;
                }
            }
        }
        // Tried 100 times but failed to find a position
    }

    public void draw(SpriteBatch batch) {
        batch.draw(sprite,
                x - size / 2,
                y - size / 2,
                size / 2, size / 2,
                size, size,
                1, 1,
                0);
    }

    @Override
    public String getDisplayText() {
        JsonObject object = new JsonObject();
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("size", size);
        return gson.toJson(object);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getWidth() {
        return size;
    }

    @Override
    public float getHeight() {
        return size;
    }

    @Override
    public int getLayer() {
        return layer;
    }
}
