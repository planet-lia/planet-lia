package com.planet_lia.match_generator_base.libs;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BotServerTest {
    @Test
    void badBotsAndTokens() throws Exception {
        String[] botsAndTokens = new String[]{"b1", "t1", "b2"};
        int[] allowedNumbersOfBots = new int[]{2};
        assertThrows(Error.class, () ->
                new BotServer(new GeneralConfig(), new Timer(), 9000, botsAndTokens, allowedNumbersOfBots));
    }

    @Test
    void isNumberOfBotsAllowed() throws Exception {
        GeneralConfig config = new GeneralConfig();
        Timer timer = new Timer();

        assertThrows(Error.class, () ->
                new BotServer(config, timer, 9000, new String[]{"b1", "t1"}, new int[]{2, 4, 5}));
        assertThrows(Error.class, () ->
                new BotServer(config, timer, 9000, new String[]{"b1", "t1", "b2"}, new int[]{2}));
        assertThrows(Error.class, () ->
                new BotServer(config, timer, 9000, new String[]{"b1", "t1", "b2"}, new int[]{1}));
        assertDoesNotThrow(() ->
                new BotServer(config, timer, 9000, new String[]{"b1", "t1", "b2", "t2"}, new int[]{2}));
        assertDoesNotThrow(() ->
                new BotServer(config, timer, 9000, new String[]{"b1", "t1", "b2", "t2", "b3", "t3", "b4", "t4"},
                        new int[]{2, 4}));
    }

    BotMock connectBot(int port, String token) throws URISyntaxException {
        HashMap<String, String> botHeader = new HashMap<>();
        botHeader.put("token", token);
        BotMock bot = new BotMock(new URI("ws://localhost:" + port), botHeader);
        bot.connect();
        return bot;
    }

    @Test
    void botWrongToken() throws Exception {
        String[] botsAndTokens = new String[]{"b1", "t1", "b2", "WRONG_TOKEN"};
        int[] allowedNumbersOfBots = new int[]{2};
        int port = 9000;
        GeneralConfig config = new GeneralConfig();
        config.connectingBotsTimeout = 2;

        BotServer server = new BotServer(config, new Timer(), port, botsAndTokens, allowedNumbersOfBots);

        connectBot(port, "t1");
        connectBot(port, "t2");

        assertThrows(Error.class, server::waitForBotsToConnect);
        server.stop();
    }

    @Test
    void botsSameTokens() throws Exception {
        String[] botsAndTokens = new String[]{"b1", "_", "b2", "_"};
        int[] allowedNumbersOfBots = new int[]{2};
        int port = 9000;
        GeneralConfig config = new GeneralConfig();
        config.connectingBotsTimeout = 2;

        BotServer server = new BotServer(config, new Timer(), port, botsAndTokens, allowedNumbersOfBots);

        connectBot(port, "a");
        connectBot(port, "_");

        assertThrows(Error.class, server::waitForBotsToConnect);

        connectBot(port, "_");
        assertDoesNotThrow(server::waitForBotsToConnect);

        server.stop();
    }

    @Test
    void basicFlow() throws Exception {
        String[] botsAndTokens = new String[]{"b1", "t1", "b2", "t2"};
        int[] allowedNumbersOfBots = new int[]{2};
        int port = 9000;
        GeneralConfig config = new GeneralConfig();
        Timer timer = new Timer();
        config.connectingBotsTimeout = 1;
        config.botFirstResponseTimeout = 0.2f;
        config.botResponseTimeout = 0.2f;
        config.maxTimeoutsPerBot = 2;
        config.botResponseTotalDurationMax = 100;

        BotServer server = new BotServer(config, timer, port, botsAndTokens, allowedNumbersOfBots);

        BotMock bot1 = connectBot(port, "t1");
        BotMock bot2 = connectBot(port, "t2");

        // Test if bots connect
        assertDoesNotThrow(server::waitForBotsToConnect);

        server.sendToAll("request");
        bot1.send("response1");
        bot2.send("response2");

        // Test if the response was received
        server.waitForBotsToRespond();
        assertEquals("response1", server.getLastResponseData(0));
        assertEquals("response2", server.getLastResponseData(1));
        assertEquals(0, server.getNumberOfTimeouts(0));
        assertEquals(0, server.getNumberOfTimeouts(1));
        assertFalse(server.isDisqualified(0));
        assertFalse(server.isDisqualified(1));

        server.stop();
    }

    @Test
    void botDisqualificationNumTimeoutsExceeded() throws Exception {
        String[] botsAndTokens = new String[]{"b1", "t1", "b2", "t2"};
        int[] allowedNumbersOfBots = new int[]{2};
        int port = 9000;
        GeneralConfig config = new GeneralConfig();
        Timer timer = new Timer();
        config.connectingBotsTimeout = 1;
        config.botFirstResponseTimeout = 0.2f;
        config.botResponseTimeout = 0.2f;
        config.maxTimeoutsPerBot = 2;
        config.botResponseTotalDurationMax = 2;

        BotServer server = new BotServer(config, timer, port, botsAndTokens, allowedNumbersOfBots);

        BotMock bot1 = connectBot(port, "t1");
        connectBot(port, "t2");

        // Test if bots connect
        server.waitForBotsToConnect();

        // Should timeout
        server.sendToAll("request");
        server.waitForBotsToRespond();

        assertNull(server.getLastResponseData(0));
        assertNull(server.getLastResponseData(1));
        assertEquals(1, server.getNumberOfTimeouts(0));
        assertEquals(1, server.getNumberOfTimeouts(1));
        assertFalse(server.isDisqualified(0));
        assertFalse(server.isDisqualified(1));

        // Bot 2 should be disqualified
        timer.time += 1;
        server.sendToAll("request");
        bot1.send("response1");
        // This send should be ignored
        bot1.send("response3");
        server.waitForBotsToRespond();

        assertEquals("response1", server.getLastResponseData(0));
        assertNull(server.getLastResponseData(1));
        assertEquals(1, server.getNumberOfTimeouts(0));
        assertEquals(2, server.getNumberOfTimeouts(1));
        assertFalse(server.isDisqualified(0));
        assertTrue(server.isDisqualified(1));
        assertEquals(-1f, server.getDisqualificationTime(0));
        assertEquals(1f, server.getDisqualificationTime(1));
        assertTrue(server.getDisqualificationReason(1).length() > 0);
        assertFalse(server.allBotsDisqualified());

        // Both bots disqualified
        timer.time += 1;
        server.sendToAll("request");
        server.waitForBotsToRespond();

        assertNull(server.getLastResponseData(0));
        assertEquals(2, server.getNumberOfTimeouts(0));
        assertEquals(2, server.getNumberOfTimeouts(1));
        assertTrue(server.isDisqualified(0));
        assertTrue(server.isDisqualified(1));
        assertEquals(2f, server.getDisqualificationTime(0));
        assertEquals(1f, server.getDisqualificationTime(1));
        assertTrue(server.allBotsDisqualified());

        server.stop();
    }
}

class BotMock extends WebSocketClient {

    BotMock(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {}

    @Override
    public void onMessage(String message) {}

    @Override
    public void onClose(int code, String reason, boolean remote) {}

    @Override
    public void onError(Exception ex) {}
}