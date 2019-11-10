package com.planet_lia.match_generator.game;


import com.beust.jcommander.JCommander;
import com.planet_lia.match_generator.libs.DefaultArgs;

public class Args extends DefaultArgs {

    public static Args values;

    // Here you can put additional arguments for your own game
    // that are not already specified in DefaultArgs class.
    // Note to import com.beust.jcommander.Parameter.
    // eg.
    // @Parameter(names = { "--map-seed", "-m" },
    //            description = "Seed for random number generator for map generation")
    // public int mapSeed = 0;

    /**
     * Parses arguments to the Args.values static field
     */
    public static void parseArgs(String[] args) {
        values = new Args();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(values)
                .build();
        jCommander.parse(args);

        // If --help flag is provided, display help
        if (Args.values.help) {
            jCommander.setProgramName("match-generator.jar");
            jCommander.usage();
            System.exit(0);
        }
    }
}

