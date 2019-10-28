package com.planet_lia.match_generator.libs;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DefaultArgs {
    @Parameter(names = { "--help", "-h" }, help = true)
    public boolean help = false;

    @Parameter(description = "List of bots with their tokens and details in a format " +
            "<bot_1> <token_1> <details_1_json> <bot_2> <token_2> <details_2_json> ... "
            + "Note that bots will connect to the first bot spot that will match their token")
    private ArrayList<String> bots = new ArrayList<>();

    @Parameter(names = {"--teams", "-t"}, description = "Specify the teams for the bots in a format x:y:z:... " +
            "which means that first x provided bots belongs to the team 0, next y bots to team 1, " +
            "next z to team 3 etc. Note that the teams format must be supported by the game in order to work. " +
            "If the parameter is not provided, the teams are set up automatically depending on the game.")
    private String teams = "";

    @Parameter(names = {"--debug", "-d"}, description = "Run match-generator in debug mode")
    public boolean debug = false;

    @Parameter(names = {"--replay", "-r"}, description = "Specify the name and the path of a replay file for this" +
            "match, if not specified, the replay file will be named using a timestamp")
    public String replay = LocalDateTime.now().toString() + ".json";

    @Parameter(names = {"--config", "-c"}, description = "Specify a path to the game configuration file")
    public String config = "assets/game-config.json";

    @Parameter(names = {"--port", "-p"}, description = "Specify the port on which bots can connect")
    public int port = 8887;

    @Parameter(names = {"--bot-listener-token"}, description = "Token with which an external service can connect " +
            "and listen all communications between match-generator and all bots. Disabled if not provided")
    public String botListenerToken = DEFAULT_BOT_LISTENER_TOKEN;
    public static final String DEFAULT_BOT_LISTENER_TOKEN  = "";

    @Parameter(names = {"--window-to-screen", "-w"}, description = "Specify the ratio between debug window " +
            "(if -d/--debug flag is enabled) and the size of the monitor")
    public float debugWindowToScreen = 0.7f;

    /**
     * Sets up BotDetails for each bot and assigns them to teams.
     * @return list of BotDetails objects
     */
    public BotDetails[] getBotsDetails(GeneralConfig generalConfig) {
        // For each bot a token and details need to be provided
        if (bots.size() % 3 != 0) {
            throw new Error("Not all bots have token and details provided as parameters");
        }

        int numberOfBots = bots.size() / 3;
        BotDetails[] botsDetails = new BotDetails[numberOfBots];

        try {
            for (int i = 0; i < bots.size(); i += 3) {
                String botName = bots.get(i);
                String token = bots.get(i + 1);
                BotDetailsAdditional optional = (new Gson()).fromJson(bots.get(i + 2), BotDetailsAdditional.class);
                botsDetails[i / 3] = new BotDetails(botName, token, optional);
            }
        }
        catch (Exception e) {
            System.err.println("Failed to parse bots parameters, check that you have provided bots, " +
                    "tokens and optional parameters for each bot");
            throw e;
        }

        // Check if numbers of bots is allowed
        if (!generalConfig.isNumberOfBotsAllowed(botsDetails.length)) {
            throw new Error("Number of provided bots " + botsDetails.length +
                    " is not supported by this game");
        }

        // Assign bots to teams
        int[] teamSizes = getTeamSizes(generalConfig, numberOfBots);
        DefaultArgs.setTeams(teamSizes, botsDetails);

        return botsDetails;
    }


    /**
     * Parses teams argument to array of team sizes.
     * @return array of team sizes or null if argument not provided
     */
    private int[] getTeamSizes(GeneralConfig generalConfig, int numberOfBots) {
        if (teams.equals("")) {
                teams = createDefaultTeams(generalConfig, numberOfBots);
        }

        // Parse teams argument
        try {
            String[] teamSizesStr = teams.split(":");
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
     * Returns a first format provided in general config that
     * fits the provided numbers of bots
     */
    private String createDefaultTeams(GeneralConfig generalConfig, int numberOfBots) {
        for (TeamFormat format : generalConfig.allowedTeamFormats) {
            if (format.getNumberOfBots() == numberOfBots) {
                return format.format;
            }
        }
        throw new Error("Provided number of bots (" + numberOfBots + ") does not fit any supported teams formats");
    }

    /**
     * Assign bots to teams, this modifies teamIndex field in BotDetails
     */
    private static void setTeams(int[] teamSizes, BotDetails[] botsDetails) {
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
     * This method checks if the number of provided bots is allowed by the game.
     *
     * @param allowedNumbersOfBots - how many bots does a game allow in one match
     * @param numberOfBots - number of bots that will play in this match
     * @return if the number of bots is allowed by the game
     */
    private static boolean isNumberOfBotsAllowed(ArrayList<Integer> allowedNumbersOfBots, int numberOfBots) {
        for (int allowedNumber : allowedNumbersOfBots) {
            if (numberOfBots == allowedNumber) {
                return true;
            }
        }
        return false;
    }
}