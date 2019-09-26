package com.planet_lia.match_generator.libs;

import org.java_websocket.WebSocket;

class BotConnection {

    BotDetails details;

    WebSocket connection;
    String lastResponseData;

    boolean waitingResponse = false;
    int currentRequestIndex = -1;
    long currentRequestTime = 0;
    int numberOfTimeouts = 0;
    boolean disqualified = false;
    float disqualificationTime = -1f;
    float responseTotalDuration = 0f;
    String disqualificationReason = "";
    boolean initialMessageSent = false;

    public BotConnection(BotDetails details) {
        this.details = details;
    }
}
