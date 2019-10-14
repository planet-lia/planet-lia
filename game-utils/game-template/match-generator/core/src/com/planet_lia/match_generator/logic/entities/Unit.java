package com.planet_lia.match_generator.logic.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.planet_lia.match_generator.libs.Clickable;
import com.planet_lia.match_generator.libs.Timer;
import com.planet_lia.match_generator.logic.Assets;
import com.planet_lia.match_generator.logic.GameConfig;
import com.planet_lia.match_generator.logic.api.commands.Direction;

public class Unit implements Clickable {

    private float x;
    private float y;
    private float size;

    private int layer = 2;
    private Sprite sprite;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Unit(float x, float y) {
        this.x = x;
        this.y = y;
        this.size = GameConfig.values.unitSize;

        Texture tex = Assets.get(Assets.unit, Texture.class);
        if (tex != null) {
            sprite = new Sprite(tex);
        }
    }

    public void move(Direction direction) {
        float destinationX = x;
        float destinationY = y;

        switch (direction) {
            case LEFT:
                if (x - size >= size / 2f) {
                    destinationX = x - size;
                }
                break;
            case RIGHT:
                if (x + size <= GameConfig.values.mapWidth - size / 2f) {
                    destinationX = x + size;
                }
                break;
            case UP:
                if (y + size <= GameConfig.values.mapHeight - size / 2f) {
                    destinationY = y + size;
                }
                break;
            case DOWN:
                if (y - size >= size / 2f) {
                    destinationY = y - size;
                }
                break;
        }

        // Store this movement in the replay file
        this.x = destinationX;
        this.y = destinationY;
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
        object.addProperty("rotation", sprite.getRotation());
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
