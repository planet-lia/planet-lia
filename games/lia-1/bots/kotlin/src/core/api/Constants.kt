package core.api

/**
 * Constants are set on runtime from game engine, changing them has
 * no effect. Find the predefined values in data/game-config.json
 * or print them out in processGameEnvironment() method in your bot
 * implementation.
 */
class Constants(
    /** The duration of the game in seconds.  */
    var GAME_DURATION: Float,
    /** The width of the map in world units.  */
    var MAP_WIDTH: Int,
    /** The height of the map in world units.  */
    var MAP_HEIGHT: Int,
    /** Map as a 2D array of booleans. If map[x][y] equals True that means that at (x,y)
     * there is an obstacle. x=0, y=0 points to bottom left corner.  */
    var MAP: Array<BooleanArray>,
    /** Approximate location where your team was spawned.  */
    var SPAWN_POINT: SpawnPoint,
    /** The diameter of the unit in world units.  */
    var UNIT_DIAMETER: Float,
    /** A full health of a unit when the game starts.  */
    var UNIT_FULL_HEALTH: Int,
    /** The velocity in world units per second with which the unit moves forward.  */
    var UNIT_FORWARD_VELOCITY: Float,
    /** The velocity in world units per second with which the unit moves backward.  */
    var UNIT_BACKWARD_VELOCITY: Float,
    /** The angle with which the unit's orientation changes per second when rotating normally.  */
    var UNIT_ROTATION_VELOCITY: Float,
    /** The angle with which the unit's orientation changes per second when rotating slowly.  */
    var UNIT_SLOW_ROTATION_VELOCITY: Float,
    /** Delay between shooting two pre-loaded bullets.  */
    var DELAY_BETWEEN_SHOTS: Float,
    /** The time to reload one bullet.  */
    var RELOAD_TIME: Float,
    /** A maximum number of bullets that a unit can hold at once.  */
    var MAX_BULLETS: Int,
    /** The time after which the unit starts to regenerate health after being hit by a bullet.  */
    var HEALTH_REGENERATION_DELAY: Float,
    /** The amount of health points per second that the unit receives when recovering.  */
    var HEALTH_REGENERATION_PER_SECOND: Int,
    /** The length of unit's viewing area.  */
    var VIEWING_AREA_LENGTH: Float,
    /** The width of unit's viewing area at the side that is the furthest away from the unit.  */
    var VIEWING_AREA_WIDTH: Float,
    /** The amount by which is the start of a viewing area offset from the unit's center
     * (negative means towards the back).  */
    var VIEWING_AREA_OFFSET: Float,
    /** The diameter of the bullet in world units.  */
    var BULLET_DIAMETER: Float,
    /** The speed in world units per second with which the bullet moves forward.  */
    var BULLET_VELOCITY: Float,
    /** The damage that a warrior receives when it is hit by a bullet.  */
    var BULLET_DAMAGE_TO_WARRIOR: Int,
    /** The damage that a worker receives when it is hit by a bullet.  */
    var BULLET_DAMAGE_TO_WORKER: Int,
    /** The range of the bullet in world units.  */
    var BULLET_RANGE: Float,
    /** Price in resources for purchasing a warrior unit.  */
    var WARRIOR_PRICE: Int,
    /** Price in resources for purchasing a worker unit.  */
    var WORKER_PRICE: Int,
    /** Maximum number of units on your team.  */
    var MAX_NUMBER_OF_UNITS: Float,
    /** After how many seconds new resources stop spawning  */
    var STOP_SPAWNING_AFTER: Int,
    /** The maximum duration of the first update() call.  */
    var FIRST_TICK_TIMEOUT: Float,
    /** The maximum duration of each update() call after the first one.  */
    var TICK_TIMEOUT: Float
)

class SpawnPoint(
    var x: Float,
    var y: Float
)

