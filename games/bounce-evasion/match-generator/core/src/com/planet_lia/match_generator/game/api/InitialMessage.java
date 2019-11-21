package com.planet_lia.match_generator.game.api;

import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.game.entities.Background;
import com.planet_lia.match_generator.game.entities.Coin;
import com.planet_lia.match_generator.game.entities.Saw;
import com.planet_lia.match_generator.game.entities.Unit;
import com.planet_lia.match_generator.libs.BaseInitialMessage;
import com.planet_lia.match_generator.libs.BotMessageType;

import java.util.ArrayList;

public class InitialMessage extends BaseInitialMessage {

    // Here add data specific to the game
    public int mapWidth;
    public int mapHeight;
    public boolean[][] map;

    public UnitData yourUnit;
    public UnitData opponentUnit;

    public CoinData[] coins;
    public SawData[] saws;

    public static InitialMessage create(Background background,
                                        Unit yourUnit,
                                        Unit opponentUnit,
                                        Coin[] coins,
                                        ArrayList<Saw> saws) {
        InitialMessage msg = new InitialMessage();
        msg.__type = BotMessageType.INITIAL;

        msg.mapWidth = GameConfig.values.mapWidth;
        msg.mapHeight = GameConfig.values.mapHeight;

        msg.map = new boolean[msg.mapHeight][msg.mapWidth];
        for (int y = 0; y < msg.mapHeight; y++) {
            for (int x = 0; x < msg.mapWidth; x++) {
                msg.map[y][x] = background.tiles.get(y).get(x) != null;
            }
        }

        msg.yourUnit = new UnitData(yourUnit.x, yourUnit.y, yourUnit.points, yourUnit.lives);
        msg.opponentUnit = new UnitData(opponentUnit.x, opponentUnit.y, opponentUnit.points, opponentUnit.lives);

        msg.coins = new CoinData[coins.length];
        for (int i = 0; i < coins.length; i++) {
            msg.coins[i] = new CoinData(coins[i].x, coins[i].y);
        }

        msg.saws = new SawData[saws.size()];
        for (int i = 0; i < saws.size(); i++) {
            Saw saw = saws.get(i);
            msg.saws[i] = new SawData(saw.x, saw.y, saw.direction);
        }

        return msg;
    }
}
