package com.planet_lia.match_generator.libs;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DefaultArgs {
    @Parameter(names = { "--help", "-h" }, help = true)
    public boolean help = false;

    @Parameter(description = "List of bots with their tokens and details in a format " +
            "<bot_1> <token_1> <details_1_json> <bot_2> <token_2> <details_2_json> ... "
            + "Note that bots will connect to the first bot spot that will match their token")
    private ArrayList<String> bots = new ArrayList<>();

    @Parameter(names = {"--teams", "-t"}, description = "Specify the teams for the bots in a format x:y:z:... " +
            "which means that first x provided bots belongs to the team 0, next y bots to team 1, " +
            "next z to team 3 etc. Note that the teams format must be supported by the game in order to work")
    private String teams = null;

    @Parameter(names = {"--debug", "-d"}, description = "Run match-generator in debug mode")
    public boolean debug = false;

    @Parameter(names = {"--replay", "-r"}, description = "Specify the name and the path of a replay file for this\n" +
            "match, if not specified, the replay file will be named using a timestamp")
    public String replay = "";

    @Parameter(names = {"--config", "-c"}, description = "Specify a path to the game configuration file")
    public String config = "";

    @Parameter(names = {"--port", "-p"}, description = "Specify the port on which bots can connect")
    public int port = 8887;

    @Parameter(names = {"--bot-listener-token"}, description = "Token with which an external service can connect " +
            "and listen all communications between match-generator and all bots. Disabled if not provided")
    public String botListenerToken = DEFAULT_BOT_LISTENER_TOKEN;
    public static final String DEFAULT_BOT_LISTENER_TOKEN  = "";

    /**
     * Parses teams argument to array of team sizes.
     * @return array of team sizes or null if argument not provided
     */
    public int[] getTeamSizes() {
        if (this.teams == null) {
            return null;
        }

        // Parse teams argument
        try {
            String[] teamSizesStr = this.teams.split(":");
            int[] teamSizes = new int[teamSizesStr.length];
            for (int i = 0; i < teamSizesStr.length; i++) {
                teamSizes[i] = Integer.parseInt(teamSizesStr[i]);
            }
            return teamSizes;
        }
        catch (Exception e) {
            System.err.println("Failed to parse --teams/-t argument");
            throw e;
        }
    }

    /**
     * Assign bots to teams, this modifies teamIndex field in BotDetails
     * @param teamSizes
     * @param botsDetails
     */
    public static void setTeams(int[] teamSizes, BotDetailsAdvanced[] botsDetails) {
        if (teamSizes == null) {
            throw new Error("teamSizes is null, provide a --teams flag with parameters or specify custom teamSizes");
        }

        // Check that teamSizes matches with number of bots in botsDetails
        int numTeamBotSlots = 0;
        for (int size : teamSizes) {
            numTeamBotSlots += size;
        }
        if (numTeamBotSlots != botsDetails.length) {
            throw new Error("Number of bot slots in teams does not match with number of provided bots");
        }

        // Assign bots to teams
        for (int botIndex = 0; botIndex < botsDetails.length; botIndex++) {
            botsDetails[botIndex].teamIndex = findTeam(botIndex, teamSizes);
        }
    }

    private static int findTeam(int botIndex, int[] teamSizes) {
        int count = 0;
        for (int teamIndex = 0; teamIndex < teamSizes.length; teamIndex++) {
            count += teamSizes[teamIndex];
            if (botIndex < count) {
                return teamIndex;
            }
        }
        throw new Error("There are more bots provided than slots in all teams");
    }

    /**
     * Parses bots with their tokens and optional details.
     * @return array of BotDetails
     */
    public BotDetailsAdvanced[] getBotsDetails() {
        return parseBotDetails(this.bots);
    }

    static BotDetailsAdvanced[] parseBotDetails(List<String> parameters) {
        // For each bot a token and details need to be provided
        if (parameters.size() % 3 != 0) {
            throw new Error("Not all bots have token and details provided as parameters");
        }

        int numberOfBots = parameters.size() / 3;
        BotDetailsAdvanced[] botsDetails = new BotDetailsAdvanced[numberOfBots];

        try {
            for (int i = 0; i < parameters.size(); i += 3) {
                String botName = parameters.get(i);
                String token = parameters.get(i + 1);
                BotDetailsOptional optional = (new Gson()).fromJson(parameters.get(i + 2), BotDetailsOptional.class);
                botsDetails[i / 3] = new BotDetailsAdvanced(botName, token, optional);
            }
        }
        catch (Exception e) {
            System.err.println("Failed to parse bots parameters, check that you have provided bots, tokens and " +
                    "optional parameters for each bot");
            throw e;
        }

        return botsDetails;
    }
}