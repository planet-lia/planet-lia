import core.*;
import core.api.*;

/**
 * The default Java bot implementation that is not specific
 * to any Planet Lia game but instead serves as a base for
 * all game specific bot implementations.
 */
public class MyBot implements Bot {

    // Called only once before the match starts. It holds the
    // data that you may need before the game starts.
    @Override
    public void setup(InitialData data) {
        // TODO
        // This is the default bot that is not configured
        // for any specific game.
        // See how to support a specific game:
        // - <TODO_LINK>
    }

    // Called repeatedly while the match is generating. Each
    // time you receive the current match state and can use
    // response object to issue your commands.
    @Override
    public void update(MatchState state, Response response) {
        // TODO
        // This is the default bot that is not configured
        // for any specific game.
        // See how to support a specific game:
        // - <TODO_LINK>
    }

    // Connects your bot to match generator, don't change it.
    public static void main(String[] args) throws Exception {
        NetworkingClient.connectNew(args, new MyBot());
    }
}