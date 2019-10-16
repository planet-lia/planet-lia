package com.planet_lia.match_generator.libs;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DefaultArgsTest {
    @Test
    void botsTeamsAndDetails() {
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

        GeneralConfig config = new GeneralConfig();
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:3:1", 1)};

        BotDetails[] botsDetails = parsedArgs.getBotsDetails(config);

        assertBotDetailsAdvanced(botsDetails[0], "b1", "t1", 0, -1);
        assertBotDetailsAdvanced(botsDetails[1], "b2", "t2", 1,-1);
        assertBotDetailsAdvanced(botsDetails[2], "b3", "t3", 1, 2);
        assertBotDetailsAdvanced(botsDetails[3], "b4", "t4", 1,-1);
        assertBotDetailsAdvanced(botsDetails[4], "b5", "t5", 2,-1);
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


            GeneralConfig config = new GeneralConfig();
            config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:3", 1)};

            assertThrows(Error.class, () -> parsedArgs.getBotsDetails(config));
        }
    }

    void assertBotDetailsAdvanced(BotDetails details, String botName, String token, int teamIndex, int rank) {
        assertEquals(botName, details.botName);
        assertEquals(token, details.token);
        assertEquals(teamIndex, details.teamIndex);
        assertEquals(rank, details.additional.rank);
    }
}
