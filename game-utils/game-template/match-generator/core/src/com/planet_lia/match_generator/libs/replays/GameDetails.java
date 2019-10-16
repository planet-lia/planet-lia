package com.planet_lia.match_generator.libs.replays;

public class GameDetails {
    public String game;
    public String version;
    public String backgroundColor;
    public CameraSettings camera;
    public ShowEntityPathSettings showEntityPath;

    public GameDetails(String game,
                       String version,
                       String backgroundColor,
                       CameraSettings camera,
                       ShowEntityPathSettings showEntityPath) {
        this.game = game;
        this.version = version;
        this.backgroundColor = backgroundColor;
        this.camera = camera;
        this.showEntityPath = showEntityPath;
    }
}
