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
import com.planet_lia.match_generator.libs.Clickable;
import com.planet_lia.match_generator.libs.replays.Replay;
import com.planet_lia.match_generator.libs.replays.StepSection;
import com.planet_lia.match_generator.libs.replays.TextSection;
import com.planet_lia.match_generator.libs.replays.TextureEntityAttribute;

import java.util.ArrayList;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Planet implements Clickable {

    Replay replay;

    public int planetId;
    private String eid;
    public Owner owner;
    public String asset;
    public Sprite sprite;
    public float x, y, size;
    public float resources = 0;
    public int indicatorIdCount = 0;

    public String workerIndicatorAsset;
    public Sprite workerIndicatorSprite;
    public String warriorIndicatorAsset;
    public Sprite warriorIndicatorSprite;

    int nUnitIndicatorSpots = 20;
    float indicatorSize = GameConfig.values.unitIndicatorSize;

    public ArrayList<Unit> unitsOnPlanet = new ArrayList<>();

    private ArrayList<UnitIndicator> workerIndicators = new ArrayList<>();
    private ArrayList<UnitIndicator> warriorIndicators = new ArrayList<>();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Planet(Replay replay, int planetId, float x, float y, Owner owner) {
        this.planetId = planetId;
        this.eid = "planet_" + planetId;
        this.owner = owner;
        this.replay = replay;

        switchAsset(owner);

        this.size = GameConfig.values.planetSize;
        this.x = x;
        this.y = y;

        writeToReplay();
    }

    private void addUnitIndicator(Unit.Type type, float time) {
        boolean nUnitIndicatorSpotsChanged = false;
        if (nUnitIndicatorSpots <= getNumberOfWarriors() + getNumberOfWorkers()) {
            // Make nUnitIndicatorSpots larger
            nUnitIndicatorSpots += 20;
            nUnitIndicatorSpotsChanged = true;
        }
        else if (nUnitIndicatorSpots - 22 > getNumberOfWarriors() + getNumberOfWorkers()) {
            nUnitIndicatorSpots -= 20;
            nUnitIndicatorSpotsChanged = true;
        }

        float angleOffset = 360f / nUnitIndicatorSpots;
        indicatorSize = GameConfig.values.unitIndicatorSize * (20f / nUnitIndicatorSpots);

        if (nUnitIndicatorSpotsChanged) {
            // Rearrange indicators
            for (int i = 0; i < workerIndicators.size(); i++) {
                UnitIndicator indicator = workerIndicators.get(i);
                indicator.location.set(GameConfig.values.unitIndicatorOffset, 0);
                indicator.location.setAngle(270 - angleOffset / 2f - i * angleOffset).add(x, y);
                indicator.size = indicatorSize;
                saveIndicatorPositionAndSize(time, indicator);
            }
            for (int i = 0; i < warriorIndicators.size(); i++) {
                UnitIndicator indicator = warriorIndicators.get(i);
                indicator.location.set(GameConfig.values.unitIndicatorOffset, 0);
                indicator.location.setAngle(270 + angleOffset / 2f + i * angleOffset).add(x, y);
                indicator.size = indicatorSize;
                saveIndicatorPositionAndSize(time, indicator);
            }
        }

        if (type == Unit.Type.WORKER) {
            int nWorkers = getNumberOfWorkers();
            Vector2 v = new Vector2(GameConfig.values.unitIndicatorOffset, 0);
            v.setAngle(270 - angleOffset / 2f - (nWorkers - 1) * angleOffset).add(x, y);
            UnitIndicator indicator = new UnitIndicator(getNextIndicatorId(), v, owner, indicatorSize);
            workerIndicators.add(indicator);
            saveIndicatorToReplay(time, indicator, workerIndicatorAsset);
        }
        else {
            int nWarriors = getNumberOfWarriors();
            Vector2 v = new Vector2(GameConfig.values.unitIndicatorOffset, 0);
            v.setAngle(270 + angleOffset / 2f + (nWarriors - 1) * angleOffset).add(x, y);
            UnitIndicator indicator = new UnitIndicator(getNextIndicatorId(), v, owner, indicatorSize);
            warriorIndicators.add(indicator);
            saveIndicatorToReplay(time, indicator, warriorIndicatorAsset);
        }
    }

    private String getNextIndicatorId() {
        return eid + "_" + indicatorIdCount++;
    }

    private void saveIndicatorToReplay(float time, UnitIndicator indicator, String indicatorAsset) {
        String indicatorId = indicator.eid;
        saveIndicatorPositionAndSize(time, indicator);
        replay.sections.add(new TextSection(indicatorId, TextureEntityAttribute.TEXTURE, time,
                shortenImagePath(indicatorAsset)));
        replay.sections.add(new StepSection(indicatorId, TextureEntityAttribute.LAYER, time, 1));
        replay.sections.add(new StepSection(indicatorId, TextureEntityAttribute.VISIBILITY, time, 1));
    }

    private void saveIndicatorPositionAndSize(float time, UnitIndicator indicator) {
        String indicatorId = indicator.eid;
        replay.sections.add(new StepSection(indicatorId, TextureEntityAttribute.X, time, indicator.location.x));
        replay.sections.add(new StepSection(indicatorId, TextureEntityAttribute.Y, time, indicator.location.y));
        replay.sections.add(new StepSection(indicatorId, TextureEntityAttribute.WIDTH, time, indicator.size));
        replay.sections.add(new StepSection(indicatorId, TextureEntityAttribute.HEIGHT, time, indicator.size));
    }


    private void removeUnitIndicator(Unit.Type type, float time) {
        UnitIndicator indicator;
        if (type == Unit.Type.WORKER) {
            indicator = workerIndicators.get(workerIndicators.size() - 1);
            workerIndicators.remove(workerIndicators.size() - 1);
        }
        else {
            indicator = warriorIndicators.get(warriorIndicators.size() - 1);
            warriorIndicators.remove(warriorIndicators.size() - 1);
        }
        replay.sections.add(new StepSection(indicator.eid, TextureEntityAttribute.VISIBILITY, time, 0));
    }

    private void switchAsset(Owner owner) {
        switch (owner) {
            case NONE: {
                asset = Assets.planetGrey;
                break;
            }
            case RED: {
                asset = Assets.planetRed;
                workerIndicatorAsset = Assets.redIndicator;
            } break;
            case GREEN: {
                asset = Assets.planetGreen;
                workerIndicatorAsset = Assets.greenIndicator;
            } break;
        }
        warriorIndicatorAsset = Assets.whiteIndicator;

        if (sprite == null) sprite = new Sprite(Assets.get(asset, Texture.class));
        sprite.setTexture(Assets.get(asset, Texture.class));

        if (owner != Owner.NONE) {
            if (workerIndicatorSprite == null) workerIndicatorSprite = new Sprite(Assets.get(workerIndicatorAsset, Texture.class));
            workerIndicatorSprite.setTexture(Assets.get(workerIndicatorAsset, Texture.class));

            if (warriorIndicatorSprite == null) warriorIndicatorSprite = new Sprite(Assets.get(warriorIndicatorAsset, Texture.class));
            warriorIndicatorSprite.setTexture(Assets.get(warriorIndicatorAsset, Texture.class));
        }
    }

    private void writeToReplay() {
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, 0f, x));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, 0f, y));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, 0f, size));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, 0f, size));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, 0f,
                shortenImagePath(asset)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, 0f, 3));
    }

    public void unitArrived(Unit unit, float time) {
        if (owner == unit.owner) {
            unitsOnPlanet.add(unit);
            unit.currentPlanet = this;
            addUnitIndicator(unit.type, time);
        }
        else if (owner != Owner.NONE) {
            // Battle units
            while (unit.health > 0 && !unitsOnPlanet.isEmpty()) {
                for (int i = 0; i < unitsOnPlanet.size(); i++) {
                    Unit ownerUnit = unitsOnPlanet.get(i);
                    // Preferably battle against the warrior else with the last worker
                    if (ownerUnit.type == Unit.Type.WARRIOR || i == unitsOnPlanet.size() - 1) {
                        // Battle
                        ownerUnit.battle(unit.type, time);
                        unit.battle(ownerUnit.type, time);
                    }
                }
            }
        }

        if (unitsOnPlanet.isEmpty() && unit.health > 0) {
            owner = unit.owner;
            switchAsset(owner);
            unitsOnPlanet.add(unit);
            unit.currentPlanet = this;
            addUnitIndicator(unit.type, time);
            replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, time, shortenImagePath(asset)));
        }
    }

    public int getNumberOfWorkers() {
        int count = 0;
        for (Unit unit : unitsOnPlanet) {
            if (unit.type == Unit.Type.WORKER) count++;
        }
        return count;
    }

    public int getNumberOfWarriors() {
        int count = 0;
        for (Unit unit : unitsOnPlanet) {
            if (unit.type == Unit.Type.WARRIOR) count++;
        }
        return count;
    }

    public void update(float delta) {
        if (owner != Owner.NONE) {
            resources += Math.min(getNumberOfWorkers(), GameConfig.values.maxActiveWorkersPerPlanet)
                    * GameConfig.values.resourcesGenerationSpeed * delta;
            if (resources > GameConfig.values.resourcesForNewUnit) {
                resources = GameConfig.values.resourcesForNewUnit;
            }
        }
    }

    public void removeUnit(Unit unit, float time) {
        unitsOnPlanet.remove(unit);
        removeUnitIndicator(unit.type, time);
        if (unitsOnPlanet.isEmpty()) {
            owner = Owner.NONE;
            switchAsset(owner);
            replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, time, shortenImagePath(asset)));
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(sprite, x - size / 2f, y - size / 2f, size, size);

        for (UnitIndicator indicator : workerIndicators) {
            batch.draw(workerIndicatorSprite,
                    indicator.location.x - indicatorSize / 2f,
                    indicator.location.y - indicatorSize / 2f,
                    indicatorSize, indicatorSize);
        }
        for (UnitIndicator indicator : warriorIndicators) {
            batch.draw(warriorIndicatorSprite,
                    indicator.location.x - indicatorSize / 2f,
                    indicator.location.y - indicatorSize / 2f,
                    indicatorSize, indicatorSize);
        }
    }

    @Override
    public String getDisplayText() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "Planet");
        object.addProperty("owner", owner.toString());
        object.addProperty("id", planetId);
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("diameter", size);
        object.addProperty("resources", resources);
        object.addProperty("numberOfWorkers", getNumberOfWorkers());
        object.addProperty("numberOfWarriors", getNumberOfWarriors());
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
        return 3;
    }
}
