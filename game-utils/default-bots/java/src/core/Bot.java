package core;

import core.api.Response;
import core.api.InitialData;
import core.api.MatchState;

public interface Bot {
    void setup(InitialData data);
    void update(MatchState data, Response response);
}
