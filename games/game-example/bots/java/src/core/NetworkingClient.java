package core;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import core.api.Response;
import core.api.InitialData;
import core.api.MatchState;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


/**
 * Handles the connection to the game engine and takes
 * care of sending and retrieving data.
 **/
public class NetworkingClient extends WebSocketClient {

    private Gson gson = new Gson();
    private Bot myBot;

    public static NetworkingClient connectNew(String[] args, Bot myBot) throws Exception {
        Args parsedArgs = new Args();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(parsedArgs)
                .build();
        jCommander.parse(args);

        // If --help flag is provided, display help
        if (parsedArgs.help) {
            jCommander.setProgramName("my-bot.jar");
            jCommander.usage();
            return null;
        }

        // Setup headers
        Map<String,String> httpHeaders = new HashMap<>();
        httpHeaders.put("token", parsedArgs.token);

        NetworkingClient c = new NetworkingClient(new URI("ws://localhost:" + parsedArgs.port), httpHeaders, myBot);
        // Disable connection lost timeout
        c.setConnectionLostTimeout(0);

        c.connect();

        return c;
    }

    private NetworkingClient(URI serverUri, Map<String, String> httpHeaders, Bot myBot) {
        super(serverUri, httpHeaders);
        this.myBot = myBot;
    }

    @Override
    public void onMessage(ByteBuffer bytes) {}

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed, exiting...");
        System.exit(0);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        if (!isOpen()) {
            System.exit(1);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {}

    @Override
    public void onMessage(String message) {
        try {
            // Create response object to store commands issued by the bot
            Response response = new Response();

            if (message.contains("\"__type\":\"INITIAL\"")) {
                InitialData data = gson.fromJson(message, InitialData.class);
                response.__uid = data.__uid;
                myBot.setup(data);
            }
            else if (message.contains("\"__type\":\"UPDATE\"")) {
                MatchState data = gson.fromJson(message, MatchState.class);
                response.__uid = data.__uid;
                myBot.update(data, response);
            }
            send(gson.toJson(response));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}