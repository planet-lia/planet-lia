package com.planet_lia.match_generator_base.desktop;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.beust.jcommander.JCommander;
import com.planet_lia.match_generator_base.MatchGenerator;
import com.planet_lia.match_generator_base.logic.Args;
import com.planet_lia.match_generator_base.logic.GameConfig;

public class DesktopLauncher {
    public static void main (String[] arg) {
        // TODO replace with real arguments
        String[] args = new String[]{"-d", "b1", "t1", "b2", "t2"};

        // Parse arguments
        Args parsedArgs = new Args();
        JCommander.newBuilder()
                .addObject(parsedArgs)
                .build()
                .parse(args);

        GameConfig gameConfig = new GameConfig();
        MatchGenerator game =  new MatchGenerator(parsedArgs, gameConfig);

        if (parsedArgs.debug) {
            // Run debug view
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setWindowedMode(gameConfig.debugViewWidth, gameConfig.debugViewHeight);
            config.useVsync(false);
            config.setTitle(gameConfig.gameName);
            config.setResizable(false);
            new Lwjgl3Application(game, config);
        } else {
            // Run headless application
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            config.renderInterval = 0f;
            new HeadlessApplication(game, config);
        }
    }
}

