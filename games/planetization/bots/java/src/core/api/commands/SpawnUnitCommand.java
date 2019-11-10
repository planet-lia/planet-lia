package core.api.commands;

public class SpawnUnitCommand implements Command {
    public String __type = this.getClass().getSimpleName();
    public int planetId;
    public UnitType type;

    public SpawnUnitCommand(int planetId, UnitType type) {
        this.planetId = planetId;
        this.type = type;
    }
}
