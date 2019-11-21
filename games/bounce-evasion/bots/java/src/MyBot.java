import com.google.gson.Gson;
import core.*;
import core.api.*;
import core.api.commands.Direction;

/**
 * Example Java bot implementation for Planet Lia Bounce Evasion.
 */
public class MyBot implements Bot {

    InitialData data;

    // Called only once before the match starts. It holds the
    // data that you may need before the game starts.
    @Override
    public void setup(InitialData data) {
        System.out.println((new Gson()).toJson(data));
        this.data = data;

        // Print out the map
        for (int y = data.mapHeight - 1; y >= 0; y--) {
            for (int x = 0; x < data.mapWidth; x++) {
                System.out.print((data.map[y][x]) ? "_" : "#");
            }
            System.out.println();
        }
    }

    // Called repeatedly while the match is generating. Each
    // time you receive the current match state and can use
    // response object to issue your commands.
    @Override
    public void update(MatchState state, Response response) {
        // Find and send your unit to a random direction that
        // moves it to a valid field on the map
        // TODO: Remove this code and implement a proper path finding!
        while (true) {
            double rand = Math.random();

            // Pick a random direction to move
            Direction direction;
            if (rand < 0.25) direction = Direction.LEFT;
            else if (rand < 0.5) direction = Direction.RIGHT;
            else if (rand < 0.75) direction = Direction.UP;
            else direction = Direction.DOWN;

            // Find on which position this move will send your unit
            int newX, newY;

            if (direction == Direction.LEFT) newX = state.yourUnit.x - 1;
            else if (direction == Direction.RIGHT) newX = state.yourUnit.x + 1;
            else newX = state.yourUnit.x;

            if (direction == Direction.UP) newY = state.yourUnit.y + 1;
            else if (direction == Direction.DOWN) newY = state.yourUnit.y - 1;
            else newY = state.yourUnit.y;

            // If the new position is on the map then send the unit towards
            // that direction and break the loop, else try again
            if (newX >= 0 && newY >= 0 && newX < data.mapWidth && newY < data.mapHeight && data.map[newY][newX]) {
                response.moveUnit(direction);
                break;
            }
        }
    }

    // Connects your bot to match generator, don't change it.
    public static void main(String[] args) throws Exception {
        NetworkingClient.connectNew(args, new MyBot());
    }
}
