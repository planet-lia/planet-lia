package com.planet_lia.match_generator.logic;

import com.google.gson.Gson;
import com.planet_lia.match_generator.libs.GeneralConfig;

import java.io.*;

public class GameConfig {

    public static final String ASSETS_VERSION = "1.0";
    public static final String PATH_TO_ASSETS = "assets/" + ASSETS_VERSION;
    public static final String PATH_TO_GAME_CONFIG = PATH_TO_ASSETS + "/game-config.json";

    public GeneralConfig generalConfig;

    // Here you can add other configuration fields that will
    // load from a game-config.json
    public int cameraViewWidth;
    public int cameraViewHeight;
    public int defaultFontSize;

    public static GameConfig load() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_GAME_CONFIG));
        return (new Gson()).fromJson(reader, GameConfig.class);
    }
}