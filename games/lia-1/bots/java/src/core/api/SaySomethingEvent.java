package core.api;

public class SaySomethingEvent implements Command {
    public String __type = SaySomethingEvent.class.getSimpleName();
    public int unitId;
    public String text;

    public SaySomethingEvent(int unitId, String text) {
        this.unitId = unitId;
        this.text = text;
    }
}
