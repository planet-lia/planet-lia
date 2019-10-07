package core.api;

public class RotationEvent implements Command {
    public String __type = RotationEvent.class.getSimpleName();
    public int unitId;
    public Rotation rotation;

    public RotationEvent(int unitId, Rotation rotation) {
        this.unitId = unitId;
        this.rotation = rotation;
    }
}
