package com.planet_lia.match_generator.libs;

import org.java_websocket.WebSocket;


class BotListener {

    enum MessageSender {
        MATCH_GENERATOR, BOT
    }

    WebSocket connection;
    String token;

    BotListener(String token) {
        this.token = token;
    }

    void send(MessageSender sender, int botIndex, String data) {
        this.connection.send(createMessage(sender,botIndex, data));
    }

    static String createMessage(MessageSender sender, int botIndex, String data) {
        data = String.format(
                "{" +
                        "\"sender\": \"%s\"," +
                        "\"botIndex\":%d," +
                        "\"data\":%s" +
                "}",
                sender.toString(), botIndex, data);
        return data;
    }
}
