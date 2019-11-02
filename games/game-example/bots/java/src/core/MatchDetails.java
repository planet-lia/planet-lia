package core;

public class MatchDetails {

    /**
     * Index of the bot in botsDetails to which
     * this instance of MatchDetails was sent to
     */
    public int yourBotIndex;

    /**
     * List of all bots that participate in this
     * match with their details
     */
    public BotDetails[] botsDetails;
}
