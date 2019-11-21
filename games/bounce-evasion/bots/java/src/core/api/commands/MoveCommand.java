package core.api.commands;

public class MoveCommand implements Command {
    public String __type = this.getClass().getSimpleName();
    public Direction direction;

    public MoveCommand(Direction direction) {
        this.direction = direction;
    }
}
