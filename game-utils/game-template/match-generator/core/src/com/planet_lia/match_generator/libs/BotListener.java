package com.planet_lia.match_generator.libs;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;


class BotListener {

    enum MessageSender {
        MATCH_GENERATOR, BOT
    }

    WebSocket connection;
    String token;
    private static Gson gson = new Gson();

    BotListener(String token) {
        this.token = token;
    }

    void send(MessageSender sender, int botIndex, Object data) {
        this.connection.send(createMessage(sender, botIndex, data));
    }

    public static String createMessage(MessageSender sender, int botIndex, Object data) {
        return gson.toJson(new BotListenerMessage(sender, botIndex, data));
    }
}
