package com.planet_lia.match_generator.desktop;

import com.badlogic.gdx.Graphics;
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

public class DesktopLauncher {

    public static void main (String[] arg) throws Exception {
        // TODO replace with real arguments
        String[] args = new String[]{"-d", "b1", "t1", "{}", "b2", "t2", "{}", "b3", "t3", "{}", "b4", "t4", "{}"};

        // Parse arguments
        Args parsedArgs = new Args();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(parsedArgs)
                .build();
        jCommander.parse(args);

        // If --help flag is provided, display help
        if (parsedArgs.help) {
            jCommander.setProgramName("match-generator.jar");
            jCommander.usage();
            return;
        }

        // Load configs
        GameConfig gameConfig = GameConfig.load();
        GeneralConfig generalConfig = gameConfig.generalConfig;

        // Increase bot restrictions if it is debug mode
        if (parsedArgs.debug) {
            int largeNumber = 100000000;
            generalConfig.connectingBotsTimeout = largeNumber;
            generalConfig.botFirstResponseTimeout = largeNumber;
            generalConfig.botResponseTimeout = largeNumber;
            generalConfig.maxTimeoutsPerBot = largeNumber;
            generalConfig.botResponseTotalDurationMax = largeNumber;
        }

        // Setup match generator
        BotDetails[] botsDetails = parsedArgs.getBotsDetails(gameConfig.generalConfig);
        MatchGenerator game =  new MatchGenerator(parsedArgs, gameConfig, botsDetails);

        if (parsedArgs.debug) {
            // Run with debug view

            // Get monitor width and height
            Graphics.Monitor primary = Lwjgl3ApplicationConfiguration.getPrimaryMonitor();
            Graphics.DisplayMode mode = Lwjgl3ApplicationConfiguration.getDisplayMode(primary);
            int monitorHeight = mode.height;

            // Configure debug window size
            float windowToMonitorRatio = parsedArgs.debugWindowToScreen;
            int windowHeight = (int) (monitorHeight * windowToMonitorRatio);

            // Run with debug window
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setWindowedMode(generalConfig.debugWindow.getWindowWidth(windowHeight), windowHeight);
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

