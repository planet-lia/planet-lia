package com.planet_lia.match_generator.desktop;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.beust.jcommander.JCommander;
import com.planet_lia.match_generator.MatchGenerator;
import com.planet_lia.match_generator.libs.BotDetails;
import com.planet_lia.match_generator.libs.GeneralConfig;
import com.planet_lia.match_generator.logic.Args;
import com.planet_lia.match_generator.logic.GameConfig;

import java.io.IOException;

public class DesktopLauncher {

    public static void main (String[] arg) throws Exception {
        // TODO replace with real arguments
        String[] args = new String[]{"-d", "b1", "t1", "{}", "b2", "t2", "{}"};

        // Parse arguments
        Args parsedArgs = new Args();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(parsedArgs)
                .build();
        jCommander.parse(args);

        if (parsedArgs.help) {
            jCommander.setProgramName("match-generator.jar");
            jCommander.usage();
            return;
        }

        GameConfig gameConfig = GameConfig.load();
        BotDetails[] botsDetails = parsedArgs.getBotsDetails(gameConfig.generalConfig);

        MatchGenerator game =  new MatchGenerator(parsedArgs, gameConfig, botsDetails);

        GeneralConfig generalConfig = gameConfig.generalConfig;

        if (parsedArgs.debug) {
            // Run debug view
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setWindowedMode(generalConfig.debugViewWidth, generalConfig.debugViewHeight);
            config.useVsync(false);
            config.setTitle(generalConfig.gameNamePretty);
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

