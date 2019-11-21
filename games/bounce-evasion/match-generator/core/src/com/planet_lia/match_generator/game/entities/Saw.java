package com.planet_lia.match_generator.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.libs.Clickable;
import com.planet_lia.match_generator.libs.replays.*;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Saw implements Clickable {

    public enum SawDirection {
        UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    private Replay replay;

    public String eid;
    public int x;
    public int y;
    public float size;
    private int layer = 4;

    private Sprite sprite;
    public SawDirection direction;
    public int sawSpawnDelay;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Saw(float time, int id, Replay replay, int x, int y, SawDirection direction) {
        this.eid = "saw_" + id;
        this.replay = replay;
        this.size = GameConfig.values.sawSize;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.sawSpawnDelay = GameConfig.values.sawSpawnUpdatesDelay;

        Texture tex = Assets.get(Assets.saw, Texture.class);
        if (tex != null) {
            sprite = new Sprite(tex);
        }

        float halfTileSize = GameConfig.values.tileSize / 2f;

        // Write coin to replay file
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, time, x + halfTileSize));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, time, y + halfTileSize));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, time, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, time, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.ROTATION_DEG, time, 0f));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, time,
                shortenImagePath(Assets.saw)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, time, 4f));
    }

    public void update(float time, float delta) {
        // Update position
        switch (direction) {
            case UP_LEFT: {
                x -= 1;
                y += 1;
                break;
            }
            case UP_RIGHT: {
                x += 1;
                y += 1;
                break;
            }
            case DOWN_LEFT: {
                x -= 1;
                y -= 1;
                break;
            }
            case DOWN_RIGHT: {
                x += 1;
                y -= 1;
                break;
            }
        }

        boolean changed = false;
        // Change direction
        switch (direction) {
            case UP_LEFT: {
                //  Hit left and top border
                if (x == 0 && y == GameConfig.values.mapHeight - 1) {
                    direction = SawDirection.DOWN_RIGHT;
                    changed = true;
                }
                // Hit left border
                else if (x == 0) {
                    direction = SawDirection.UP_RIGHT;
                    changed = true;
                }
                // Hit top border
                else if (y == GameConfig.values.mapHeight - 1) {
                    direction = SawDirection.DOWN_LEFT;
                    changed = true;
                }
                break;
            }
            case UP_RIGHT: {
                //  Hit right and top border
                if (x == GameConfig.values.mapWidth - 1 && y == GameConfig.values.mapHeight - 1) {
                    direction = SawDirection.DOWN_LEFT;
                    changed = true;
                }
                // Hit right border
                else if (x == GameConfig.values.mapWidth - 1) {
                    direction = SawDirection.UP_LEFT;
                    changed = true;
                }
                // Hit top border
                else if (y == GameConfig.values.mapHeight - 1) {
                    direction = SawDirection.DOWN_RIGHT;
                    changed = true;
                }
                break;
            }
            case DOWN_LEFT: {
                //  Hit left and bottom border
                if (x == 0 && y == 0) {
                    direction = SawDirection.UP_RIGHT;
                    changed = true;
                }
                // Hit left border
                else if (x == 0) {
                    direction = SawDirection.DOWN_RIGHT;
                    changed = true;
                }
                // Hit top border
                else if (y == 0) {
                    direction = SawDirection.UP_LEFT;
                    changed = true;
                }
                break;
            }
            case DOWN_RIGHT: {
                //  Hit right and bottom border
                if (x == GameConfig.values.mapWidth - 1 && y == 0) {
                    direction = SawDirection.UP_LEFT;
                    changed = true;
                }
                // Hit right border
                else if (x == GameConfig.values.mapWidth - 1) {
                    direction = SawDirection.DOWN_LEFT;
                    changed = true;
                }
                // Hit top border
                else if (y == 0) {
                    direction = SawDirection.UP_RIGHT;
                    changed = true;
                }
                break;
            }
        }

        if (changed) {
            float halfTileSize = GameConfig.values.tileSize / 2f;
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.X, time, x + halfTileSize));
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.Y, time, y + halfTileSize));
        }
    }

    public void savePositionAndRotation(float time) {
        float halfTileSize = GameConfig.values.tileSize / 2f;
        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.X, time, x + halfTileSize));
        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.Y, time, y + halfTileSize));

        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.ROTATION_DEG, time, time * 360 * 5));

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
