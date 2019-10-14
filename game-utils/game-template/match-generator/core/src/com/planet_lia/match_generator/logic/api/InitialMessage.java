package com.planet_lia.match_generator.logic.api;

import com.planet_lia.match_generator.libs.BaseInitialMessage;
import com.planet_lia.match_generator.libs.BotMessageType;

public class InitialMessage extends BaseInitialMessage {

    // Here add data specific to the game
    public int mapWidth;
    public int mapHeight;
    public int unitSize;

    public InitialMessage() {
        __type = BotMessageType.INITIAL;
    }
}
