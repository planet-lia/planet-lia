package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BaseEntity {
    public abstract void update(Timer timer, float delta);
    public abstract void draw(SpriteBatch batch);
    public abstract String getDisplayText();
    public abstract float getX();
    public abstract float getY();
    public abstract float getWidth();
    public abstract float getHeight();
    public abstract int getLayer();
}
