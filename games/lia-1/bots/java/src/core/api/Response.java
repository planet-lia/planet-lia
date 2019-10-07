package core.api;

import java.util.ArrayList;

public class Response {
    public int __uid;
    private ArrayList<Command> commands = new ArrayList<>();

    /** Change thrust speed of a unit */
    public void setSpeed(int unitId, Speed speed) {
        commands.add(new SpeedEvent(unitId, speed));
    }

    /** Change rotation speed of a unit */
    public void setRotation(int unitId, Rotation rotation) {
        commands.add(new RotationEvent(unitId, rotation));
    }

    /** Make a unit shoot */
    public void shoot(int unitId) {
        commands.add(new ShootEvent(unitId));
    }

    /** Start navigation */
    public void navigationStart(int unitId, float x, float y, boolean moveBackwards) {
        commands.add(new NavigationStartEvent(unitId, x, y, moveBackwards));
    }

    public void navigationStart(int unitId, float x, float y) {
        commands.add(new NavigationStartEvent(unitId, x, y, false));
    }

    /** Stop navigation */
    public void navigationStop(int unitId) {
        commands.add(new NavigationStopEvent(unitId));
    }

    /** Make your unit say something */
    public void saySomething(int unitId, String text) {
        commands.add(new SaySomethingEvent(unitId, text));
    }

    public void spawnUnit(UnitType type) {
        commands.add(new SpawnUnitEvent(type));
    }

    public void addCommand(Command command) {
        commands.add(command);
    }
}
