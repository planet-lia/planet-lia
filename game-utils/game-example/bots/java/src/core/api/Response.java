package core.api;

import core.api.commands.Command;
import core.api.commands.Direction;
import core.api.commands.MoveCommand;

import java.util.ArrayList;

public class Response {
    public int __uid;
    private ArrayList<Command> commands = new ArrayList<>();

    public void moveUnit(Direction direction) {
        commands.add(new MoveCommand(direction));
    }
}
