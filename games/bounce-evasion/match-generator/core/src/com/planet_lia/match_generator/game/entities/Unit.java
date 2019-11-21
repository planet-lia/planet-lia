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
import com.planet_lia.match_generator.game.api.commands.Direction;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Unit implements Clickable {

    private Replay replay;

    public String eid;
    public int x;
    public int y;
    public float size;

    private int layer = 2;
    private Sprite sprite;

    public int destinationX;
    public int destinationY;

    public boolean willDie;

    public Direction previousDirection;
    public Direction currentDirection;

    public float rotation;

    public int points;
    public int lives;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Unit(int id, int x, int y, Replay replay, String assetPath, Direction startDirection) {
        this.replay = replay;
        this.eid = "unit_" + id;
        this.x = x;
        this.y = y;
        this.destinationX = x;
        this.destinationY = y;
        this.size = GameConfig.values.unitSize;
        this.lives = GameConfig.values.unitLives;
        this.currentDirection = startDirection;

        Texture tex = Assets.get(assetPath, Texture.class);
        if (tex != null) {
            sprite = new Sprite(tex);
        }

        float halfTileSize = GameConfig.values.tileSize / 2f;

        // Write unit to replay file
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, 0f, x + halfTileSize));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, 0f, y + halfTileSize));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, 0f, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, 0f, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.SCALE, 0f, 1f));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, 0f,
                shortenImagePath(assetPath)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, 0f, 3f));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.VISIBILITY, 0f, 1f));
        rotation = (startDirection == Direction.RIGHT) ? 0f : 180f;
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.ROTATION_DEG, 0f, rotation));
    }

    public void move(Direction direction, Background background) {
        if (lives == 0) return;

        previousDirection = currentDirection;
        currentDirection = direction;

        int tileSize = GameConfig.values.tileSize;

        switch (direction) {
            case LEFT: destinationX = x - tileSize; break;
            case RIGHT: destinationX = x + tileSize; break;
            case UP: destinationY = y + tileSize; break;
            case DOWN: destinationY = y - tileSize; break;
        }

        if (isOutOfBounds(background, destinationX, destinationY)) {
            willDie = true;
        }
    }

    private boolean isOutOfBounds(Background background, int x, int y) {
        if (x < 0 || y < 0 || x >= GameConfig.values.mapWidth || y >= GameConfig.values.mapHeight) {
            return true;
        }
        return background.tiles.get(y).get(x) == null;
    }

    public void addPoint(float time) {
        this.points++;

        float halfTileSize = GameConfig.values.tileSize / 2f;

        String eidParticle = "PARTICLE_" + eid + "_points_" + this.points;
        replay.sections.add(new StepSection(eidParticle, ParticleEntityAttribute.X, time, x + halfTileSize));
        replay.sections.add(new StepSection(eidParticle, ParticleEntityAttribute.Y, time, y + halfTileSize));
        replay.sections.add(new TextSection(eidParticle, ParticleEntityAttribute.EFFECT, time, "pickup"));
        replay.sections.add(new BooleanSection(eidParticle, ParticleEntityAttribute.EMIT, time, true));
        replay.sections.add(new StepSection(eidParticle, ParticleEntityAttribute.LAYER, time, 20f));
    }

    public void removeLife(float time) {
        if (lives != 0) lives--;

        float halfTileSize = GameConfig.values.tileSize / 2f;

        String eidParticle = "PARTICLE_" + eid + "_lives_" + this.lives;
        replay.sections.add(new StepSection(eidParticle, ParticleEntityAttribute.X, time, x + halfTileSize));
        replay.sections.add(new StepSection(eidParticle, ParticleEntityAttribute.Y, time, y + halfTileSize));
        replay.sections.add(new TextSection(eidParticle, ParticleEntityAttribute.EFFECT, time, "pickup"));
        replay.sections.add(new BooleanSection(eidParticle, ParticleEntityAttribute.EMIT, time, true));
        replay.sections.add(new StepSection(eidParticle, ParticleEntityAttribute.LAYER, time, 20f));
        if (lives == 0) {
            replay.sections.add(new StepSection(eid, TextureEntityAttribute.VISIBILITY, time, 1f));
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.VISIBILITY, time + 0.2f, 0f));
        }
    }


    public void update(float time, float delta) {
        // If the move command was issued in previous turn, move the unit
        if (lives == 0) return;

        if (x != destinationX || y != destinationY) {

            float halfTileSize = GameConfig.values.tileSize / 2f;

            // Handle rotation
            if (previousDirection != currentDirection) {
                rotation += getRotationDelta(previousDirection, currentDirection);
                replay.sections.add(new StepSection(eid, TextureEntityAttribute.ROTATION_DEG, time - delta, rotation));
            }

            // Update position of the unit
            this.x = destinationX;
            this.y = destinationY;

            // Make the unit move linearly to its destination from previous to this update call
            if (willDie) {
                willDie = false;
                lives = 0;
                replay.sections.add(new StepSection(eid, TextureEntityAttribute.SCALE, time, 1f));
                replay.sections.add(new LinearSection(eid, TextureEntityAttribute.SCALE, time + 1, 0f));
            }
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.X, time, x + halfTileSize));
            replay.sections.add(new LinearSection(eid, TextureEntityAttribute.Y, time, y + halfTileSize));
        }
    }

    private float getRotationDelta(Direction previousDirection, Direction currentDirection) {
        if (previousDirection == Direction.LEFT && currentDirection == Direction.UP) return -90f;
        if (previousDirection == Direction.LEFT && currentDirection == Direction.RIGHT) return -180f;
        if (previousDirection == Direction.LEFT && currentDirection == Direction.DOWN) return 90f;
        if (previousDirection == Direction.UP && currentDirection == Direction.RIGHT) return -90f;
        if (previousDirection == Direction.UP && currentDirection == Direction.DOWN) return 180f;
        if (previousDirection == Direction.UP && currentDirection == Direction.LEFT) return 90f;
        if (previousDirection == Direction.RIGHT && currentDirection == Direction.DOWN) return -90f;
        if (previousDirection == Direction.RIGHT && currentDirection == Direction.LEFT) return 180f;
        if (previousDirection == Direction.RIGHT && currentDirection == Direction.UP) return 90f;
        if (previousDirection == Direction.DOWN && currentDirection == Direction.LEFT) return -90f;
        if (previousDirection == Direction.DOWN && currentDirection == Direction.UP) return -180f;
        if (previousDirection == Direction.DOWN && currentDirection == Direction.RIGHT) return 90f;
        return 0f;
    }

    public void draw(SpriteBatch batch) {
        float tileSize = GameConfig.values.tileSize;

        batch.draw(sprite,
                x + tileSize / 2f - size / 2f,
                y + tileSize / 2f - size / 2f,
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
        object.addProperty("points", points);
        object.addProperty("lives", lives);
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
