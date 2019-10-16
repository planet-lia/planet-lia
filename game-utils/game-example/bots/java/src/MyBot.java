import com.google.gson.Gson;
import core.*;
import core.api.*;
import core.api.commands.Direction;

/**
 * Example Java bot implementation for Planet Lia game-template.
 * To change this so that it can be used in some other Planet Lia game:
 * 1. Change InitialData, MatchState and Response objects to fit
 *   the API format of that game
 * 2. Update the basic MyBot implementation with basic bot logic
 */
public class MyBot implements Bot {

    InitialData data;

    // Called only once before the match starts. It holds the
    // data that you may need before the game starts.
    @Override
        public void setup(InitialData data) {
        System.out.println((new Gson()).toJson(data));
        this.data = data;
        System.out.println("There are " + data.__matchDetails.botsDetails.length + " bots in the match.");
    }

    // Called repeatedly while the match is generating. Each
    // time you receive the current match state and can use
    // response object to issue your commands.
    @Override
    public void update(MatchState state, Response response) {
        System.out.println((new Gson()).toJson(state));
        // Move in one of the four directions once per second
        double randomNumber = Math.random();
        if (randomNumber < 0.25) response.moveBox(Direction.LEFT);
        else if (randomNumber < 0.5) response.moveBox(Direction.RIGHT);
        else if (randomNumber < 0.75) response.moveBox(Direction.UP);
        else response.moveBox(Direction.DOWN);
    }

    // Connects your bot to match generator, don't change it.
    public static void main(String[] args) throws Exception {
        NetworkingClient.connectNew(args, new MyBot());
    }
}
