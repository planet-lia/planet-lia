package com.planet_lia.match_generator.libs;

import org.java_websocket.WebSocket;

class BotConnection {

    String botName;
    String token;
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

    BotConnection(String botName, String token) {
        this.botName = botName;
        this.token = token;
    }
}
