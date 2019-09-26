package com.planet_lia.match_generator.libs;

import com.beust.jcommander.JCommander;
import com.planet_lia.match_generator.libs.BotListener.MessageSender;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.planet_lia.match_generator.libs.BotListener.MessageSender.BOT;
import static com.planet_lia.match_generator.libs.BotListener.MessageSender.MATCH_GENERATOR;
import static com.planet_lia.match_generator.libs.BotMessageType.INITIAL;
import static com.planet_lia.match_generator.libs.BotMessageType.UPDATE;
import static com.planet_lia.match_generator.libs.BotServer.BOT_LISTENER_TOKEN_HEADER_KEY;
import static com.planet_lia.match_generator.libs.BotServer.injectGeneralFieldsToJsonData;
import static org.junit.jupiter.api.Assertions.*;

class BotServerTest {
    @Test
    void badBotParameters() {
        GeneralConfig config = new GeneralConfig();
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};

        assertThrows(Error.class, () -> argsToBotDetails(config, "b1", "t1", "{}", "b2"));
    }

    @Test
    void isNumberOfBotsAllowed() {
        GeneralConfig config = new GeneralConfig();
        Timer timer = new Timer();

        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};
        assertThrows(Error.class, () ->
                new BotServer(config, timer, 9000, argsToBotDetails(config, "b1", "t1", "{}")));
        assertThrows(Error.class, () ->
                new BotServer(config, timer, 9000, argsToBotDetails(config, "b1", "t1", "{}", "b2", "t2")));
        assertThrows(Error.class, () ->
                new BotServer(config, timer, 9000, argsToBotDetails(config, "b1", "t1", "{}", "b2")));
        assertDoesNotThrow(() ->
                new BotServer(config, timer, 9000, argsToBotDetails(config, "b1", "t1",  "{}", "b2", "t2", "{}")));
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1:1:1", 1)};
        assertDoesNotThrow(() ->
                new BotServer(config, timer, 9000, argsToBotDetails(config,
                        "b1", "t1", "{}", "b2", "t2", "{}", "b3", "t3", "{}", "b4", "t4", "{}")));
    }

    @Test
    void botWrongToken() throws Exception {
        GeneralConfig config = new GeneralConfig();
        config.connectingBotsTimeout = 2;
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};
        BotDetails[] botsDetails = argsToBotDetails(config, "b1", "t1", "{}", "b2", "WRONG_TOKEN", "{}");
        int port = 9000;

        BotServer server = new BotServer(config, new Timer(), port, botsDetails);
        server.start();

        connectBot(port, "t1");
        connectBot(port, "t2");

        assertThrows(Error.class, server::waitForBotsToConnect);
        server.stop();
    }

    @Test
    void botsSameTokens() throws Exception {
        GeneralConfig config = new GeneralConfig();
        config.connectingBotsTimeout = 2;
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};
        BotDetails[] botsDetails = argsToBotDetails(config, "b1", "_", "{}", "b2", "_", "{}");
        int port = 9000;

        BotServer server = new BotServer(config, new Timer(), port, botsDetails);
        server.start();

        connectBot(port, "a");
        connectBot(port, "_");

        assertThrows(Error.class, server::waitForBotsToConnect);

        connectBot(port, "_");
        assertDoesNotThrow(server::waitForBotsToConnect);

        server.stop();
    }

    @Test
    void basicFlow() throws Exception {
        int port = 9000;
        GeneralConfig config = new GeneralConfig();
        Timer timer = new Timer();
        config.connectingBotsTimeout = 1;
        config.botFirstResponseTimeout = 0.2f;
        config.botResponseTimeout = 0.2f;
        config.maxTimeoutsPerBot = 2;
        config.botResponseTotalDurationMax = 100;
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};

        BotDetails[] botsDetails = argsToBotDetails(config,"b1", "t1", "{}", "b2", "t2", "{}");


        BotServer server = new BotServer(config, timer, port, botsDetails);
        server.start();

        ClientMock bot1 = connectBot(port, "t1");
        ClientMock bot2 = connectBot(port, "t2");

        // Test if bots connect
        assertDoesNotThrow(server::waitForBotsToConnect);

        server.sendToAll("{\"a\":\"request\"}", INITIAL);
        bot1.send("{\"a\":\"response1\"}");
        bot2.send("{\"a\":\"response2\"}");

        // Test if the response was received
        server.waitForBotsToRespond();
        assertTrue(bot1.receivedData.contains(
                injectGeneralFieldsToJsonData("{\"a\":\"request\"}", INITIAL, botsDetails)
        ));
        assertTrue(bot2.receivedData.contains(
                injectGeneralFieldsToJsonData("{\"a\":\"request\"}", INITIAL, botsDetails)
        ));
        assertEquals("{\"a\":\"response1\"}", server.getLastResponseData(0));
        assertEquals("{\"a\":\"response2\"}", server.getLastResponseData(1));
        assertEquals(0, server.getNumberOfTimeouts(0));
        assertEquals(0, server.getNumberOfTimeouts(1));
        assertFalse(server.isDisqualified(0));
        assertFalse(server.isDisqualified(1));

        server.stop();
    }

    @Test
    void botDisqualificationNumTimeoutsExceeded() throws Exception {
        GeneralConfig config = new GeneralConfig();
        Timer timer = new Timer();
        config.connectingBotsTimeout = 1;
        config.botFirstResponseTimeout = 0.2f;
        config.botResponseTimeout = 0.2f;
        config.maxTimeoutsPerBot = 2;
        config.botResponseTotalDurationMax = 2;
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};

        BotDetails[] botsDetails = argsToBotDetails(config,"b1", "t1", "{}", "b2", "t2", "{}");
        int port = 9000;
        BotServer server = new BotServer(config, timer, port, botsDetails);
        server.start();

        ClientMock bot1 = connectBot(port, "t1");
        connectBot(port, "t2");

        // Test if bots connect
        server.waitForBotsToConnect();

        // Should timeout
        server.sendToAll("request", INITIAL);
        server.waitForBotsToRespond();

        assertNull(server.getLastResponseData(0));
        assertNull(server.getLastResponseData(1));
        assertEquals(1, server.getNumberOfTimeouts(0));
        assertEquals(1, server.getNumberOfTimeouts(1));
        assertFalse(server.isDisqualified(0));
        assertFalse(server.isDisqualified(1));

        // Bot 2 should be disqualified
        timer.time += 1;
        server.sendToAll("request", UPDATE);
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
        server.sendToAll("request", UPDATE);
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


    @Test
    void botListenerDisabledTest() {
        GeneralConfig config = new GeneralConfig();
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};
        BotDetails[] botsDetails = argsToBotDetails(config, "b1", "t1", "{}", "b2", "t2", "{}");

        BotServer server = new BotServer(config, new Timer(), 9000, botsDetails);

        assertFalse(server.isBotListenerEnabled());
    }

    @Test
    void botListenerTest() throws Exception {
        GeneralConfig config = new GeneralConfig();
        config.connectingBotsTimeout = 1;
        config.botFirstResponseTimeout = 2f;
        config.botResponseTimeout = 2f;
        config.maxTimeoutsPerBot = 2;
        config.botResponseTotalDurationMax = 100;
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};
        BotDetails[] botsDetails = argsToBotDetails(config, "b1", "t1", "{}", "b2", "t2", "{}");
        int port = 9000;
        Timer timer = new Timer();
        String botListenerToken = "btt";

        BotServer server = new BotServer(config, timer, port, botsDetails, botListenerToken);
        server.start();

        ClientMock bot1 = connectBot(port, "t1");
        ClientMock bot2 = connectBot(port, "t2");

        // Test if bots connect
        assertDoesNotThrow(server::waitForBotsToConnect);
        assertTrue(server.isBotListenerEnabled());
        assertFalse(server.isBotListenerConnected());

        ClientMock listener = connectClient(port, BOT_LISTENER_TOKEN_HEADER_KEY, botListenerToken);
        Thread.sleep(100);
        assertTrue(server.isBotListenerConnected());

        // First exchange
        server.sendToAll("{\"a\":\"request1\"}", INITIAL);
        bot1.send("{\"a\":\"response1\"}");
        bot2.send("{\"a\":\"response2\"}");
        server.waitForBotsToRespond();

        // Second exchange
        server.sendToAll("{\"a\":\"request2\"}", UPDATE);
        bot1.send("{\"a\":\"response3\"}");
        bot2.send("{\"a\":\"response4\"}");
        server.waitForBotsToRespond();

        assertBotListenerContains(
                listener, "{\"a\":\"request1\"}", MATCH_GENERATOR, 0, INITIAL, botsDetails);
        assertBotListenerContains(
                listener, "{\"a\":\"request1\"}", MATCH_GENERATOR, 1, INITIAL, botsDetails);
        assertBotListenerContains(
                listener, "{\"a\":\"response1\"}", BOT, 0, null, null);
        assertBotListenerContains(
                listener, "{\"a\":\"response2\"}", BOT, 1, null, null);
        assertBotListenerContains(
                listener, "{\"a\":\"request2\"}", MATCH_GENERATOR, 0, UPDATE, null);
        assertBotListenerContains(
                listener, "{\"a\":\"request2\"}", MATCH_GENERATOR, 1, UPDATE, null);
        assertBotListenerContains(
                listener, "{\"a\":\"response3\"}", BOT, 0, null, null);
        assertBotListenerContains(
                listener, "{\"a\":\"response4\"}", BOT, 1, null, null);

        server.stop();
    }

    private void assertBotListenerContains(ClientMock botListener,
                                           String msg,
                                           MessageSender sender,
                                           int botIndex,
                                           BotMessageType msgType,
                                           BotDetails[] botsDetails) {
        assertTrue(botListener.receivedData.contains(
                BotListener.createMessage(
                        sender,
                        botIndex,
                        injectGeneralFieldsToJsonData(msg, msgType, botsDetails))));
    }

    @Test
    void onlyOneInitialMessageAllowed() throws Exception {
        GeneralConfig config = new GeneralConfig();
        config.connectingBotsTimeout = 1;
        config.botFirstResponseTimeout = 0.2f;
        config.botResponseTimeout = 0.2f;
        config.maxTimeoutsPerBot = 2;
        config.botResponseTotalDurationMax = 100;
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};
        BotDetails[] botsDetails = argsToBotDetails(config, "b1", "t1", "{}", "b2", "t2", "{}");
        int port = 9000;
        Timer timer = new Timer();

        BotServer server = new BotServer(config, timer, port, botsDetails);
        server.start();

        ClientMock bot1 = connectBot(port, "t1");
        ClientMock bot2 = connectBot(port, "t2");

        // Test if bots connect
        assertDoesNotThrow(server::waitForBotsToConnect);

        server.sendToAll("request", INITIAL);
        bot1.send("response1");
        bot2.send("response2");

        assertThrows(Error.class, () -> server.sendToAll("request2", INITIAL));

        server.stop();
    }

    ClientMock connectBot(int port, String token) throws URISyntaxException {
        return connectClient(port, "token", token);
    }

    ClientMock connectClient(int port, String headerKey, String headerValue) throws URISyntaxException {
        HashMap<String, String> header = new HashMap<>();
        header.put(headerKey, headerValue);
        ClientMock client = new ClientMock(new URI("ws://localhost:" + port), header);
        client.connect();
        return client;
    }

    BotDetails[] argsToBotDetails(GeneralConfig config, String... args) {
        // Parse arguments
        DefaultArgs defaultArgs = new DefaultArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(defaultArgs)
                .build();
        jCommander.parse(args);

        return defaultArgs.getBotsDetails(config);
    }
}

class ClientMock extends WebSocketClient {

    ArrayList<String> receivedData = new ArrayList<>();

    ClientMock(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {}

    @Override
    public void onMessage(String message) {
        this.receivedData.add(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {}

    @Override
    public void onError(Exception ex) {}
}