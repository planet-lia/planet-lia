package core.api;

public class NavigationStartEvent implements Command {
    public String __type = NavigationStartEvent.class.getSimpleName();
    public int unitId;
    public float x;
    public float y;
    public boolean moveBackwards;

    public NavigationStartEvent(int unitId, float x, float y, boolean moveBackwards) {
        this.unitId = unitId;
        this.x = x;
        this.y = y;
        this.moveBackwards = moveBackwards;
    }
}
