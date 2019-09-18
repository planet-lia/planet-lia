package com.planet_lia.match_generator_base.libs;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;

public class DefaultArgs {
    @Parameter(description = "List of bots with their tokens in a format <bot_1> <token_1> <bot_2> <token_2> ... "
            + "Note that bots will connect to the first bot spot that will match their token")
    public ArrayList<String> bots = new ArrayList<>();

    @Parameter(names = {"--debug", "-d"}, description = "Run match-generator in debug mode")
    public boolean debug = false;

    @Parameter(names = {"--replay", "-r"}, description = "Specify the name and the path of a replay file for this\n" +
            "match, if not specified, the replay file will be named using a timestamp")
    public String replay = "";

    @Parameter(names = {"--config", "-c"}, description = "Specify a path to the game configuration file")
    public String config = "";

    @Parameter(names = {"--port", "-p"}, description = "Specify the port on which bots can connect, default is 8887")
    public int port = 8887;
}