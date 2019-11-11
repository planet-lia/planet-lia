package com.planet_lia.match_generator.game;

import com.google.gson.Gson;
import com.planet_lia.match_generator.libs.GeneralConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameConfig {


    public static final Random random = new Random(3);

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
    public int planetSize;
    public int planetGapWidth;
    public int planetGapHeight;
    public ArrayList<Integer> planetPositionsOnGrid;
    public int greenPlanetOnStart;
    public int redPlanetOnStart;
    public float unitSize;
    public float unitSpeed;
    public float unitRotationSpeed;
    public int numberOfWorkersOnStart;
    public int unitCost;
    public int resourceGenerationSpeed;
    public int maxActiveWorkersPerPlanet;
    public float unitIndicatorSize;
    public float unitIndicatorOffset;
    public int maxNumberOfUnitsPerTeam;
    public float maxMatchDuration;
    public float workerHealth;
    public float workerAttack;
    public float warriorHealth;
    public float warriorAttack;
    public float damageReductionRatioOnDefence;

    /** Initializes static fields in GameConfig class */
    public static void load(String configPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(configPath));
        values = (new Gson()).fromJson(reader, GameConfig.class);

        values.pathToAssets = "assets/" + values.general.assetsVersion;
        values.pathToImages = values.pathToAssets + "/images/";
        values.pathToFonts = values.pathToAssets + "/fonts/";
    }

    /** Removes pathToImages from provided path */
    public static String shortenImagePath(String path) {
        return path.replaceFirst(values.pathToImages, "");
    }
}