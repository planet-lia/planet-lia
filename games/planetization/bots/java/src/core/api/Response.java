package core.api;

import core.api.commands.Command;
import core.api.commands.SendUnitCommand;
import core.api.commands.SpawnUnitCommand;
import core.api.commands.UnitType;

import java.util.ArrayList;

public class Response {
    public int __uid;
    private ArrayList<Command> commands = new ArrayList<>();

    public void spawnUnit(int planetId, UnitType type)  {
        commands.add(new SpawnUnitCommand(planetId, type));
    }

    public void sendUnit(int unitId, int destinationPlanetId)  {
        commands.add(new SendUnitCommand(unitId, destinationPlanetId));
    }
}
