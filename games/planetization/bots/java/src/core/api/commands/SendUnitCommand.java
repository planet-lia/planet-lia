package core.api.commands;

public class SendUnitCommand implements Command {
    public String __type = this.getClass().getSimpleName();
    public int unitId;
    public int destinationPlanetId;

    public SendUnitCommand(int unitId, int destinationPlanetId) {
        this.unitId = unitId;
        this.destinationPlanetId = destinationPlanetId;
    }
}
