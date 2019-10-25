package com.planet_lia.match_generator.libs;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
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
        server.registerBotCommands(new Class[]{});

        server.start();

        ClientMock bot1 = connectBot(port, "t1");
        ClientMock bot2 = connectBot(port, "t2");

        // Test if bots connect
        assertDoesNotThrow(server::waitForBotsToConnect);

        server.sendToAll(new InitialMessageMock("request"));
        bot1.send(toJson(new BotResponseMock("response1", 0)));
        bot2.send(toJson(new BotResponseMock("response2", 0)));

        // Test if the response was received
        server.waitForBotsToRespond();
        assertTrue(bot1.receivedData.contains(
                toJson(new InitialMessageMock("request", 0, new MatchDetails(botsDetails, 0)))
        ));
        assertTrue(bot2.receivedData.contains(
                toJson(new InitialMessageMock("request", 0, new MatchDetails(botsDetails, 1)))
        ));
        assertEquals(toJson(new BotResponseMock("response1", 0)),
                toJson(server.getLastResponseData(0, BotResponseMock.class)));
        assertEquals(toJson(new BotResponseMock("response2", 0)),
                toJson(server.getLastResponseData(1, BotResponseMock.class)));
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
        server.registerBotCommands(new Class[]{});
        server.start();

        ClientMock bot1 = connectBot(port, "t1");
        connectBot(port, "t2");

        // Test if bots connect
        server.waitForBotsToConnect();

        // Should timeout
        server.sendToAll(new InitialMessageMock("request"));
        server.waitForBotsToRespond();

        assertNull(server.getLastResponseData(0, BotResponseMock.class));
        assertNull(server.getLastResponseData(1, BotResponseMock.class));
        assertEquals(1, server.getNumberOfTimeouts(0));
        assertEquals(1, server.getNumberOfTimeouts(1));
        assertFalse(server.isDisqualified(0));
        assertFalse(server.isDisqualified(1));

        // Bot 2 should be disqualified
        timer.add(1);
        server.sendToAll(new MatchStateMessageMock("request", 1));
        bot1.send(toJson(new BotResponseMock("response1", 1)));
        bot1.send(toJson(new BotResponseMock("response1", 1)));
        // This send should be ignored
        bot1.send(toJson(new BotResponseMock("response3", 1)));
        server.waitForBotsToRespond();

        assertEquals(toJson(new BotResponseMock("response1", 1)),
                toJson(server.getLastResponseData(0, BotResponseMock.class)));
        assertNull(server.getLastResponseData(1, BotResponseMock.class));
        assertEquals(1, server.getNumberOfTimeouts(0));
        assertEquals(2, server.getNumberOfTimeouts(1));
        assertFalse(server.isDisqualified(0));
        assertTrue(server.isDisqualified(1));
        assertEquals(-1f, server.getDisqualificationTime(0));
        assertEquals(1f, server.getDisqualificationTime(1));
        assertTrue(server.getDisqualificationReason(1).length() > 0);
        assertFalse(server.areAllBotsDisqualified());

        // Both bots disqualified
        timer.add(1);
        server.sendToAll(new MatchStateMessageMock("request"));
        server.waitForBotsToRespond();

        assertNull(server.getLastResponseData(0, BotResponseMock.class));
        assertEquals(2, server.getNumberOfTimeouts(0));
        assertEquals(2, server.getNumberOfTimeouts(1));
        assertTrue(server.isDisqualified(0));
        assertTrue(server.isDisqualified(1));
        assertEquals(2f, server.getDisqualificationTime(0));
        assertEquals(1f, server.getDisqualificationTime(1));
        assertTrue(server.areAllBotsDisqualified());

        server.stop();
    }


    @Test
    void botListenerDisabledTest() {
        GeneralConfig config = new GeneralConfig();
        config.allowedTeamFormats = new TeamFormat[] {new TeamFormat("1:1", 1)};
        BotDetails[] botsDetails = argsToBotDetails(config, "b1", "t1", "{}", "b2", "t2", "{}");

        BotServer server = new BotServer(config, new Timer(), 9000, botsDetails);
        server.registerBotCommands(new Class[]{});

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
        server.registerBotCommands(new Class[]{});
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
        server.sendToAll(new InitialMessageMock("request1"));
        bot1.send(toJson(new BotResponseMock("response1", 0)));
        bot2.send(toJson(new BotResponseMock("response2", 0)));
        server.waitForBotsToRespond();

        // Second exchange
        server.sendToAll(new MatchStateMessageMock("request2"));
        bot1.send(toJson(new BotResponseMock("response3", 1)));
        bot2.send(toJson(new BotResponseMock("response4", 1)));
        server.waitForBotsToRespond();

        MatchDetails matchDetails = new MatchDetails(botsDetails, 0);
        assertBotListenerContains(
                listener,
                new InitialMessageMock("request1", 0, matchDetails),
                MATCH_GENERATOR, 0, INITIAL, 0, matchDetails);
        matchDetails = new MatchDetails(botsDetails, 1);
        assertBotListenerContains(
                listener,
                new InitialMessageMock("request1", 0, matchDetails),
                MATCH_GENERATOR, 1, INITIAL, 0, matchDetails);
        assertBotListenerContains(
                listener, new BotResponseMock("response1", 0), BOT, 0,
                null, 0, null);
        assertBotListenerContains(
                listener, new BotResponseMock("response2", 0), BOT, 1,
                null, 0, null);
        assertBotListenerContains(
                listener,
                new MatchStateMessageMock("request2", 1),
                MATCH_GENERATOR, 0, UPDATE, 1, null);
        assertBotListenerContains(
                listener,
                new MatchStateMessageMock("request2", 1),
                MATCH_GENERATOR, 1, UPDATE, 1, null);
        assertBotListenerContains(
                listener, new BotResponseMock("response3", 1), BOT, 0,
                null, 1, null);
        assertBotListenerContains(
                listener,  new BotResponseMock("response4", 1), BOT, 1,
                null, 1, null);

        server.stop();
    }

    private void assertBotListenerContains(ClientMock botListener,
                                           Object msg,
                                           MessageSender sender,
                                           int botIndex,
                                           BotMessageType msgType,
                                           int uid,
                                           MatchDetails matchDetails) {
        String a = BotListener.createMessage(
                sender,
                botIndex,
                msg);
        assertTrue(botListener.receivedData.contains(
                BotListener.createMessage(
                        sender,
                        botIndex,
                        msg)));
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
        server.registerBotCommands(new Class[]{});
        server.start();

        ClientMock bot1 = connectBot(port, "t1");
        ClientMock bot2 = connectBot(port, "t2");

        // Test if bots connect
        assertDoesNotThrow(server::waitForBotsToConnect);

        server.sendToAll(new InitialMessageMock("request1"));
        bot1.send(toJson(new BotResponseMock("response1", 0)));
        bot2.send(toJson(new BotResponseMock("response2", 0)));

        assertThrows(Error.class, () -> server.sendToAll(new InitialMessageMock("request2")));

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

    public String toJson(Object response) {
        return (new Gson()).toJson(response);
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

class InitialMessageMock extends BaseInitialMessage {

    String text;

    InitialMessageMock(String text) {
        __type = INITIAL;
        this.text = text;
    }

    InitialMessageMock(String text,  int uid, MatchDetails matchDetails) {
        __type = INITIAL;
        this.text = text;
        this.__uid = uid;
        this.__matchDetails = matchDetails;
    }
}

class MatchStateMessageMock extends ApiMessage {

    String text;

    MatchStateMessageMock(String text) {
        __type = UPDATE;
        this.text = text;
    }

    MatchStateMessageMock(String text,  int uid) {
        __type = UPDATE;
        this.text = text;
        this.__uid = uid;
    }
}

class BotResponseMock {

    String text;
    int __uid;

    BotResponseMock(String text, int uid) {
        this.text = text;
        this.__uid = uid;
    }
}