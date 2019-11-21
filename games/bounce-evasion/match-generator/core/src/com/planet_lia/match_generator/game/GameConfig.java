package com.planet_lia.match_generator.game;

import com.google.gson.Gson;
import com.planet_lia.match_generator.libs.GeneralConfig;

import java.io.*;
import java.util.Random;

public class GameConfig {

    public Random random;

    public static GameConfig values;

    public GeneralConfig general;

    // Setup on load(...) call
    public String pathToAssets;
    public String pathToImages;
    public String pathToFonts;

    // Here you can add other configuration fields that will
    // load from a game-config.json
    public int gameUpdatesPerBotRequest;
    public float cameraViewWidth;
    public float cameraViewHeight;
    public float defaultFontSize;
    public int mapWidth;
    public int mapHeight;
    public int tileSize;
    public float coinSize;
    public float unitSize;
    public int unitLives;
    public int numberOfCoins;
    public int minSpawnCoinDistance;
    public float sawSize;
    public int sawSpawnUpdatesDelay;

    /** Initializes static fields in GameConfig class */
    public static void load(String configPath, int randomSeed) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(configPath));
        values = (new Gson()).fromJson(reader, GameConfig.class);

        if (randomSeed != -1) values.random = new Random(randomSeed);
        else values.random = new Random();

        values.pathToAssets = "assets/" + values.general.assetsVersion;
        values.pathToImages = values.pathToAssets + "/images/";
        values.pathToFonts = values.pathToAssets + "/fonts/";
    }

    /** Removes pathToImages from provided path */
    public static String shortenImagePath(String path) {
        return path.replaceFirst(values.pathToImages, "");
    }
}