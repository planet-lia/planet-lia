package com.planet_lia.match_generator.logic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.planet_lia.match_generator.libs.BaseEntity;
import com.planet_lia.match_generator.libs.Timer;

public class BoxEntity extends BaseEntity {
    public float x,  y, width, height;

    int layer;
    Texture texture;

    float nextWidth;

    public BoxEntity(float x, float y, float width, float height, int layer) {
        this.x = x;
        this.y = y;
        this.width = width * 0.1f;
        this.height = height * 0.1f;
        this.layer = layer;

        nextWidth = this.width / 2f;

        texture = Assets.get(Assets.block, Texture.class);
    }

    @Override
    public void update(Timer timer, float delta) {
        float tmp = width;
        width = nextWidth;
        nextWidth = tmp;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x - width / 2f, y - height / 2f, width, height);
    }

    @Override
    public String getDisplayText() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
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
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public int getLayer() {
        return layer;
    }
}
