package core.api;

import core.MatchDetails;

public class InitialData {
    public int __uid;
    public MatchDetails __matchDetails;
    public int mapWidth;
    public int mapHeight;
    public int sawSpawnDelay;
    public boolean[][] map;
    public Unit yourUnit;
    public Unit opponentUnit;
    public Coin[] coins;
    public Saw[] saws;
}
