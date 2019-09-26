package com.planet_lia.match_generator.libs;

public class GeneralConfig {
    /**
     * A name of the game that will display on top of debug viewer.
     * Eg. Game Template
     */
    public String gameNamePretty;

    /**
     * A name of the game that corresponds to the name of the folder
     * eg. game-template
     */
    public String gameName;

    /**
     * Width of the debug view that is displayed while the match is generating
     */
    public int debugViewWidth;

    /**
     * Height  of the debug view that is displayed while the match is generating
     */
    public int debugViewHeight;

    /**
     * How many game logic updates happen per second
     */
    public int ticksPerSecond;

    /**
     * How long do bots have to connect (in seconds)
     * before being disqualified. Measured form the
     * time the bot server has started.
     */
    public float connectingBotsTimeout;

    /**
     * Time (in seconds) that a bot has to respond
     * after the first update
     */
    public float botFirstResponseTimeout;

    /**
     * Time (in seconds) that a bot has to respond
     * after each update except for the first one
     */
    public float botResponseTimeout;

    /**
     * How many times can a bot not respond in time
     * until it is disqualified.
     */
    public int maxTimeoutsPerBot;

    /**
     * Limit to how much time does the bot have to respond
     * to all requests in a match.
     */
    public float botResponseTotalDurationMax;

    /**
     * How many game logic updates happen before one
     * update to all bots is sent.
     *
     * If ticksPerSecond is set to 30 and gameUpdatesPerBotsUpdate
     * is set to 3, bots will receive game updates 10 times per second.
     */
    public int gameUpdatesPerBotsUpdate;

    /**
     * Array of allowed numbers of bots eg. [2,4,8] means that
     * 2, 4 or 8 bots must be provided to the match-generator
     * and that this many must connect before the generation
     * can start
     */
    public int[] allowedNumbersOfBots;
}