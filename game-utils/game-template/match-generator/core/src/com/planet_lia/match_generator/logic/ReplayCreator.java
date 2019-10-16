package com.planet_lia.match_generator.logic;

import com.google.gson.Gson;
import com.planet_lia.match_generator.libs.replays.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReplayCreator {

    // Check the documentation for all the details about the replay files format:
    // - https://github.com/planet-lia/planet-lia/blob/master/game-utils/match-viewer/docs/writing_replay_files.md#replay-file-format
    public static Replay newReplay() {
        GameDetails gameDetails = new GameDetails(
                "game-template",
                GameConfig.ASSETS_VERSION,
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

    public static void saveReplayFile(Replay replay, String filePath) {
        String replayJson = new Gson().toJson(replay);
        try {
            Files.write(Paths.get(filePath), replayJson.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to save replay file in a file");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
