package com.planet_lia.match_generator.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.planet_lia.match_generator.game.GameLogic;
import com.planet_lia.match_generator.libs.MatchGenerator;
import com.planet_lia.match_generator.libs.BotDetails;
import com.planet_lia.match_generator.libs.GeneralConfig;
import com.planet_lia.match_generator.game.Args;
import com.planet_lia.match_generator.game.GameConfig;

public class DesktopLauncher {

    public static void main (String[] arg) throws Exception {
        // Parse arguments
        Args.parseArgs(arg);

        // Load configs
        GameConfig.load(Args.values.config);
        GeneralConfig generalConfig = GameConfig.values.general;

        // Increase bot restrictions if it is debug mode
        if (Args.values.debug) {
            int largeNumber = 100000000;
            generalConfig.connectingBotsTimeout = largeNumber;
            generalConfig.botFirstResponseTimeout = largeNumber;
            generalConfig.botResponseTimeout = largeNumber;
            generalConfig.maxTimeoutsPerBot = largeNumber;
            generalConfig.botResponseTotalDurationMax = largeNumber;
        }

        // Setup match generator
        BotDetails[] botsDetails = Args.values.getBotsDetails(GameConfig.values.general);
        MatchGenerator game =  new MatchGenerator(Args.values, botsDetails, new GameLogic());

        if (Args.values.debug) {
            // Run with debug view

            // Get monitor width and height
            Graphics.Monitor primary = Lwjgl3ApplicationConfiguration.getPrimaryMonitor();
            Graphics.DisplayMode mode = Lwjgl3ApplicationConfiguration.getDisplayMode(primary);
            int monitorHeight = mode.height;

            // Configure debug window size
            float windowToMonitorRatio = Args.values.debugWindowToScreen;
            int windowHeight = (int) (monitorHeight * windowToMonitorRatio);

            // Run with debug window
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setWindowedMode(generalConfig.debugWindow.getWindowWidth(windowHeight), windowHeight);
            config.useVsync(false);
            config.setTitle(generalConfig.gameNamePretty);
            config.setResizable(false);
            try {
                new Lwjgl3Application(game, config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Run headless application
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            config.renderInterval = 0f;
            try {
                new HeadlessApplication(game, config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

