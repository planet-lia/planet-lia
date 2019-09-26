package com.planet_lia.match_generator.libs;

class BotDetailsAdvanced {
    String botName;
    String token;
    int teamIndex = -1;
    BotDetailsOptional optional;

    public BotDetailsAdvanced(String botName, String token, BotDetailsOptional optional) {
        this.botName = botName;
        this.token = token;
        this.optional = optional;
    }
}

class BotDetailsOptional {
    int rank = -1;
    // In the future we can add here things that are specific to a single
    // bot such as link to the storage that he can use during the match
    // generation etc., eg.
    // String linkToExternalStorage;
}