package core;

import core.api.InitialData;
import core.api.MatchState;
import core.api.Response;

public interface Bot {
    void setup(InitialData data);
    void update(MatchState state, Response response);
}
