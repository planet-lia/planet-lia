package com.planet_lia.match_generator.game.api;

import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.game.entities.Background;
import com.planet_lia.match_generator.game.entities.Saw;
import com.planet_lia.match_generator.game.entities.Unit;
import com.planet_lia.match_generator.libs.ApiMessage;
import com.planet_lia.match_generator.libs.BotMessageType;

import java.util.ArrayList;

public class MatchStateMessage extends ApiMessage {

    // Here add data specific to the game
    public float time;

    public UnitData yourUnit;
    public UnitData opponentUnit;
    public SawData[] saws;

    public static MatchStateMessage create(float time, Unit yourUnit, Unit opponentUnit, ArrayList<Saw> saws) {
        MatchStateMessage msg = new MatchStateMessage();
        msg.__type = BotMessageType.UPDATE;
        msg.time = time;

        msg.yourUnit = new UnitData(yourUnit.x, yourUnit.y, yourUnit.points, yourUnit.lives);
        msg.opponentUnit = new UnitData(opponentUnit.x, opponentUnit.y, opponentUnit.points, opponentUnit.lives);

        msg.saws = new SawData[saws.size()];
        for (int i = 0; i < saws.size(); i++) {
            Saw saw = saws.get(i);
            msg.saws[i] = new SawData(saw.x, saw.y, saw.direction);
        }

        return msg;
    }
}
