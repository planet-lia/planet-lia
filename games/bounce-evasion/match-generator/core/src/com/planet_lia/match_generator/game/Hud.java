package com.planet_lia.match_generator.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planet_lia.match_generator.libs.BotDetails;
import com.planet_lia.match_generator.libs.replays.*;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Hud {
    Replay replay;

    // Hud background
    Sprite bgSprite;
    float bgWidth = GameConfig.values.cameraViewWidth * 0.5f;
    float bgHeight = GameConfig.values.cameraViewHeight * 0.06f;
    float bgX = GameConfig.values.cameraViewWidth * 0.5f;
    float bgY = GameConfig.values.cameraViewHeight - bgHeight * 0.5f;

    String yellowHex = GameConfig.values.general.botColors[0];
    String greenHex =  GameConfig.values.general.botColors[1];
    Color yellow = Color.valueOf(yellowHex);
    Color green =  Color.valueOf(greenHex);

    // Names
    float nameY = bgY;
    float nameOffsetX = GameConfig.values.cameraViewWidth * 0.01f;
    float nYellowPointsX = bgX - bgWidth / 2f + nameOffsetX;
    float nameGreenX = bgX + bgWidth / 2f - nameOffsetX;
    float fontSize = 24;

    String nameYellowText;
    String nameGreenText;
    BitmapFont font;

    // Yellow lives
    float hudItemsY = bgY;
    float offsetSmall = GameConfig.values.cameraViewWidth * 0.02f;
    float offsetBig = GameConfig.values.cameraViewWidth * 0.1f;
    float yellowLivesImageX = nYellowPointsX + offsetBig;
    float iconImageSize = bgHeight * 0.6f;
    Sprite lifeSprite;

    // Yellow lives text
    float yellowLivesTextX = yellowLivesImageX + offsetSmall;
    int nYellowLives = GameConfig.values.unitLives;
    public String yellowLivesTextId = "HUD_TEXT_yellow_lives";

    //Yellow points
    float yellowPointsImageX = yellowLivesTextX + offsetSmall * 2;
    Sprite pointsSprite;

    // Yellow points text
    float yellowPointsTextX = yellowPointsImageX + offsetSmall;
    int nYellowPoints = 0;
    public String yellowPointsTextId = "HUD_TEXT_yellow_point";

    // Green points
    float greenPointsImageX = nameGreenX - offsetBig - offsetSmall;

    // Green points text
    float greenPointsTextX = greenPointsImageX + offsetSmall;
    int nGreenPoints = 0;
    public String greenPointsTextId = "HUD_TEXT_green_point";

    // Green lives
    float greenLivesImageX = greenPointsImageX - offsetSmall * 3;

    // Green lives text
    float greenLivesTextX = greenLivesImageX + offsetSmall;
    int nGreenLives = GameConfig.values.unitLives;
    public String greenLivesTextId = "HUD_TEXT_green_lives";

    // Time
    float timeX = bgX;
    float timeY = bgY;
    float time = 0f;
    String timeId = "HUD_TEXT_time";

    public Hud(Replay replay, BotDetails[] botDetails) {
        this.replay = replay;
        this.nameYellowText = botDetails[0].botName;
        this.nameGreenText = botDetails[1].botName;

        bgSprite = Assets.setTextureToSprite(bgSprite, Assets.hudBg);
        lifeSprite = Assets.setTextureToSprite(lifeSprite, Assets.life);
        pointsSprite = Assets.setTextureToSprite(pointsSprite, Assets.coin);

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

        // Items images
        writeBasicTexture("HUD_yellow_lives", yellowLivesImageX, hudItemsY, iconImageSize, iconImageSize,
                Assets.life, 2);
        writeBasicTexture("HUD_yellow_point", yellowPointsImageX, hudItemsY, iconImageSize, iconImageSize,
                Assets.coin, 2);
        writeBasicTexture("HUD_green_lives", greenLivesImageX, hudItemsY, iconImageSize, iconImageSize,
                Assets.life, 2);
        writeBasicTexture("HUD_green_point", greenPointsImageX, hudItemsY, iconImageSize, iconImageSize,
                Assets.coin, 2);

        // Time
        writeBasicNumericText(timeId, timeX, timeY, 0.5f,  time, 2, "#FFFFFF", 2);

        // Unit texts
        writeBasicNumericText(yellowLivesTextId, yellowLivesTextX, hudItemsY, 0f, nYellowLives, 0,
                yellowHex, 2);
        writeBasicNumericText(yellowPointsTextId, yellowPointsTextX, hudItemsY, 0f, nYellowPoints, 0,
                yellowHex, 2);
        writeBasicNumericText(greenLivesTextId, greenLivesTextX, hudItemsY, 0f, nGreenLives, 0,
                greenHex, 2);
        writeBasicNumericText(greenPointsTextId, greenPointsTextX, hudItemsY, 0f, nGreenPoints, 0,
                greenHex, 2);

        // Names
        writeBasicText("HUD_TEXT_yellow_name", nYellowPointsX, nameY, 0f, nameYellowText, yellowHex, 2);
        writeBasicText("HUD_TEXT_green_name", nameGreenX, nameY, 1f, nameGreenText, greenHex, 2);
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

    public void decreaseYellowLives() {
        nYellowLives--;
        writeUpdatedNumericText(yellowLivesTextId, nYellowLives);
    }

    public void decreaseGreenLives() {
        nGreenLives--;
        writeUpdatedNumericText(greenLivesTextId, nGreenLives);
    }

    public void increaseYellowPoints() {
        nYellowPoints++;
        writeUpdatedNumericText(yellowPointsTextId, nYellowPoints);
    }

    public void increaseGreenPoints() {
        nGreenPoints++;
        writeUpdatedNumericText(greenPointsTextId, nGreenPoints);
    }


    public void draw(SpriteBatch batch) {
        float wOffset = (GameConfig.values.cameraViewWidth - GameConfig.values.mapWidth) / 2f;
        float hOffset = (GameConfig.values.cameraViewHeight - GameConfig.values.mapHeight) / 2f;

        // Draw hud background
        batch.draw(bgSprite, bgX - bgWidth / 2f - wOffset, bgY - bgHeight / 2f - hOffset, bgWidth, bgHeight);

        // Draw yellow lives
        batch.draw(lifeSprite,
                yellowLivesImageX - iconImageSize / 2f - wOffset,
                hudItemsY - iconImageSize / 2f - hOffset,
                iconImageSize, iconImageSize);

        // Draw yellow point
        batch.draw(pointsSprite,
                yellowPointsImageX - iconImageSize / 2f - wOffset,
                hudItemsY - iconImageSize / 2f - hOffset,
                iconImageSize, iconImageSize);

        // Draw green lives
        batch.draw(lifeSprite,
                greenLivesImageX - iconImageSize / 2f - wOffset,
                hudItemsY - iconImageSize / 2f - hOffset,
                iconImageSize, iconImageSize);

        // Draw green point
        batch.draw(pointsSprite,
                greenPointsImageX - iconImageSize / 2f - wOffset,
                hudItemsY - iconImageSize / 2f - hOffset,
                iconImageSize, iconImageSize);
    }

    public void writeEndGameAnimation(int winningTeamIndex) {
        String winnerBgId = "HUD_winner_bg";
        float camWidth = GameConfig.values.cameraViewWidth;
        float camHeight = GameConfig.values.cameraViewHeight;

        writeBasicTexture(winnerBgId, camWidth / 2f, camHeight / 2f, camWidth, camHeight, Assets.endGameOverlay, 0f);
        replay.sections.add(new StepSection(winnerBgId, TextureEntityAttribute.OPACITY, time, 0.0f));
        replay.sections.add(new LinearSection(winnerBgId, TextureEntityAttribute.OPACITY, time + 0.5f, 0.9f));

        String winnerId = "HUD_TEXT_winner";
        String name = (winningTeamIndex == 0) ? nameYellowText : nameGreenText;
        String color = (winningTeamIndex == 0) ? yellowHex : greenHex;

        writeBasicText(winnerId, camWidth / 2f, camHeight / 2f, 0.5f, name + " wins!", color, 5);
        replay.sections.add(new StepSection(winnerId, TextEntityAttribute.FONT_SIZE, time, fontSize * 3));
        replay.sections.add(new StepSection(winnerId, TextureEntityAttribute.OPACITY, time,0.0f));
        replay.sections.add(new StepSection(winnerId, TextureEntityAttribute.OPACITY, time + 0.2f, 1f));
    }

    public void drawFont(SpriteBatch batch, Viewport gameHudViewport) {
        float ratio = gameHudViewport.getScreenHeight() / GameConfig.values.cameraViewHeight;
        float offsetY = font.getCapHeight() / 2f;

        // Draw names
        font.setColor(yellow);
        font.draw(batch, nameYellowText, nYellowPointsX * ratio, nameY * ratio + offsetY,
                0f, Align.left, false);
        font.setColor(green);
        font.draw(batch, nameGreenText, nameGreenX * ratio, nameY * ratio + offsetY,
                0f, Align.right, false);
        font.setColor(Color.WHITE);

        // Draw time
        font.draw(batch, String.format("%.2f", time), timeX * ratio, timeY * ratio + offsetY,
                0f, Align.center, false);

        // Draw yellow lives text
        font.draw(batch, "" + nYellowLives, yellowLivesTextX * ratio,
                hudItemsY * ratio + offsetY, 0f, Align.left, false);

        // Draw yellow point text
        font.draw(batch, "" + nYellowPoints, yellowPointsTextX * ratio,
                hudItemsY * ratio + offsetY, 0f, Align.left, false);

        // Draw green lives text
        font.draw(batch, "" + nGreenLives, greenLivesTextX * ratio,
                hudItemsY * ratio + offsetY, 0f, Align.left, false);

        // Draw green point text
        font.draw(batch, "" + nGreenPoints, greenPointsTextX * ratio,
                hudItemsY * ratio + offsetY, 0f, Align.left, false);
    }

    public void updateTime(float time) {
        this.time = time;
        writeUpdatedNumericTextLinearly(timeId, time);
    }
}
