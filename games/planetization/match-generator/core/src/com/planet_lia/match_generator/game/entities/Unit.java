package com.planet_lia.match_generator.game.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.game.GameLogic;
import com.planet_lia.match_generator.game.pathfinding.PathNode;
import com.planet_lia.match_generator.libs.Clickable;
import com.planet_lia.match_generator.libs.replays.*;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Unit implements Clickable {

    public enum Type {
        WORKER, WARRIOR
    }

    private GameLogic gameLogic;

    private Replay replay;

    public int unitId;
    public String eid;
    public Type type;
    public Owner owner;
    private String asset;
    private Sprite sprite;
    public float x, y, size;
    public float rotation;
    public float health;
    public float attack;

    public Planet currentPlanet;
    public Planet destinationPlanet;

    public Array<PathNode> currentPath;
    public int currentPathNodeIndex;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Unit(GameLogic gameLogic, Replay replay, int unitId, Type type, Owner owner, Planet planet, float time) {
        this.gameLogic = gameLogic;
        this.unitId = unitId;
        this.eid = "unit_" + owner.toString() + "_" + unitId;
        this.type = type;
        this.owner = owner;
        this.currentPlanet = planet;
        this.rotation = 0;
        this.health = (type == Type.WORKER) ? GameConfig.values.workerHealth : GameConfig.values.warriorHealth;
        this.attack = (type == Type.WORKER) ? GameConfig.values.workerAttack : GameConfig.values.warriorAttack;
        this.replay = replay;

        if (type == Type.WORKER && owner == Owner.RED) asset = Assets.redWorker;
        if (type == Type.WARRIOR && owner == Owner.RED) asset = Assets.redWarrior;
        if (type == Type.WORKER && owner == Owner.GREEN) asset = Assets.greenWorker;
        if (type == Type.WARRIOR && owner == Owner.GREEN) asset = Assets.greenWarrior;

        Texture texture = Assets.get(asset, Texture.class);
        if (texture != null) {
            sprite = new Sprite(texture);
        }

        this.size = GameConfig.values.unitSize;
        updateUnitPositionOnPlanet(planet, time);

        writeToReplay(time);
    }

    private void writeToReplay(float time) {
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, time, x));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, time, y));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, time, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, time, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.ROTATION_DEG, time, rotation));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, time,
                shortenImagePath(asset)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, time, 1));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.VISIBILITY, time, 1));
    }

    public void goTo(Planet destination, float time) {
        if (destination != currentPlanet && currentPlanet != null) {
            destinationPlanet = destination;

            if (canGoDirectlyToPlanet(destination)) {
                currentPath = new Array<>(); // It will go directly to destinationPlanet
            }
            else {
                // Find the path
                PathNode startNode = findStartingPathNode(currentPlanet, destination.x, destination.y);
                PathNode endNode = findStartingPathNode(destination, startNode.x, startNode.y);
                currentPath = startNode.allPaths.get(endNode.getIndex()).nodes;
            }

            currentPathNodeIndex = 0;

            startFromAPlanet(time);
            saveLocationAndRotation(time);
        }
    }

    private boolean canGoDirectlyToPlanet(Planet destination) {
        return currentPlanet.directlyAccessiblePlanetIds.contains(destination.planetId);
    }

    private PathNode findStartingPathNode(Planet start, float x, float y) {
        PathNode startNode = null;
        for (PathNode node : start.closePathNodes) {
            if (startNode == null
                    || Vector2.dst(startNode.x, startNode.y, x, y)
                    > Vector2.dst(node.x, node.y, x, y)) {
                startNode = node;
            }
        }
        return startNode;
    }

    public void dealDamage(float attack, float time) {
        if (health <= 0) return;

        health -= attack;

        if (health <= 0) {
            if (owner == Owner.RED) gameLogic.redUnitsToRemove.add(this);
            else gameLogic.greenUnitsToRemove.add(this);
            if (currentPlanet != null) {
                currentPlanet.removeUnit(this, time);
            }
            else {
                String eidParticle = "PARTICLE_" + owner.toString() + "_" + unitId;
                replay.sections.add(new StepSection(eidParticle, ParticleEntityAttribute.X, time, x));
                replay.sections.add(new StepSection(eidParticle, ParticleEntityAttribute.Y, time, y));
                replay.sections.add(new TextSection(eidParticle, ParticleEntityAttribute.EFFECT, time, "unit-died"));
                replay.sections.add(new BooleanSection(eidParticle, ParticleEntityAttribute.EMIT, time, true));
            }
            saveLocationAndRotation(time);
            replay.sections.add(new StepSection(eid, TextureEntityAttribute.VISIBILITY, time, 0));
        }
    }

    private void startFromAPlanet(float time) {
        if (currentPlanet != null) {
            currentPlanet.removeUnit(this, time);
            currentPlanet = null;
            float x, y;
            if (currentPath.size == 0) {
                x = destinationPlanet.x;
                y = destinationPlanet.y;
            } else {
                PathNode nextNode = currentPath.get(currentPathNodeIndex);
                x = nextNode.x;
                y = nextNode.y;
            }
            rotation += angleBetweenUnitAndDestination(x, y);
        }
    }

    public void update(float time, float delta) {
        if (health <= 0) return;

        if (destinationPlanet == null || currentPath == null) return;

        float nextX;
        float nextY;
        float minOffset;
        if (currentPath.size <= currentPathNodeIndex) {
            nextX = destinationPlanet.x;
            nextY = destinationPlanet.y;
            minOffset = destinationPlanet.size / 2f;
        } else {
            nextX = currentPath.get(currentPathNodeIndex).x;
            nextY = currentPath.get(currentPathNodeIndex).y;
            minOffset = GameConfig.values.unitSize * 0.6f;
        }

        boolean arrived = moveToDestination(time, delta, nextX, nextY, minOffset);
        if (arrived) {
            saveLocationAndRotation(time);
            if (currentPath.size <= currentPathNodeIndex) {
                destinationPlanet.unitArrived(this, time);
                updateUnitPositionOnPlanet(destinationPlanet, time);
                saveLocationAndRotation(time);
                currentPath = null;
                currentPathNodeIndex = 0;
                destinationPlanet = null;
            }
            else {
                currentPathNodeIndex++;
            }
        }
    }

    public void saveLocationAndRotation(float time) {
        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.X, time, x));
        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.Y, time, y));
        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.ROTATION_DEG, time, rotation));
    }

    /** Moves to destination. */
    private boolean moveToDestination(float time, float delta, float destX, float destY, float minOffset) {
        // If we are already at the destination then stop the unit
        // (Method defined in point 1.)
        if (atDestination(destX, destY, minOffset)) {
            return true;
        }

        // Calculate the angle between unit and next node
        float angle = angleBetweenUnitAndDestination(destX, destY);
        float absAngle = Math.abs(angle);

        float rotationSpeed = GameConfig.values.unitRotationSpeed;
        float unitSpeed = GameConfig.values.unitSpeed;

        if (absAngle > 9) {
            if (angle < 0) rotation -= rotationSpeed * delta;
            else rotation += rotationSpeed * delta;
        }
        else {
            if (angle != 0) {
                rotation += angle;
                saveLocationAndRotation(time);
            }
            float velX = (float) (unitSpeed * Math.cos(Math.toRadians(rotation)));
            float velY = (float) (unitSpeed * Math.sin(Math.toRadians(rotation)));
            x += velX * delta;
            y += velY * delta;
        }

        return false;
    }

    private void updateUnitPositionOnPlanet(Planet planet, float time) {
        // Offset position of the unit a little bit for nicer visuals
        float offset = GameConfig.values.planetSize * 0.25f;
        x = planet.x + GameConfig.random.nextFloat() * offset * 2 - offset;
        y = planet.y + GameConfig.random.nextFloat() * offset * 2 - offset;
    }

    private boolean atDestination(float destX, float destY, float minOffset) {
        return (destX - x) * (destX - x) + (destY - y) * (destY - y) < minOffset * minOffset;
    }

    private Vector2 unitToDest = new Vector2();

    private float angleBetweenUnitAndDestination(float destX, float destY) {
        // Create a vector from the unit to the destination by subtracting
        // base unit location vector from base destination vector
        unitToDest.set(destX, destY);
        unitToDest.sub(x, y);

        // Calculate the orientation angle (180° different if the unit moves backwards)
        float orientationAngle = rotation;

        // Get the angle between unitToDest and unit orientationAngle vectors
        float angle = unitToDest.angle() - orientationAngle;

        // Angles can always be represented with a positive or negative value that
        // is smaller than 180°. Here this optimization helps us so that later we
        // know better to which direction we should rotate.
        if (angle > 180)
            angle -= 360f;
        else if (angle < -180) angle += 360f;

        return angle;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(sprite,
                x - size / 2f, y - size / 2f,
                size / 2f, size / 2f,
                size, size,
                1f, 1f,
                rotation
        );
    }

    @Override
    public String getDisplayText() {
        return gson.toJson(getDisplayTextJsonObject());
    }

    public JsonObject getDisplayTextJsonObject() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "Unit");
        object.addProperty("owner", owner.toString());
        object.addProperty("type", type.toString());
        object.addProperty("id", unitId);
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("size", size);
        object.addProperty("health", health);
        if (destinationPlanet != null) {
            object.addProperty("destinationPlanetId", destinationPlanet.planetId);
        }
        return object;
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
        return 2;
    }
}
