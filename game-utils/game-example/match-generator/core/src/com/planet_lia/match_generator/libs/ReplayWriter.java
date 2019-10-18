package com.planet_lia.match_generator.libs;

import com.google.gson.Gson;
import com.planet_lia.match_generator.libs.replays.BotReplayDetails;
import com.planet_lia.match_generator.libs.replays.Replay;
import com.planet_lia.match_generator.logic.GameConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.planet_lia.match_generator.libs.math.MathHelpers.round;

public class ReplayWriter {
    public static void saveReplayFile(Replay replay, int[] teamsFinalOrder, BotServer server, String filePath) {
        // Save winning team index
        replay.teamsFinalOrder = teamsFinalOrder;

        // Add BotReplayDetails to replay file
        ArrayList<BotConnection> botConnections = server.getBotConnections();
        for (int botIndex = 0; botIndex < botConnections.size(); botIndex++) {
            BotConnection connection = botConnections.get(botIndex);
            replay.botDetails.add(new BotReplayDetails(
                    connection.details.botName,
                    connection.details.teamIndex,
                    connection.details.additional.rank,
                    GameConfig.values.general.botColors[botIndex],
                    round(connection.responseTotalDuration / 1000f, 3), // Seconds
                    connection.numberOfTimeouts,
                    connection.disqualified,
                    round(connection.disqualificationTime, 3), // Seconds
                    connection.disqualificationReason
            ));
        }

        // Save replay file
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