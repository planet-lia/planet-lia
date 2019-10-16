package com.planet_lia.match_generator.logic.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.planet_lia.match_generator.libs.Clickable;
import com.planet_lia.match_generator.libs.replays.*;
import com.planet_lia.match_generator.logic.Assets;
import com.planet_lia.match_generator.logic.GameConfig;
import com.planet_lia.match_generator.logic.api.commands.Direction;

import static com.planet_lia.match_generator.logic.GameConfig.shortenImagePath;

public class Unit implements Clickable {

    private Replay replay;

    public String eid;
    private float x;
    private float y;
    private float size;

    private int layer = 2;
    private Sprite sprite;

    private float destinationX;
    private float destinationY;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Unit(int id, float x, float y, Replay replay, String assetPath) {
        this.replay = replay;
        this.eid = "unit_" + id;
        this.x = x;
        this.y = y;
        this.destinationX = x;
        this.destinationY = y;
        this.size = GameConfig.values.unitSize;

        Texture tex = Assets.get(assetPath, Texture.class);
        if (tex != null) {
            sprite = new Sprite(tex);
        }

        // Write unit to replay file
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, 0f, x));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, 0f, y));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, 0f, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, 0f, size));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, 0f,
                shortenImagePath(assetPath)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, 0f, 2f));
    }

    public void move(Direction direction, float time) {
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
    }

    public void update(float time, float delta) {
        // If the move command was issued in previous turn, move the unit
        if (x != destinationX || y != destinationY) {

            // Write old destination to replay file
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.X, time - delta, x));
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.Y, time - delta, y));

            // Update position of the unit
            this.x = destinationX;
            this.y = destinationY;

            // Make the unit move linearly to its destination from previous to this update call
            float timeBetweenUpdates = GameConfig.values.general.gameUpdatesPerSecond;
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.X, time, x));
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.Y, time, y));
        }
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
