package com.planet_lia.match_generator.libs;

public class BotDetails {
    public String botName;
    transient String token;
    public int teamIndex = -1;
    public BotDetailsAdditional additional;

    public BotDetails(String botName, String token, BotDetailsAdditional additional) {
        this.botName = botName;
        this.token = token;
        this.additional = additional;
    }
}

