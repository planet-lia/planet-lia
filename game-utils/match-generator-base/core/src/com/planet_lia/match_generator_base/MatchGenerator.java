package com.planet_lia.match_generator_base;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.planet_lia.match_generator_base.logic.Args;
import com.planet_lia.match_generator_base.logic.GameConfig;

public class MatchGenerator extends ApplicationAdapter {

    private Args args;
    private GameConfig gameConfig;

    public MatchGenerator(Args args, GameConfig gameConfig) {
        this.args = args;
        this.gameConfig = gameConfig;
    }

    @Override
    public void create() {}

    @Override
    public void render() {
        if (this.args.debug) {
            Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
    }

    @Override
    public void dispose() {}
}
