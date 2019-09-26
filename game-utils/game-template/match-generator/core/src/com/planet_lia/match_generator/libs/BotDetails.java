package com.planet_lia.match_generator.libs;

/**
 * Details about the bot that match-generator can access and use in game
 */
public class BotDetails {
    String botName;
    int teamIndex;
    int rank;

    public BotDetails(String botName, int teamIndex, int rank) {
        this.botName = botName;
        this.teamIndex = teamIndex;
        this.rank = rank;
    }
}
