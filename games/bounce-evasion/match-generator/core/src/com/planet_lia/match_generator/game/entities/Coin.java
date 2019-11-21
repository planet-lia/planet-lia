package com.planet_lia.match_generator.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.planet_lia.match_generator.libs.Clickable;
import com.planet_lia.match_generator.libs.replays.*;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Coin implements Clickable {

    private Replay replay;

    public String eid;
    public int x;
    public int y;
    public float size;
    private int layer = 1;

    private Sprite sprite;

    public boolean isPicked = false;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Coin(int id, Replay replay, int x, int y) {
        this.eid = "coin_" + id;
        this.replay = replay;
        this.size = GameConfig.values.coinSize;
        this.x = x;
        this.y = y;


        Texture tex = Assets.get(Assets.coin, Texture.class);
        if (tex != null) {
            sprite = new Sprite(tex);
        }

        float halfTileSize = GameConfig.values.tileSize / 2f;

        // Write coin to replay file
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, 0f, x + halfTileSize));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, 0f, y + halfTileSize));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, 0f, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, 0f, size));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, 0f,
                shortenImagePath(Assets.coin)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, 0f, 1f));
    }

    public void draw(SpriteBatch batch) {
        float tileSize = GameConfig.values.tileSize;

        batch.draw(sprite,
                x + tileSize / 2f - size / 2f,
                y + tileSize / 2f - size / 2f,
                size / 2f, size / 2f,
                size, size,
                1, 1,
                0);
    }

    public void changeLocation(float time, int x, int y) {
        this.x = x;
        this.y = y;
        float halfTileSize = GameConfig.values.tileSize / 2f;

        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, time, x + halfTileSize));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, time, y + halfTileSize));
    }

    @Override
    public String getDisplayText() {
        JsonObject object = new JsonObject();
        object.addProperty("id", eid);
        object.addProperty("x", x);
        object.addProperty("y", y);
        return gson.toJson(object);
    }

    @Override
    public float getX() {
        return x + GameConfig.values.tileSize / 2f;
    }

    @Override
    public float getY() {
        return y + GameConfig.values.tileSize / 2f;
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
