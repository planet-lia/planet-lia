package core;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = { "--help", "-h" }, help = true)
    public boolean help = false;

    @Parameter(names = {"--token", "-t"}, description = "Identification token with which bot can connect " +
            "to match generator")
    public String token = "_";

    @Parameter(names = {"--port", "-p"}, description = "Port of match generator to which to connect")
    public int port = 8887;
}
