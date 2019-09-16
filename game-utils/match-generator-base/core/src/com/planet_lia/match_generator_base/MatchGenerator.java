package com.planet_lia.match_generator_base;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.planet_lia.match_generator_base.libs.DefaultArgs;


public class MatchGenerator extends ApplicationAdapter {

    private DefaultArgs args;
    private GameConfig gameConfig;

    public MatchGenerator(DefaultArgs args, GameConfig gameConfig) {
        this.args = args;
        this.gameConfig = gameConfig;


    }

    @Override
    public void create() {}

    @Override
    public void render() {
        if (args.debug) {
            Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
    }

    @Override
    public void dispose() {}
}
