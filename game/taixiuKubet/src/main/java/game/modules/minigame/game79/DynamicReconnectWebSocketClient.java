package game.modules.minigame.game79;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import game.modules.minigame.TaiXiuModule;
import game.modules.minigame.TxKubetState;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class DynamicReconnectWebSocketClient extends WebSocketClient {

    private static final int RECONNECT_INTERVAL = 5000; // 5 seconds
    private static final String BASE_URI = "wss://dicelive.79club.biz/dicelive/signalr/connect?connectionToken=";

    public static WebSocketClient currentClient; // Track the active WebSocket client
    private boolean reconnecting = false;
    private boolean isManuallyClosing = false; // Flag to prevent recursive onClose calls
    private TaiXiuModule taiXiuModule;

    final ObjectMapper objectMapper = new ObjectMapper();


    public DynamicReconnectWebSocketClient(TaiXiuModule taiXiuModule, URI serverUri) {
        super(serverUri);
        this.taiXiuModule = taiXiuModule;

        SimpleModule module = new SimpleModule();
        module.addDeserializer(MainModel.class, new MainModelDeserializer());
        objectMapper.registerModule(module);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to server");
        reconnecting = false; // Reset the reconnect flag
    }

    @Override
    public void onMessage(String message) {
        try {
            MainModel model = objectMapper.readValue(message, MainModel.class);
            switch (model.getM().get(0).getMethod()) {
                case "sessionInfo":
                    SessionInfo sessionInfo = (SessionInfo) model.getM().get(0).getArguments().get(0);
                    TxKubetState kubetState = TxKubetState.getByStep(sessionInfo.getCurrentState());
                    int countDownTime = sessionInfo.getEllapsed();
                    int dice1 = sessionInfo.getResult().getDice1();
                    int dice2 = sessionInfo.getResult().getDice2();
                    int dice3 = sessionInfo.getResult().getDice3();
                    taiXiuModule.handleGameState(sessionInfo.getGid(), kubetState, countDownTime, dice1, dice2, dice3);
                    break;
                case "gameHistory":
                    break;
                case "changeDealer":
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Code: " + code + ", Reason: " + reason);

        // Prevent recursion if the close is manual
        if (!isManuallyClosing) {
            attemptReconnect(); // Trigger reconnect only for non-manual closures
        }
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("An error occurred: " + ex.getMessage());
        if (!isOpen()) {
            attemptReconnect();
        }
    }

    private void attemptReconnect() {
        if (reconnecting) return; // Prevent multiple reconnection attempts
        reconnecting = true;
        System.out.println("Attempting to reconnect in " + (RECONNECT_INTERVAL / 1000) + " seconds...");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    reconnecting = false;

                    // Safely close the old connection
                    closeConnectionSafely();

                    // Create a new client with the updated URI
                    URI newUri = buildDynamicURI(); // Generate a new URI with a fresh token
                    currentClient = new DynamicReconnectWebSocketClient(taiXiuModule, newUri); // Create a new client
                    currentClient.connect(); // Attempt to reconnect
                } catch (Exception e) {
                    System.err.println("Reconnect failed: " + e.getMessage());
                    attemptReconnect(); // Retry if reconnect fails
                }
            }
        }, RECONNECT_INTERVAL);
    }

    private void closeConnectionSafely() {
        if (currentClient != null) {
            isManuallyClosing = true; // Mark the closure as manual
            if (!currentClient.isClosed()) {
                System.out.println("Closing old WebSocket connection...");
                currentClient.close(); // Close the old client
            }
            isManuallyClosing = false; // Reset the flag after closing
        }
    }

    public static URI buildDynamicURI() {
        String loginUrl = "https://login.79club.biz/acc/Login";
        String parameters = "AccountName=baotohn2024&md5=111111";
        String cookie;
        String connectionToken;

        try {
            // Step 1: Login and retrieve the cookie
            URL url = new URL(loginUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

            // Send login parameters
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = parameters.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code and body
            int responseCode = connection.getResponseCode();
            System.out.println("Login Response Code: " + responseCode);

            // Check and retrieve cookies from the login response
            cookie = connection.getHeaderField("Set-Cookie");
            if (cookie != null) {
                System.out.println("Retrieved Cookie: " + cookie);
            } else {
                System.out.println("No cookies found in login response.");
                throw new RuntimeException("Mat me no cookie");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Step 2: Use the retrieved cookie to execute the GET request
        String responseBody = null;
        try {
            String getUrl = "https://dicelive.79club.biz/dicelive/signalr/negotiate?cp=R&cl=R&pf=web";
            URL url = new URL(getUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
            connection.setRequestProperty("Cookie", cookie);

            // Get the response code and body
            int responseCode = connection.getResponseCode();
            System.out.println("GET Request Response Code: " + responseCode);

            // Read response body
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }
            responseBody = response.toString();
            System.out.println("GET Response Body: " + responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Step 3: Extract ConnectionToken from the JSON response
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            connectionToken = jsonResponse.getString("ConnectionToken");
            System.out.println("Extracted ConnectionToken: " + connectionToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String fullUri = BASE_URI + connectionToken;
        System.out.println("Generated new URI: " + fullUri);
        return URI.create(fullUri);
    }

    public static void main(String[] args) {
        try {
            URI initialUri = buildDynamicURI(); // Replace with real initial token
            currentClient = new DynamicReconnectWebSocketClient(null, initialUri);
            currentClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
