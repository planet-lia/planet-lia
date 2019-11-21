package com.planet_lia.match_generator.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.libs.replays.Replay;
import com.planet_lia.match_generator.libs.replays.StepSection;
import com.planet_lia.match_generator.libs.replays.TextSection;
import com.planet_lia.match_generator.libs.replays.TextureEntityAttribute;

import static com.planet_lia.match_generator.game.GameConfig.shortenImagePath;

public class Tile {

    private Sprite sprite;
    public int x;
    public int y;

    // Used for flood fill algorithm
    public boolean marked;

    public Tile(Replay replay, int x, int y) {
        Texture texture = Assets.get(Assets.tile, Texture.class);
        if (texture != null) {
            sprite = new Sprite(texture);
        }
        this.x = x;
        this.y = y;
    }

    public void writeToReplay(Replay replay) {
        float tileSize = GameConfig.values.tileSize;

        String eid = "bg_" + x + "_" + y;
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, 0f, x + tileSize / 2f));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, 0f, y + tileSize / 2f));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, 0f, tileSize));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, 0f, tileSize));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, 0f,
                shortenImagePath(Assets.tile)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, 0f, 0f));
    }


    public void draw(SpriteBatch batch) {
        float tileSize = GameConfig.values.tileSize;
        batch.draw(sprite, x, y, tileSize, tileSize);
    }
}
