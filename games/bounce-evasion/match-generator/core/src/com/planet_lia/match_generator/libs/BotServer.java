package com.planet_lia.match_generator.libs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.planet_lia.match_generator.libs.BotListener.MessageSender;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import static com.planet_lia.match_generator.libs.DefaultArgs.DEFAULT_BOT_LISTENER_TOKEN;

/**
 * Creates a WebSocketServer, waits for bots to connect to it
 * and manages the exchange of API requests and responses between
 * the match-generator and all the bots.
 */
public class BotServer {

    static final String BOT_LISTENER_TOKEN_HEADER_KEY = "botListenerToken";

    private GeneralConfig generalConfig;
    private Timer gameTimer;
    private BotDetails[] botsDetails;
    private WebSocketServer server;

    private ArrayList<BotConnection> bots;
    private BotListener botListener;
    private DebugGuiStage debugGuiStage;

    private int currentRequestIndex = 0;

    private Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
    private Gson gson;
    ObjectMapper objectMapper = new ObjectMapper();

    public BotServer(GeneralConfig generalConfig,
                     Timer gameTimer,
                     int port,
                     BotDetails[] botsDetails) {
        this(generalConfig, gameTimer, port, botsDetails, DEFAULT_BOT_LISTENER_TOKEN);
    }

    public BotServer(GeneralConfig generalConfig,
                     Timer gameTimer,
                     int port,
                     BotDetails[] botsDetails,
                     String botListenerToken) {
        server = createServer(port);
        this.generalConfig = generalConfig;
        this.gameTimer = gameTimer;
        this.botsDetails = botsDetails;

        bots = prepareBotConnections(botsDetails);

        // Only create bot listener if botListenerToken is not DEFAULT_BOT_LISTENER_TOKEN
        if (!botListenerToken.equals(DEFAULT_BOT_LISTENER_TOKEN)) {
            botListener = new BotListener(botListenerToken);
        }
    }

    /**
     * Starts the websocket server
     */
    public void start() {
        server.start();
        System.out.println("Bot server started on port " + server.getPort());
    }

    /**
     * Registers bot commands so that BotServer knows how to parse them from JSON
     * @param supportedBotCommandsClasses - array of Class objects for all supported bot commands
     */
    public void registerBotCommands(Class[] supportedBotCommandsClasses) {
        BotCommandDeserializer serializer = new BotCommandDeserializer(supportedBotCommandsClasses);
        gson = new GsonBuilder().registerTypeAdapter(BotCommand.class, serializer).create();
    }

    public void setDebugGuiStage(DebugGuiStage debugGuiStage) {
        this.debugGuiStage = debugGuiStage;
    }

    /**
     * Create BotConnection objects from an array of botNames and tokens.
     *
     * @param botsDetails - array details of all provided bots
     * @return array of initialized BotConnection objects without set connections
     */
    private static ArrayList<BotConnection> prepareBotConnections(BotDetails[] botsDetails) {
        ArrayList<BotConnection> bots = new ArrayList<>(botsDetails.length);
        for (BotDetails details : botsDetails) {
            bots.add(new BotConnection(details));
        }
        return bots;
    }

    /**
     * Waits for all the bots to connect. Throws an exception if not all bots are connected
     * after the timeout.
     *
     * @throws Exception
     */
    public void waitForBotsToConnect() throws Exception {
        System.out.println("Waiting for bots to connect");

        long timeoutMillis = Math.round(generalConfig.connectingBotsTimeout * 1000);
        long checkPeriodMillis = 10L;

        while (!areAllBotsConnected()) {
            Thread.sleep(checkPeriodMillis);
            timeoutMillis -= checkPeriodMillis;
            if (timeoutMillis <= 0) {
                for (BotConnection bot : bots) {
                    if (bot.connection == null) {
                        System.err.printf("Bot with name '%s' did not connect in time\n", bot.details.botName);
                    }
                }
                throw new Exception("Some bots failed to connect in time");
            }
        }

        System.out.println("All bots connected");
    }

    /**
     * @return true if all bots are connected to the bot server else false
     */
    private boolean areAllBotsConnected() {
        for (BotConnection bot : bots) {
            if (bot.connection == null) {
                return false;
            }
        }
        return true;
    }

    private void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Connect bot
        String token = handshake.getFieldValue("token");
        if (!token.equals("")) {
            for (BotConnection bot : bots) {
                if (bot.details.token.equals(token) && bot.connection == null) {
                    bot.connection = conn;
                    // Attach bot object to connection so that later we
                    // will know to which bot a connection belongs
                    bot.connection.setAttachment(bot);
                    System.out.printf("Bot '%s' has connected\n", bot.details.botName);
                    return;
                }
            }
        }
        // Connect bot listener
        if (botListener != null) {
            token = handshake.getFieldValue(BOT_LISTENER_TOKEN_HEADER_KEY);
            if (!token.equals("")) {
                if (botListener.token.equals(token) && botListener.connection == null) {
                    botListener.connection = conn;
                    System.out.println("Bot listener has connected");
                }
            }
        }
    }

    /**
     * Send data to all bots
     * @param message - ApiMessage object
     */
    public void sendToAll(ApiMessage message) {
        for (int i = 0; i < bots.size(); i++) {
            send(i, message);
        }
    }

    /**
     * Send data to a specific bot
     * @param botIndex - the index based on where it was provided to
     *                 the match-generator as a program argument
     * @param message - ApiMessage object
     */
    public void send(int botIndex, ApiMessage message) {
        BotConnection bot = bots.get(botIndex);

        if (bot.disqualified) {
            return;
        }
        if (message.__type == BotMessageType.INITIAL) {
            if (!bot.initialMessageSent) {
                bot.initialMessageSent = true;
            }
            else {
                System.err.printf("Bot '%s' already received initial message\n", bot.details.botName);
            }
        }
        if (bot.currentRequestIndex == currentRequestIndex) {
            System.err.printf("Can't send new message to bot '%s' " +
                    "because it already received its update this turn\n", bot.details.botName);
            return;
        }

        // Inject fields values
        message.__uid = currentRequestIndex;
        if (message.__type == BotMessageType.INITIAL) {
            ((BaseInitialMessage) message).__matchDetails = new MatchDetails(this.botsDetails, botIndex);
        }

        // Use Jackson as it is faster
        //String jsonData = gson.toJson(message);
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to convert message to json.");
            e.printStackTrace();
            return;
        }


        bot.currentRequestIndex = currentRequestIndex;
        bot.currentRequestTime = System.currentTimeMillis();

        if (botListener != null) {
            botListener.send(MessageSender.MATCH_GENERATOR, botIndex, message);
        }
        if (debugGuiStage != null) {
            debugGuiStage.addLog(botIndex, MessageSender.MATCH_GENERATOR, gsonPretty.toJson(message));
        }

        // Set this after sending to botListener for better synchronicity
        bot.waitingResponse = true;

        bot.connection.send(jsonData);
    }

    private void onMessage(WebSocket conn, String message) {
        BotConnection bot;

        try {
            bot = conn.getAttachment();
        } catch (Exception e) {
            // This message belongs to the botListener which we ignore
            return;
        }

        if (bot.disqualified) {
            return;
        }
        if (bot.currentRequestIndex != currentRequestIndex) {
            System.out.printf("Bot '%s' sent response too late\n", bot.details.botName);
            return;
        }
        if (!bot.waitingResponse) {
            System.out.printf("Bot '%s' already answered to this request\n", bot.details.botName);
            return;
        }

        BotResponse response;
        try {
            response = gson.fromJson(message, BotResponse.class);
        } catch (Exception e) {
            System.out.printf("Bot response for bot '%s' is not valid: %s\n", bot.details.botName, e.getMessage());
            return;
        }

        if (response.__uid != currentRequestIndex) {
            System.out.printf("Response from bot '%s' has incorrect index\n", bot.details.botName);
            return;
        }

        if (botListener != null) {
            botListener.send(MessageSender.BOT, bots.indexOf(bot), gson.fromJson(message, JsonObject.class));
        }

        if (debugGuiStage != null) {
            debugGuiStage.addLog(bots.indexOf(bot), MessageSender.BOT, gsonPretty.toJson(response));
        }

        bot.lastResponseData = message;
        bot.responseTotalDuration += System.currentTimeMillis() - bot.currentRequestTime;

        if (bot.responseTotalDuration >= generalConfig.botResponseTotalDurationMax * 1000) {
            disqualifyBot(bot, "bot used all available time");
        }

        // Set this after all other actions for better synchronicity.
        // It prevents from waitForBotsToRespond to finish before this
        // response is applied
        bot.waitingResponse = false;
    }

    ArrayList<BotConnection> getBotConnections() {
        return bots;
    }

    public int getNumberOfTimeouts(int botIndex) {
        return bots.get(botIndex).numberOfTimeouts;
    }

    public boolean isDisqualified(int botIndex) {
        return bots.get(botIndex).disqualified;
    }

    public float getDisqualificationTime(int botIndex) {
        return bots.get(botIndex).disqualificationTime;
    }

    public String getDisqualificationReason(int botIndex) {
        return bots.get(botIndex).disqualificationReason;
    }

    public <T> T getLastResponseData(int botIndex, Class<T> type) {
        return gson.fromJson(bots.get(botIndex).lastResponseData, type);
    }

    public boolean areAllBotsDisqualified() {
        for (BotConnection bot : bots) {
            if (!bot.disqualified) {
                return false;
            }
        }
        return true;
    }

    /**
     * Waits for all bots that received a request in this turn to respond or
     * timeout. Handles disqualifying bots if necessary.
     */
    public void waitForBotsToRespond() {
        long startTime = System.currentTimeMillis();
        float timeout = (currentRequestIndex == 0)
                ? generalConfig.botFirstResponseTimeout
                : generalConfig.botResponseTimeout;
        long timeoutMillis = (long) (timeout * 1000);

        // Wait for all bots to respond or timeout
        while (System.currentTimeMillis() - startTime <= timeoutMillis) {
            if (allBotsResponded()) {
                break;
            }
            try {
                Thread.sleep(0, 0);
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Handle bots that timed out
        for (BotConnection bot : bots) {
            if (!bot.disqualified && bot.waitingResponse) {
                handleBotNotResponded(bot);
            }
        }

        currentRequestIndex++;
    }

    private void handleBotNotResponded(BotConnection bot) {
        bot.numberOfTimeouts++;
        System.out.printf("Bot '%s' failed to return response in time, number of timeouts: %d\n",
                bot.details.botName, bot.numberOfTimeouts);

        if (bot.numberOfTimeouts >= generalConfig.maxTimeoutsPerBot) {
            disqualifyBot(bot, "bot timed out to many times");
        }
    }

    private void disqualifyBot(BotConnection bot, String reason) {
        bot.disqualificationTime = gameTimer.getTime();
        bot.disqualified = true;
        bot.lastResponseData = null;
        bot.disqualificationReason = reason;
        System.out.printf("Bot '%s' disqualified for the reason: %s\n", bot.details.botName, reason);
    }

    /**
     * @return - if all bots that received a request in this turn have already responded
     */
    private boolean allBotsResponded() {
        for (BotConnection bot : bots) {
            if (bot.disqualified || bot.currentRequestIndex != currentRequestIndex) {
                continue;
            }
            if (bot.waitingResponse) {
                return false;
            }
        }
        return true;
    }

    private void onClose(WebSocket conn, int code, String reason, boolean remote) {
        BotConnection bot = conn.getAttachment();
        System.out.printf("Connection for bot '%s' closed\n", bot.details.botName);
    }

    private void onError(WebSocket conn, Exception ex) {
        if (conn == null) {
            System.err.println("Websocket connection is null on onError call");
        }
        else {
            BotConnection bot = conn.getAttachment();
            System.err.printf("Connection for bot '%s' threw an Exception\n", bot.details.botName);
        }
        ex.printStackTrace();
    }

    public boolean isBotListenerEnabled() {
        return botListener != null;
    }

    public boolean isBotListenerConnected() {
        return botListener.connection != null;
    }

    /**
     * Stops the WebSocketServer
     * @throws IOException
     * @throws InterruptedException
     */
    public void stop() throws IOException, InterruptedException {
        server.stop();
    }

    /**
     * Creates a WebSocketServer and binds it to the methods of the BotServer class
     * @param port - port on which to run the server
     * @return - the websocket server
     */
    private WebSocketServer createServer(int port) {
        BotServer botServer = this;

        // Configure websocket server
        WebSocketServer server = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                botServer.onOpen(conn, handshake);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                botServer.onClose(conn, code, reason, remote);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                botServer.onMessage(conn, message);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                botServer.onError(conn, ex);
            }

            @Override
            public void onStart() {}
        };
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.setConnectionLostTimeout(0);
        return server;
    }
}
