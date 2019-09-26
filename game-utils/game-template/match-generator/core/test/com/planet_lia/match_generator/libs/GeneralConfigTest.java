package com.planet_lia.match_generator.libs;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GeneralConfigTest {
    @Test
    void allowedNumbersOfBots() {
        GeneralConfig generalConfig = new GeneralConfig();
        generalConfig.allowedTeamFormats = new TeamFormat[]{
                new TeamFormat("1:1", 0),
                new TeamFormat("1", 0),
                new TeamFormat("5:123", 0),
                new TeamFormat("1:2", 0),
                new TeamFormat("3", 0),
                new TeamFormat("4", 0),
                new TeamFormat("1:1:3", 0),
                new TeamFormat("3:7", 0),
                new TeamFormat("12:11:1", 0)
        };

        ArrayList<Integer> allowedNumbersOfBots = new ArrayList<>(Arrays.asList(1,2,3,4,5,10,24,128));

        for (int i = 0; i < 200; i++) {
            assertEquals(generalConfig.isNumberOfBotsAllowed(i), allowedNumbersOfBots.contains(i));
        }
    }
}
