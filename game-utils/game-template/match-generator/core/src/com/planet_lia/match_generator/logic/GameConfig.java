package com.planet_lia.match_generator.logic;

import com.google.gson.Gson;
import com.planet_lia.match_generator.libs.GeneralConfig;

import java.io.*;

public class GameConfig {

    public static GameConfig values;

    public static final String ASSETS_VERSION = "1.0";
    public static final String PATH_TO_ASSETS = "assets/" + ASSETS_VERSION;
    public static final String PATH_TO_GAME_CONFIG = PATH_TO_ASSETS + "/game-config.json";

    public GeneralConfig general;

    // Here you can add other configuration fields that will
    // load from a game-config.json
    public int gameUpdatesPerBotRequest;
    public float cameraViewWidth;
    public float cameraViewHeight;
    public float defaultFontSize;
    public int mapWidth;
    public int mapHeight;
    public int backgroundTileSize;
    public float coinSize;
    public float coinPositionChangePeriod;
    public int unitSize;
    public float unitRotationSpeed;
    public float unitSpeed;

    public static void load() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_GAME_CONFIG));
        values = (new Gson()).fromJson(reader, GameConfig.class);
    }
}