package core.api;

public class ShootEvent implements Command {
    public String __type = ShootEvent.class.getSimpleName();
    public int unitId;

    public ShootEvent(int unitId) {
        this.unitId = unitId;
    }
}
