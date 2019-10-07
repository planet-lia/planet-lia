package core.api;

public class SpawnUnitEvent implements Command {
    public String __type = SpawnUnitEvent.class.getSimpleName();
    public UnitType type;

    public SpawnUnitEvent(UnitType type) {
        this.type = type;
    }
}
