package core.api;

public class UnitData {
    public int id;
    public UnitType type;
    public int health;
    public float x;
    public float y;
    public float orientationAngle;
    public Speed speed;
    public Rotation rotation;
    public boolean canShoot;
    public int nBullets;
    public OpponentInView[] opponentsInView;
    public BulletInView[] opponentBulletsInView;
    public ResourceInView[] resourcesInView;
    public Point[] navigationPath;
}

