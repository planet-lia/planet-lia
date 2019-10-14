package com.planet_lia.match_generator.logic.api;

import com.planet_lia.match_generator.libs.ApiMessage;
import com.planet_lia.match_generator.libs.BotMessageType;

public class MatchStateMessage extends ApiMessage {

    // Here add data specific to the game
    public float time;
    public UnitData unit;

    public MatchStateMessage(float time, UnitData unit) {
        __type = BotMessageType.UPDATE;
        this.time = time;
        this.unit = unit;
    }
}
