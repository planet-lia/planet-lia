package com.planet_lia.match_generator.libs;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * Creates a WebSocketServer, waits for bots to connect to it
 * and manages the exchange of API requests and responses between
 * the match-generator and all the bots.
 */
public class BotServer {

    private GeneralConfig generalConfig;
    private Timer gameTimer;
    private WebSocketServer server;

    private BotConnection[] bots;
    private int currentRequestIndex = 0;

    public BotServer(GeneralConfig generalConfig,
                     Timer gameTimer,
                     int port,
                     String[] botsAndTokens,
                     int[] allowedNumbersOfBots) {
        this.server = createServer(port);
        this.generalConfig = generalConfig;
        this.gameTimer = gameTimer;

        // For each bot a token needs to be provided
        if (botsAndTokens.length % 2 != 0) {
            throw new Error("More bots provided than tokens");
        }

        int numberOfBots = botsAndTokens.length / 2;

        if (!isNumberOfBotsAllowed(allowedNumbersOfBots, numberOfBots)) {
            throw new Error("Number of provided bots is not supported by this game, "
                    + "provided: " + (botsAndTokens.length / 2)
                    + " allowed: " + Arrays.toString(allowedNumbersOfBots));
        }

        bots = prepareBotConnection(botsAndTokens, numberOfBots);

        this.server.start();
        System.out.println("Bot server started on port " + port);
    }

    /**
     * This method checks if the number of provided bots is allowed by the game.
     *
     * @param allowedNumbersOfBots - how many bots does a game allow in one match
     * @param numberOfBots - number of bots that will play in this match
     * @return if the number of bots is allowed by the game
     */
    private static boolean isNumberOfBotsAllowed(int[] allowedNumbersOfBots, int numberOfBots) {
        for (int allowedNumber : allowedNumbersOfBots) {
            if (numberOfBots == allowedNumber) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create BotConnection objects from an array of botNames and tokens.
     *
     * @param botsAndTokens - array of bots and tokens for this match
     * @param numberOfBots - number of bots that will play in the match
     * @return array of initialized BotConnection objects without set connections
     */
    private static BotConnection[] prepareBotConnection(String[] botsAndTokens, int numberOfBots) {
        BotConnection[] bots = new BotConnection[numberOfBots];
        for (int i = 0; i < botsAndTokens.length; i += 2) {
            String botName = botsAndTokens[i];
            String token = botsAndTokens[i + 1];
            bots[i / 2] = new BotConnection(botName, token);
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

        long timeoutMillis = Math.round(this.generalConfig.connectingBotsTimeout * 1000);
        long checkPeriodMillis = 10L;

        while (!areAllBotsConnected()) {
            Thread.sleep(checkPeriodMillis);
            timeoutMillis -= checkPeriodMillis;
            if (timeoutMillis <= 0) {
                for (BotConnection bot : this.bots) {
                    if (bot.connection == null) {
                        System.err.printf("Bot with name '%s' did not connect in time\n", bot.botName);
                    }
                }
                throw new Error("Some bots failed to connect in time");
            }
        }

        System.out.println("All bots connected");
    }

    /**
     * @return true if all bots are connected to the bot server else false
     */
    private boolean areAllBotsConnected() {
        for (BotConnection bot : this.bots) {
            if (bot.connection == null) {
                return false;
            }
        }
        return true;
    }

    private void onOpen(WebSocket conn, ClientHandshake handshake) {
        String token = handshake.getFieldValue("token");

        for (BotConnection bot : this.bots) {
            if (bot.token.equals(token) && bot.connection == null) {
                bot.connection = conn;
                // Attach bot object to connection so that later we
                // will know to which bot a connection belongs
                bot.connection.setAttachment(bot);
                System.out.printf("Bot '%s' has connected\n", bot.botName);
                return;
            }
        }
    }

    /**
     * Send data to all bots
     * @param data - data as json string
     */
    public void sendToAll(String data) {
        for (int i = 0; i < this.bots.length; i++) {
            send(i, data);
        }
    }

    /**
     * Send data to a specific bot
     * @param botIndex - the index based on where it was provided to
     *                 the match-generator as a program argument
     * @param data - data as json string
     */
    public void send(int botIndex, String data) {
        BotConnection bot = this.bots[botIndex];
        if (bot.disqualified) {
            return;
        }
        if (bot.currentRequestIndex == this.currentRequestIndex) {
            System.err.printf("Can't send new message to bot '%s' " +
                    "because it already received its update this turn\n", bot.botName);
            return;
        }

        bot.currentRequestIndex = currentRequestIndex;
        bot.currentRequestTime = System.currentTimeMillis();
        bot.waitingResponse = true;
        bot.connection.send(data);
    }

    /**
     * @return - bot names of all connected bots in order they
     *         were provided as arguments to the program
     */
    public String[] getBotNames() {
        String[] botNames = new String[this.bots.length];
        for (int i = 0; i < this.bots.length; i++) {
            botNames[i] = this.bots[i].botName;
        }
        return botNames;
    }

    public int getNumberOfTimeouts(int botIndex) {
        return this.bots[botIndex].numberOfTimeouts;
    }

    public boolean isDisqualified(int botIndex) {
        return this.bots[botIndex].disqualified;
    }

    public float getDisqualificationTime(int botIndex) {
        return this.bots[botIndex].disqualificationTime;
    }

    public String getDisqualificationReason(int botIndex) {
        return this.bots[botIndex].disqualificationReason;
    }

    public String getLastResponseData(int botIndex) {
        return this.bots[botIndex].lastResponseData;
    }

    public boolean allBotsDisqualified() {
        for (BotConnection bot : this.bots) {
            if (!bot.disqualified) {
                return false;
            }
        }
        return true;
    }

    /**
     * Waits for all bots that received a request in this turn to respond or
     * timeout. Handles disqualifying bots if necessary.
     * @throws InterruptedException
     */
    public void waitForBotsToRespond() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        float timeout = (currentRequestIndex == 0)
                ? this.generalConfig.botFirstResponseTimeout
                : this.generalConfig.botResponseTimeout;
        long timeoutMillis = (long) (timeout * 1000);

        // Wait for all bots to respond or timeout
        while (System.currentTimeMillis() - startTime <= timeoutMillis) {
            if (allBotsResponded()) {
                break;
            }
            Thread.sleep(0, 0);
        }

        // Handle bots that timed out
        for (BotConnection bot : this.bots) {
            if (!bot.disqualified && bot.waitingResponse) {
                handleBotNotResponded(bot);
            }
        }

        this.currentRequestIndex++;
    }

    private void handleBotNotResponded(BotConnection bot) {
        bot.numberOfTimeouts++;
        System.out.printf("Bot '%s' failed to return response in time, number of timeouts: %d\n",
                bot.botName, bot.numberOfTimeouts);

        if (bot.numberOfTimeouts >= generalConfig.maxTimeoutsPerBot) {
            disqualifyBot(bot, "Bot timed out to many times");
        }
    }

    private void disqualifyBot(BotConnection bot, String reason) {
        bot.disqualificationTime = this.gameTimer.time;
        bot.disqualified = true;
        bot.lastResponseData = null;
        bot.disqualificationReason = reason;
    }

    /**
     * @return - if all bots that received a request in this turn have already responded
     */
    private boolean allBotsResponded() {
        for (BotConnection bot : this.bots) {
            if (bot.disqualified || bot.currentRequestIndex != this.currentRequestIndex) {
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
        System.out.printf("Connection for bot '%s' closed\n", bot.botName);
    }

    private void onMessage(WebSocket conn, String message) {
        BotConnection bot = conn.getAttachment();
        if (bot.disqualified) {
            return;
        }
        if (bot.currentRequestIndex != this.currentRequestIndex) {
            System.out.printf("Bot '%s' sent response too late\n", bot.botName);
            return;
        }
        if (!bot.waitingResponse) {
            System.out.printf("Bot '%s' already answered to this request\n", bot.botName);
            return;
        }

        bot.waitingResponse = false;
        bot.lastResponseData = message;
        bot.responseTotalDuration += System.currentTimeMillis() - bot.currentRequestTime;

        if (bot.responseTotalDuration >= this.generalConfig.botResponseTotalDurationMax) {
            disqualifyBot(bot, "Bot used all available time");
        }
    }

    private void onError(WebSocket conn, Exception ex) {
        BotConnection bot = conn.getAttachment();
        System.err.printf("Connection for bot '%s' threw an Exception\n", bot.botName);
        ex.printStackTrace();
    }

    /**
     * Stops the WebSocketServer
     * @throws IOException
     * @throws InterruptedException
     */
    public void stop() throws IOException, InterruptedException {
        this.server.stop();
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
        return server;
    }
}
