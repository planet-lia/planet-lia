package com.planet_lia.match_generator.logic;

import com.planet_lia.match_generator.libs.replays.*;

public class ReplayCreator {

    // Check the documentation for all the details about the replay files format:
    // - https://github.com/planet-lia/planet-lia/blob/master/game-utils/match-viewer/docs/writing_replay_files.md#replay-file-format
    public static Replay newReplay() {
        GameDetails gameDetails = new GameDetails(
                GameConfig.values.general.gameNamePretty,
                GameConfig.values.general.gameName,
                GameConfig.values.general.gameVersion,
                GameConfig.values.general.assetsVersion,
                "#000000",
                new CameraSettings(
                        GameConfig.values.cameraViewWidth,
                        GameConfig.values.cameraViewHeight
                ),
                new ShowEntityPathSettings(
                        "#FFFFFF",
                        0.6f,
                        "#FF00000",
                        GameConfig.values.unitSize * 0.2f,
                        0.2f
                )
        );

        Replay replay = new Replay(gameDetails);

        // Store the size of the map as a match detail
        replay.matchDetails.add(new MatchDetail(
                "Map size",
                GameConfig.values.mapWidth + " x " + GameConfig.values.mapHeight));

        return replay;
    }

}
