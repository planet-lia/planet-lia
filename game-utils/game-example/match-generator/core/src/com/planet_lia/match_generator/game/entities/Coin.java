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

    public String eid = "coin";
    public float x;
    public float y;
    public float size;
    private int layer = 1;

    private Sprite sprite;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Coin(Replay replay) {
        this.replay = replay;
        this.size = GameConfig.values.coinSize;

        x = GameConfig.values.mapWidth / 2f;
        y =GameConfig.values.mapHeight / 2f;


        Texture tex = Assets.get(Assets.coin, Texture.class);
        if (tex != null) {
            sprite = new Sprite(tex);
        }

        // Write coin to replay file
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, 0f, x));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, 0f, y));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, 0f, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, 0f, size));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, 0f,
                shortenImagePath(Assets.coin)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, 0f, 1f));
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
