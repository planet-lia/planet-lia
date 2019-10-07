package core.api;

public class MatchState {
    public int __uid;
    public float time;
    public int numberOfOpponentUnits;
    public int resources;
    public boolean canSaySomething;
    public UnitData[] units;

    public int getUid() {
        return __uid;
    }
}
