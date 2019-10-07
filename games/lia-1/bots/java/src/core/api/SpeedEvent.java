package core.api;

public class SpeedEvent implements Command {
    public String __type = SpeedEvent.class.getSimpleName();
    public int unitId;
    public Speed speed;

    public SpeedEvent(int unitId, Speed speed) {
        this.unitId = unitId;
        this.speed = speed;
    }
}
