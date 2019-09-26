package com.planet_lia.match_generator.libs;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DefaultArgsTest {
    @Test
    void botsAndTeams() {
        String[] args = new String[]{
                "--teams", "1:3:1",
                "b1", "t1", "{}",
                "b2", "t2", "{}",
                "b3", "t3", "{\"rank\":2}",
                "b4", "t4", "{\"unusedField\":\"ignored\"}",
                "b5", "t5", "{}"
        };

        // Parse arguments
        DefaultArgs parsedArgs = new DefaultArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(parsedArgs)
                .build();
        jCommander.parse(args);

        // Test botsDetails
        BotDetailsAdvanced[] botsDetails = parsedArgs.getBotsDetails();
        assertBotDetails(botsDetails[0], "b1", "t1", -1);
        assertBotDetails(botsDetails[1], "b2", "t2", -1);
        assertBotDetails(botsDetails[2], "b3", "t3", 2);
        assertBotDetails(botsDetails[3], "b4", "t4", -1);
        assertBotDetails(botsDetails[4], "b5", "t5", -1);

        // Test team sizes
        int[] teamSizes = parsedArgs.getTeamSizes();
        assertEquals(teamSizes[0], 1);
        assertEquals(teamSizes[1], 3);
        assertEquals(teamSizes[2], 1);

        // Test assigning bots to teams
        DefaultArgs.setTeams(teamSizes, botsDetails);
        assertEquals(0, botsDetails[0].teamIndex);
        assertEquals(1, botsDetails[1].teamIndex);
        assertEquals(1, botsDetails[2].teamIndex);
        assertEquals(1, botsDetails[3].teamIndex);
        assertEquals(2, botsDetails[4].teamIndex);
    }

    @Test
    void brokenTeamsParameter() {
        String[][] cases = new String[][]{
                new String[]{
                        "--teams", "1:3",
                        "b1", "t1", "{}"
                },
                new String[]{
                        "b1", "t1", "{}"
                },
        };

        for (String[] args : cases) {
            // Parse arguments
            DefaultArgs parsedArgs = new DefaultArgs();
            JCommander jCommander = JCommander.newBuilder()
                    .addObject(parsedArgs)
                    .build();
            jCommander.parse(args);

            BotDetailsAdvanced[] botsDetails = parsedArgs.getBotsDetails();
            int[] teamSizes = parsedArgs.getTeamSizes();

            // Test assigning bots to teams
            assertThrows(Error.class, () -> DefaultArgs.setTeams(teamSizes, botsDetails));
        }
    }

    void assertBotDetails(BotDetailsAdvanced details, String botName, String token, int rank) {
        assertEquals(details.botName, botName);
        assertEquals(details.token, token);
        assertEquals(details.optional.rank, rank);
    }

}
