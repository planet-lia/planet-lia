package com.planet_lia.match_generator.game.api.commands;

import com.planet_lia.match_generator.game.entities.Unit;
import com.planet_lia.match_generator.libs.BotCommand;

public class SpawnUnitCommand implements BotCommand {
    public int planetId;
    public Unit.Type type;
}
