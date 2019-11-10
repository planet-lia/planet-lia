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

public class Background {

    private Sprite sprite;
    private String eid = "background";
    float x, y, width, height;

    public Background(Replay replay) {
        Texture texture = Assets.get(Assets.background, Texture.class);
        if (texture != null) {
            sprite = new Sprite(texture);
        }

        this.width = GameConfig.values.mapWidth;
        this.height = GameConfig.values.mapHeight;
        this.x = width / 2f;
        this.y = height / 2f;

        writeToReplay(replay);
    }

    private void writeToReplay(Replay replay) {
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.X, 0f, x));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.Y, 0f, y));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.WIDTH, 0f, width));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.HEIGHT, 0f, height));
        replay.sections.add(new TextSection(eid, TextureEntityAttribute.TEXTURE, 0f,
                shortenImagePath(Assets.background)));
        replay.sections.add(new StepSection(eid, TextureEntityAttribute.LAYER, 0f, 0));
    }


    public void draw(SpriteBatch batch) {
        batch.draw(sprite, 0, 0, width, height);
    }
}
