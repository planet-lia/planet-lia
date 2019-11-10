package core.api;

import core.api.commands.UnitType;

public class UnitData {
    public int id;
    public UnitType type;
    public float x, y;
    public float rotation;
    public float health;
    public int currentPlanetId;
    public int destinationPlanetId;
}
