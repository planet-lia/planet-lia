package com.planet_lia.match_generator.game;

import com.google.gson.Gson;
import com.planet_lia.match_generator.libs.GeneralConfig;

import java.io.*;
import java.util.ArrayList;

public class GameConfig {

    public static GameConfig values;

    public GeneralConfig general;

    // Setup on load(...) call
    public String pathToAssets;
    public String pathToImages;

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
    public int resourcesForNewUnit;
    public int resourcesGenerationSpeed;
    public int maxActiveWorkersPerPlanet;
    public float unitIndicatorSize;
    public float unitIndicatorOffset;
    public float maxNumberOfUnitsPerTeam;

    /** Initializes static fields in GameConfig class */
    public static void load(String configPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(configPath));
        values = (new Gson()).fromJson(reader, GameConfig.class);

        values.pathToAssets = "assets/" + values.general.assetsVersion;
        values.pathToImages = values.pathToAssets + "/images/";
    }

    /** Removes pathToImages from provided path */
    public static String shortenImagePath(String path) {
        return path.replaceFirst(values.pathToImages, "");
    }
}