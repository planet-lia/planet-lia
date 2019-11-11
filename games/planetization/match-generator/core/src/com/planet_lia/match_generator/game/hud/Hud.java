package com.planet_lia.match_generator.game.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.game.entities.Owner;
import com.planet_lia.match_generator.game.entities.Unit;
import com.planet_lia.match_generator.libs.BotDetails;
import com.planet_lia.match_generator.libs.replays.*;

import java.util.ArrayList;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Hud {
    Replay replay;

    // Hud background
    Sprite bgSprite;
    float bgWidth = GameConfig.values.cameraViewWidth * 0.5f;
    float bgHeight = GameConfig.values.cameraViewHeight * 0.06f;
    float bgX = GameConfig.values.cameraViewWidth * 0.5f;
    float bgY = GameConfig.values.cameraViewHeight - bgHeight * 0.5f;

    // Power bars
    float pbHeight = GameConfig.values.cameraViewHeight * 0.01f;
    float pbY = bgY - bgHeight / 2f - pbHeight * 0.5f;
    float pbGreenWidth = bgWidth / 2f;
    float pbRedWidth = bgWidth / 2f;
    float pbGreenX = bgX - bgWidth / 4f;
    float pbRedX = bgX + bgWidth / 4f;
    Sprite pbGreenSprite;
    Sprite pbRedSprite;
    String pbRedId = "HUD_red_pb";
    String pbGreenId = "HUD_green_pb";

    String greenHex = GameConfig.values.general.botColors[0];
    String redHex =  GameConfig.values.general.botColors[1];
    Color green = Color.valueOf(greenHex);
    Color red =  Color.valueOf(redHex);

    // Names
    float nameY = bgY;
    float nameOffsetX = GameConfig.values.cameraViewWidth * 0.01f;
    float nameGreenX = bgX - bgWidth / 2f + nameOffsetX;
    float nameRedX = bgX + bgWidth / 2f - nameOffsetX;
    float fontSize = 24;
    String nameGreenText;
    String nameRedText;
    BitmapFont font;

    // Green worker
    float hudItemsY = bgY;
    float offsetSmall = GameConfig.values.cameraViewWidth * 0.02f;
    float offsetBig = GameConfig.values.cameraViewWidth * 0.1f;
    float greenWorkerImageX = nameGreenX + offsetBig;
    float unitImageSize = bgHeight * 0.6f;
    Sprite greenWorkerSprite;

    // Green worker text
    float greenWorkerTextX = greenWorkerImageX + offsetSmall;
    int nGreenWorkers = 0;
    public String greenWorkerTextId = "HUD_TEXT_green_worker";

    // Green warrior
    float greenWarriorImageX = greenWorkerTextX + offsetSmall * 2;
    Sprite greenWarriorSprite;

    // Green warrior text
    float greenWarriorTextX = greenWarriorImageX + offsetSmall;
    int nGreenWarriors = 0;
    public String greenWarriorTextId = "HUD_TEXT_green_warrior";

    // Red warrior
    float redWarriorImageX = nameRedX - offsetBig - offsetSmall;
    Sprite redWarriorSprite;

    // Red warrior text
    float redWarriorTextX = redWarriorImageX + offsetSmall;
    int nRedWarriors = 0;
    public String redWarriorTextId = "HUD_TEXT_red_warrior";

    // Red worker
    float redWorkerImageX = redWarriorImageX - offsetSmall * 3;
    Sprite redWorkerSprite;

    // Red worker text
    float redWorkerTextX = redWorkerImageX + offsetSmall;
    int nRedWorkers = 0;
    public String redWorkerTextId = "HUD_TEXT_red_worker";

    // Time
    float timeX = bgX;
    float timeY = bgY;
    float time = 0f;
    String timeId = "HUD_TEXT_time";

    // Standalone curve ids
    public String nGreenUnitsCurveId = "CURVE_green_units";
    public String nRedUnitsCurveId = "CURVE_red_units";

    public Hud(Replay replay, BotDetails[] botDetails) {
        this.replay = replay;
        this.nameGreenText = botDetails[0].botName;
        this.nameRedText = botDetails[1].botName;

        bgSprite = Assets.setTextureToSprite(bgSprite, Assets.hudBg);
        pbGreenSprite = Assets.setTextureToSprite(pbGreenSprite, Assets.pbGreen);
        pbRedSprite = Assets.setTextureToSprite(pbRedSprite, Assets.pbRed);
        greenWorkerSprite = Assets.setTextureToSprite(greenWorkerSprite, Assets.greenWorker);
        greenWarriorSprite = Assets.setTextureToSprite(greenWarriorSprite, Assets.greenWarrior);
        redWorkerSprite = Assets.setTextureToSprite(redWorkerSprite, Assets.redWorker);
        redWarriorSprite = Assets.setTextureToSprite(redWarriorSprite, Assets.redWarrior);

        font = Assets.get(Assets.hudFont, BitmapFont.class);
        if (font != null) {
            font.usesIntegerPositions();
        }

        writeToReplayFile();
    }

    private void writeToReplayFile() {
        // Background
        String hudBgId = "HUD_bg";
        writeBasicTexture(hudBgId, bgX, bgY, bgWidth, bgHeight, Assets.hudBg, 1);
        replay.sections.add(new StepSection(hudBgId, TextureEntityAttribute.OPACITY, time, 0.5f));

        // Green power bar
        writeBasicTexture(pbGreenId, pbGreenX, pbY, pbGreenWidth, pbHeight, Assets.pbGreen, 2);
        // Green power bar
        writeBasicTexture(pbRedId, pbRedX, pbY, pbRedWidth, pbHeight, Assets.pbRed, 2);

        // Unit images
        writeBasicTexture("HUD_green_worker", greenWorkerImageX, hudItemsY, unitImageSize, unitImageSize,
                Assets.greenWorker, 2);
        writeBasicTexture("HUD_green_warrior", greenWarriorImageX, hudItemsY, unitImageSize, unitImageSize,
                Assets.greenWarrior, 2);
        writeBasicTexture("HUD_red_worker", redWorkerImageX, hudItemsY, unitImageSize, unitImageSize,
                Assets.redWorker, 2);
        writeBasicTexture("HUD_red_warrior", redWarriorImageX, hudItemsY, unitImageSize, unitImageSize,
                Assets.redWarrior, 2);

        // Time
        writeBasicNumericText(timeId, timeX, timeY, 0.5f,  time, 2, "#FFFFFF", 2);

        // Unit texts
        writeBasicNumericText(greenWorkerTextId, greenWorkerTextX, hudItemsY, 0f,  nGreenWorkers, 0,
                greenHex, 2);
        writeBasicNumericText(greenWarriorTextId, greenWarriorTextX, hudItemsY, 0f,  nGreenWarriors, 0,
                greenHex, 2);
        writeBasicNumericText(redWorkerTextId, redWorkerTextX, hudItemsY, 0f,  nRedWorkers, 0,
                redHex, 2);
        writeBasicNumericText(redWarriorTextId, redWarriorTextX, hudItemsY, 0f,  nRedWarriors, 0,
                redHex, 2);

        // Names
        writeBasicText("HUD_TEXT_green_name", nameGreenX, nameY, 0f, nameGreenText, greenHex, 2);
        writeBasicText("HUD_TEXT_red_name", nameRedX, nameY, 1f, nameRedText, redHex, 2);
    }
    private void writeUpdatedPowerBar(String id, float x, float width) {
        replay.sections.add(new StepSection(id, TextureEntityAttribute.X, time,  x));
        replay.sections.add(new StepSection(id, TextureEntityAttribute.WIDTH, time, width));
    }

    private void writeBasicTexture(String id, float x, float y, float width, float height, String asset, float layer) {
        replay.sections.add(new StepSection(id, TextureEntityAttribute.X, time, x));
        replay.sections.add(new StepSection(id, TextureEntityAttribute.Y, time, y));
        replay.sections.add(new StepSection(id, TextureEntityAttribute.WIDTH, time, width));
        replay.sections.add(new StepSection(id, TextureEntityAttribute.HEIGHT, time, height));
        replay.sections.add(new TextSection(id, TextureEntityAttribute.TEXTURE, time,
                shortenImagePath(asset)));
        replay.sections.add(new StepSection(id, TextureEntityAttribute.LAYER, time, layer));
    }

    private void writeBasicText(String id, float x, float y, float anchor, String text, String color, float layer) {
        replay.sections.add(new StepSection(id, TextEntityAttribute.X, time, x));
        replay.sections.add(new StepSection(id, TextEntityAttribute.Y, time, y));
        replay.sections.add(new StepSection(id, TextEntityAttribute.FONT_SIZE, time, fontSize));
        replay.sections.add(new StepSection(id, TextEntityAttribute.ANCHOR_X, time, anchor));
        replay.sections.add(new TextSection(id, TextEntityAttribute.TEXT, time, text));
        replay.sections.add(new TextSection(id, TextEntityAttribute.COLOR, time, color));
        replay.sections.add(new StepSection(id, TextEntityAttribute.LAYER, time, layer));
    }

    private void writeBasicNumericText(String id, float x, float y, float anchor, float numericText,
                                      float precision, String color, float layer) {
        replay.sections.add(new StepSection(id, TextEntityAttribute.X, time, x));
        replay.sections.add(new StepSection(id, TextEntityAttribute.Y, time, y));
        replay.sections.add(new StepSection(id, TextEntityAttribute.FONT_SIZE, time, fontSize));
        replay.sections.add(new StepSection(id, TextEntityAttribute.ANCHOR_X, time, anchor));
        replay.sections.add(new StepSection(id, TextEntityAttribute.NUMBER_TEXT, time, numericText));
        replay.sections.add(new StepSection(id, TextEntityAttribute.NUMBER_TEXT_DEC, time, precision));
        replay.sections.add(new TextSection(id, TextEntityAttribute.COLOR, time, color));
        replay.sections.add(new StepSection(id, TextEntityAttribute.LAYER, time, layer));
    }

    private void writeUpdatedNumericTextLinearly(String id, float numericText) {
        replay.sections.add(new LinearSection(id, TextEntityAttribute.NUMBER_TEXT, time, numericText));
    }

    private void writeUpdatedNumericText(String id, float numericText) {
        replay.sections.add(new StepSection(id, TextEntityAttribute.NUMBER_TEXT, time, numericText));
    }

    public void unitCountChanged(Owner owner, Unit.Type type, int numberChange, float time) {
        if (owner == Owner.GREEN && type == Unit.Type.WORKER) {
            nGreenWorkers += numberChange;
            writeUpdatedNumericText(greenWorkerTextId, nGreenWorkers);
        }
        else if (owner == Owner.GREEN && type == Unit.Type.WARRIOR) {
            nGreenWarriors += numberChange;
            writeUpdatedNumericText(greenWarriorTextId, nGreenWarriors);
        }
        else if (owner == Owner.RED && type == Unit.Type.WORKER) {
            nRedWorkers += numberChange;
            writeUpdatedNumericText(redWorkerTextId, nRedWorkers);
        }
        else if (owner == Owner.RED && type == Unit.Type.WARRIOR) {
            nRedWarriors += numberChange;
            writeUpdatedNumericText(redWarriorTextId, nRedWarriors);
        }

        int nGreenUnits = nGreenWarriors + nGreenWorkers;
        int nRedUnits = nRedWarriors + nRedWorkers;
        int nAllUnits = nGreenUnits + nRedUnits;

        replay.sections.add(new StepSection(nGreenUnitsCurveId, EmptyAttribute.NONE, time, nGreenUnits));
        replay.sections.add(new StepSection(nRedUnitsCurveId, EmptyAttribute.NONE, time, nRedUnits));

        pbGreenWidth = bgWidth * (nGreenUnits / (float) nAllUnits);
        pbRedWidth = bgWidth * (nRedUnits / (float) nAllUnits);

        pbGreenX = bgX - bgWidth / 2f + pbGreenWidth / 2f;
        pbRedX = bgX + bgWidth / 2f - pbRedWidth / 2f;

        writeUpdatedPowerBar(pbGreenId, pbGreenX, pbGreenWidth);
        writeUpdatedPowerBar(pbRedId, pbRedX, pbRedWidth);
    }

    public void writeEndGameAnimation(int winningTeamIndex) {
        String winnerBgId = "HUD_winner_bg";
        float mapWidth = GameConfig.values.mapWidth;
        float mapHeight = GameConfig.values.mapHeight;

        writeBasicTexture(winnerBgId, mapWidth / 2f, mapHeight / 2f, mapWidth, mapHeight, Assets.hudBg, 4);
        replay.sections.add(new StepSection(winnerBgId, TextureEntityAttribute.OPACITY, time, 0.0f));
        replay.sections.add(new LinearSection(winnerBgId, TextureEntityAttribute.OPACITY, time + 0.5f, 0.9f));

        String winnerId = "HUD_TEXT_winner";
        String name = (winningTeamIndex == 0) ? nameGreenText : nameRedText;
        String color = (winningTeamIndex == 0) ? greenHex : redHex;

        writeBasicText(winnerId, mapWidth / 2f, mapHeight / 2f, 0.5f, name + " wins!", color, 5);
        replay.sections.add(new StepSection(winnerId, TextEntityAttribute.FONT_SIZE, time, fontSize * 3));
        replay.sections.add(new StepSection(winnerId, TextureEntityAttribute.OPACITY, time,0.0f));
        replay.sections.add(new StepSection(winnerId, TextureEntityAttribute.OPACITY, time + 0.2f, 1f));
    }

    public void draw(SpriteBatch batch) {
        // Draw hud background
        batch.draw(bgSprite, bgX - bgWidth / 2f, bgY - bgHeight / 2f, bgWidth, bgHeight);

        // Draw power bars
        batch.draw(pbGreenSprite, pbGreenX - pbGreenWidth / 2f, pbY - pbHeight / 2f, pbGreenWidth, pbHeight);
        batch.draw(pbRedSprite, pbRedX - pbRedWidth / 2f, pbY - pbHeight / 2f, pbRedWidth, pbHeight);

        // Draw green worker
        batch.draw(greenWorkerSprite, greenWorkerImageX - unitImageSize / 2f, hudItemsY - unitImageSize / 2f,
                unitImageSize, unitImageSize);

        // Draw green warrior
        batch.draw(greenWarriorSprite, greenWarriorImageX - unitImageSize / 2f, hudItemsY - unitImageSize / 2f,
                unitImageSize, unitImageSize);

        // Draw red worker
        batch.draw(redWorkerSprite, redWorkerImageX - unitImageSize / 2f, hudItemsY - unitImageSize / 2f,
                unitImageSize, unitImageSize);

        // Draw red warrior
        batch.draw(redWarriorSprite, redWarriorImageX - unitImageSize / 2f, hudItemsY - unitImageSize / 2f,
                unitImageSize, unitImageSize);
    }

    public void drawFont(SpriteBatch batch, Viewport gameHudViewport) {
        float ratio = gameHudViewport.getScreenHeight() / GameConfig.values.cameraViewHeight;
        float offsetY = font.getCapHeight() / 2f;

        // Draw names
        font.setColor(green);
        font.draw(batch, nameGreenText, nameGreenX * ratio, nameY * ratio + offsetY,
                0f, Align.left, false);
        font.setColor(red);
        font.draw(batch, nameRedText, nameRedX * ratio, nameY * ratio + offsetY,
                0f, Align.right, false);
        font.setColor(Color.WHITE);

        // Draw time
        font.draw(batch, String.format("%.2f", time), timeX * ratio, timeY * ratio + offsetY,
                0f, Align.center, false);

        // Draw green worker text
        font.draw(batch, "" + nGreenWorkers, greenWorkerTextX * ratio,
                hudItemsY * ratio + offsetY, 0f, Align.left, false);

        // Draw green warrior text
        font.draw(batch, "" + nGreenWarriors, greenWarriorTextX * ratio,
                hudItemsY * ratio + offsetY, 0f, Align.left, false);

        // Draw red worker text
        font.draw(batch, "" + nRedWorkers, redWorkerTextX * ratio,
                hudItemsY * ratio + offsetY, 0f, Align.left, false);

        // Draw red warrior text
        font.draw(batch, "" + nRedWarriors, redWarriorTextX * ratio,
                hudItemsY * ratio + offsetY, 0f, Align.left, false);
    }

    public void updateTime(float time) {
        this.time = time;
        writeUpdatedNumericTextLinearly(timeId, time);
    }
}
