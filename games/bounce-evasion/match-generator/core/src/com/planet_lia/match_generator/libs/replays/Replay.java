package com.planet_lia.match_generator.libs.replays;

import java.util.ArrayList;

public class Replay {
    public GameDetails gameDetails;
    public ArrayList<MatchDetail> matchDetails = new ArrayList<>();
    public ArrayList<BotReplayDetails> botDetails = new ArrayList<>();
    public int[] teamsFinalOrder;
    public ArrayList<Chart> charts = new ArrayList<>();
    public ArrayList<Section> sections = new ArrayList<>();

    public Replay(GameDetails gameDetails) {
        this.gameDetails = gameDetails;
    }
}
