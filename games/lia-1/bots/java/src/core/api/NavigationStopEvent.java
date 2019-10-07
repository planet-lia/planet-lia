package core.api;

public class NavigationStopEvent implements Command {
    public String __type = NavigationStopEvent.class.getSimpleName();
    public int unitId;

    public NavigationStopEvent(int unitId) {
        this.unitId = unitId;
    }
}
