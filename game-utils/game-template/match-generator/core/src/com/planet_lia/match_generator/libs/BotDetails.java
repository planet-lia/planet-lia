package com.planet_lia.match_generator.libs;

public class BotDetails {
    String botName;
    String token;
    int teamIndex = -1;
    BotDetailsAdditional additional;

    public BotDetails(String botName, String token, BotDetailsAdditional additional) {
        this.botName = botName;
        this.token = token;
        this.additional = additional;
    }
}

