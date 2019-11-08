package com.planet_lia.match_generator.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.planet_lia.match_generator.libs.replays.Replay;
import com.planet_lia.match_generator.libs.replays.StepSection;
import com.planet_lia.match_generator.libs.replays.TextSection;
import com.planet_lia.match_generator.libs.replays.TextureEntityAttribute;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Background {

    private Sprite sprite;

    public Background(Replay replay) {
        Texture texture = Assets.get(Assets.tile, Texture.class);
        if (texture != null) {
            sprite = new Sprite(texture);
        }

        writeToReplay(replay);
    }

    private void writeToReplay(Replay replay) {
        float tileSize = GameConfig.values.backgroundTileSize;

        for (int x = 0; x < GameConfig.values.mapWidth; x += tileSize) {
            for (int y = 0; y < GameConfig.values.mapHeight; y += tileSize) {
                String eid = "bg_" + x + "_" + y;
                replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, 0f, x + tileSize / 2f));
                replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, 0f, y + tileSize / 2f));
                replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, 0f, tileSize));
                replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, 0f, tileSize));
                replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, 0f,
                        shortenImagePath(Assets.tile)));
                replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, 0f, 0f));
            }
        }
    }


    public void draw(SpriteBatch batch) {
        float tileSize = GameConfig.values.backgroundTileSize;

        for (int x = 0; x < GameConfig.values.mapWidth; x += tileSize) {
            for (int y = 0; y < GameConfig.values.mapHeight; y += tileSize) {
                batch.draw(sprite, x, y, tileSize, tileSize);
            }
        }
    }
}
