package com.planet_lia.match_generator_base.libs;

public class GeneralConfig {
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
}