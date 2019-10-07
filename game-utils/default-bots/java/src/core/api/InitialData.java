package core.api;

import core.MatchDetails;

import java.util.HashMap;

public class InitialData extends HashMap<String, Object> {
    // TODO remove extends HashMap and add fields specific to your game

    // TODO If you provided your own implementation for the game, uncomment this:
    // public MatchDetails[] __matchDetails;

    // TODO If you provided your own implementation for the game then create
    //      normal __uid field and return it here
    public int getUid() {
        return (int) (double) get("__uid");
    }
}
