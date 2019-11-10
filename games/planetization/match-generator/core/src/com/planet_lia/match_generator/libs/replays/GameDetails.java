package com.planet_lia.match_generator.libs.replays;

public class GameDetails {
    public String gameNamePretty;
    public String gameName;
    public String gameVersion;
    public String assetsVersion;
    public String backgroundColor;
    public CameraSettings camera;
    public ShowEntityPathSettings showEntityPath;

    public GameDetails(String gameNamePretty,
                       String gameName,
                       String gameVersion,
                       String assetsVersion,
                       String backgroundColor,
                       CameraSettings camera,
                       ShowEntityPathSettings showEntityPath) {
        this.gameNamePretty = gameNamePretty;
        this.gameName = gameName;
        this.gameVersion = gameVersion;
        this.assetsVersion = assetsVersion;
        this.backgroundColor = backgroundColor;
        this.camera = camera;
        this.showEntityPath = showEntityPath;
    }
}
