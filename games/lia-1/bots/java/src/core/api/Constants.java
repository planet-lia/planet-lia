package core.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Constants are set on runtime from game engine, changing them has
 * no effect. Find the predefined values in data/game-config.json
 * or print them out in processGameEnvironment() method in your bot
 * implementation.
 */
public class Constants {
    /** The duration of the game in seconds. */
    public float GAME_DURATION;
    /** The width of the map in world units. */
    public int MAP_WIDTH;
    /** The height of the map in world units. */
    public int MAP_HEIGHT;
    /** Map as a 2D array of booleans. If map[x][y] equals True that means that at (x,y)
     * there is an obstacle. x=0, y=0 points to bottom left corner. */
    public boolean[][] MAP;
    /** Approximate location where your team was spawned. */
    public SpawnPoint SPAWN_POINT;
    /** The diameter of the unit in world units. */
    public float UNIT_DIAMETER;
    /** A full health of a unit when the game starts. */
    public int UNIT_FULL_HEALTH;
    /** The velocity in world units per second with which the unit moves forward. */
    public float UNIT_FORWARD_VELOCITY;
    /** The velocity in world units per second with which the unit moves backward. */
    public float UNIT_BACKWARD_VELOCITY;
    /** The angle with which the unit's orientation changes per second when rotating normally. */
    public float UNIT_ROTATION_VELOCITY;
    /** The angle with which the unit's orientation changes per second when rotating slowly. */
    public float UNIT_SLOW_ROTATION_VELOCITY;
    /** Delay between shooting two pre-loaded bullets. */
    public float DELAY_BETWEEN_SHOTS;
    /** The time to reload one bullet. */
    public float RELOAD_TIME;
    /** A maximum number of bullets that a unit can hold at once. */
    public int MAX_BULLETS;
    /** The time after which the unit starts to regenerate health after being hit by a bullet. */
    public float HEALTH_REGENERATION_DELAY;
    /** The amount of health points per second that the unit receives when recovering. */
    public int HEALTH_REGENERATION_PER_SECOND;
    /** The length of unit's viewing area. */
    public float VIEWING_AREA_LENGTH;
    /** The width of unit's viewing area at the side that is the furthest away from the unit. */
    public float VIEWING_AREA_WIDTH;
    /** The amount by which is the start of a viewing area offset from the unit's center
     * (negative means towards the back). */
    public float VIEWING_AREA_OFFSET;
    /** The diameter of the bullet in world units. */
    public float BULLET_DIAMETER;
    /** The speed in world units per second with which the bullet moves forward. */
    public float BULLET_VELOCITY;
    /** The damage that a warrior receives when it is hit by a bullet. */
    public int BULLET_DAMAGE_TO_WARRIOR;
    /** The damage that a worker receives when it is hit by a bullet. */
    public int BULLET_DAMAGE_TO_WORKER;
    /** The range of the bullet in world units. */
    public float BULLET_RANGE;
    /** Price in resources for purchasing a warrior unit. */
    public int WARRIOR_PRICE;
    /** Price in resources for purchasing a worker unit. */
    public int WORKER_PRICE;
    /** Maximum number of units on your team. */
    public float MAX_NUMBER_OF_UNITS;
    /** After how many seconds new resources stop spawning */
    public int STOP_SPAWNING_AFTER;
    /** The maximum duration of the first update() call. */
    public float FIRST_TICK_TIMEOUT;
    /** The maximum duration of each update() call after the first one. */
    public float TICK_TIMEOUT;

    public static class SpawnPoint {
        public float x;
        public float y;
    }
}

