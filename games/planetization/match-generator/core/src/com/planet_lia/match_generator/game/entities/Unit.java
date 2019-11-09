package com.planet_lia.match_generator.game.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.game.GameLogic;
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

    public Planet currentPlanet;
    public Planet destinationPlanet;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Unit(GameLogic gameLogic, Replay replay, int unitId, Type type, Owner owner, Planet planet, float time) {
        this.gameLogic = gameLogic;
        this.unitId = unitId;
        this.eid = "unit_" + owner.toString() + "_" + unitId;
        this.type = type;
        this.owner = owner;
        this.currentPlanet = planet;
        this.rotation = 0;
        this.health = 100;
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
        this.x = planet.x;
        this.y = planet.y;

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
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, time, 2));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.VISIBILITY, time, 1));
    }

    public void goTo(Planet planet, float time) {

//        HipsterDirectedGraph<String,Double> graph =
//                GraphBuilder.<String,Double>create()
//                        .connect("A").to("B").withEdge(4d)
//                        .connect("A").to("C").withEdge(2d)
//                        .connect("B").to("C").withEdge(5d)
//                        .connect("B").to("D").withEdge(10d)
//                        .connect("C").to("E").withEdge(3d)
//                        .connect("D").to("F").withEdge(11d)
//                        .connect("E").to("D").withEdge(4d)
//                        .createDirectedGraph();
//
//        SearchProblem p = GraphSearchProblem
//                .startingFrom("A")
//                .in(graph)
//                .takeCostsFromEdges()
//                .build();
//
//        System.out.println(Hipster.createDijkstra(p).search("F"));


        if (planet != currentPlanet) {
            destinationPlanet = planet;
            if (currentPlanet != null) {
                startFromAPlanet(time);
            }
            saveLocationAndRotation(time);
        }
    }

    public void battle(Unit.Type opponentType, float time) {
        if (type == Type.WORKER || opponentType == Type.WARRIOR) {
            health = 0;
        }
        else {
            health -= 50;
        }
        if (health <= 0) {
            if (owner == Owner.RED) gameLogic.redUnitsToRemove.add(this);
            else gameLogic.greenUnitsToRemove.add(this);
            if (currentPlanet != null) {
                currentPlanet.removeUnit(this, time);
            }
            saveLocationAndRotation(time);
            replay.sections.add(new StepSection(eid, TextureEntityAttribute.VISIBILITY, time, 0));
        }
    }

    private void startFromAPlanet(float time) {
        if (currentPlanet != null) {
            currentPlanet.removeUnit(this, time);
            currentPlanet = null;
            rotation += angleBetweenUnitAndDestination(destinationPlanet.x, destinationPlanet.y);
        }
    }

    public void update(float time, float delta) {
        if (destinationPlanet != null) {
            moveToDestination(time, delta, destinationPlanet.x, destinationPlanet.y);
        }
    }

    public void saveLocationAndRotation(float time) {
        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.X, time, x));
        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.Y, time, y));
        replay.sections.add(new LinearSection(eid, TextureEntityAttribute.ROTATION_DEG, time, rotation));
    }

    /** Moves to destination. */
    private void moveToDestination(float time, float delta, float destX, float destY) {
        // If we are already at the destination then stop the unit
        // (Method defined in point 1.)
        if (atDestination()) {
            saveLocationAndRotation(time);
            addUnitToPlanet(destinationPlanet, time);
            saveLocationAndRotation(time);
            destinationPlanet = null;
            return;
        }

        // Calculate the angle between unit and next node
        float angle = angleBetweenUnitAndDestination(destX, destY);
        float absAngle = Math.abs(angle);

        float rotationSpeed = GameConfig.values.unitRotationSpeed;
        float unitSpeed = GameConfig.values.unitSpeed;

        if (absAngle > 5) {
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
    }

    private void addUnitToPlanet(Planet planet, float time) {
        planet.unitArrived(this, time);
        x = planet.x;
        y = planet.y;
    }

    private boolean atDestination() {
        float radius = destinationPlanet.size / 2f;
        return (destinationPlanet.x - x) * (destinationPlanet.x - x) + (destinationPlanet.y - y) * (destinationPlanet.y - y) < radius * radius;
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
        JsonObject object = new JsonObject();
        object.addProperty("type", "Unit");
        object.addProperty("owner", owner.toString());
        object.addProperty("type", type.toString());
        object.addProperty("id", unitId);
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
        return 2;
    }
}
