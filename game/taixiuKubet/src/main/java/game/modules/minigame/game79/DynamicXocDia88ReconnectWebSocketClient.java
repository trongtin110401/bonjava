package game.modules.minigame.game79;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import game.modules.minigame.TaiXiuModule;
import game.modules.minigame.TxKubetState;
import org.apache.commons.collections.CollectionUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class DynamicXocDia88ReconnectWebSocketClient extends WebSocketClient {

    private static final int RECONNECT_INTERVAL = 5000; // 5 seconds
    private static final String BASE_URI = "wss://taixiukulive.system32-cloudfare-356783752985678522.monster/signalr/connect?transport=webSockets&connectionToken=L8E%2BYcS6McCNrPXjBYz0G58TJkzjOobXbIjshiwTd106%2FIohI0rBYsV9MDgZg2pwrtbzd2H%2FAaeoVA2szXkAOPrRofpnERT3wYBQ5ojzOwHMq5taq%2FrUE4%2F55oibAB1H&connectionData=%5B%7B%22name%22%3A%22taixiukuahub%22%7D%5D&tid=6&access_token=";

    public static WebSocketClient currentClient; // Track the active WebSocket client
    private boolean reconnecting = false;
    private boolean isManuallyClosing = false; // Flag to prevent recursive onClose calls
    private TaiXiuModule taiXiuModule;

    final ObjectMapper objectMapper = new ObjectMapper();


    public DynamicXocDia88ReconnectWebSocketClient(TaiXiuModule taiXiuModule, URI serverUri) {
        super(serverUri);
        this.taiXiuModule = taiXiuModule;

        SimpleModule module = new SimpleModule();
        module.addDeserializer(MainModel.class, new XocDia88MainModelDeserializer());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
            if (message.equals("{}")) {
                return;
            }
//            System.out.println(message);
            MainModel model = objectMapper.readValue(message, MainModel.class);
            if(CollectionUtils.isNotEmpty(model.getM())) {
                switch (model.getM().get(0).getMethod()) {
                    case "LivestreamsessionInfo":
                        SessionInfo sessionInfo = (SessionInfo) model.getM().get(0).getArguments().get(0);
                        TxKubetState kubetState = TxKubetState.getByStep(sessionInfo.getCurrentState());
                        int countDownTime = sessionInfo.getEllapsed();
                        int dice1 = sessionInfo.getResult().getDice1();
                        int dice2 = sessionInfo.getResult().getDice2();
                        int dice3 = sessionInfo.getResult().getDice3();
                        if (taiXiuModule != null) {
                            taiXiuModule.handleGameState(sessionInfo.getGid(), kubetState, countDownTime, dice1, dice2, dice3);
                        }
                        break;
                    case "gameHistory":
                        break;
                    case "changeDealer":
                        break;
                }
            }
        } catch (Exception e) {
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
                    currentClient = new DynamicXocDia88ReconnectWebSocketClient(taiXiuModule, newUri); // Create a new client
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
        String loginUrl = "https://verx3-575ry57fdxd4yrfddg.system32-cloudfare-356783752985678522.monster/api/Account/Login";
        String parameters = "{\"LoginType\":1,\"UserName\":\"baotohn2024\",\"Password\":\"11111111a\",\"DeviceId\":\"af045fc2-3571-45a0-8bfd-0eae40b725ab\",\"DeviceType\":1}";
        String cookie;
        String connectionToken;

        try {
            // Step 1: Login and retrieve the cookie
            URL url = new URL(loginUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("accept-language", "vi-VN,vi;q=0.9");
            connection.setRequestProperty("cache-control", "no-cache");
            connection.setRequestProperty("content-type", "application/json; charset=UTF-8");
            connection.setRequestProperty("cookie", "cf_clearance=LIfEia49OcCcu8NblJh4KoYdS74jcrjTP8QR4GfU5_8-1732638554-1.2.1.1-hHD...");
            connection.setRequestProperty("origin", "https://play.xocdia88.skin");
            connection.setRequestProperty("pragma", "no-cache");
            connection.setRequestProperty("referer", "https://play.xocdia88.skin/");
            connection.setRequestProperty("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
            connection.setRequestProperty("sec-ch-ua-mobile", "?0");
            connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            connection.setRequestProperty("sec-fetch-dest", "empty");
            connection.setRequestProperty("sec-fetch-mode", "cors");
            connection.setRequestProperty("sec-fetch-site", "cross-site");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");

            // Cho phép gửi dữ liệu
            connection.setDoOutput(true);

            // Send login parameters
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = parameters.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code and body
            int responseCode = connection.getResponseCode();
            System.out.println("Login Response Code: " + responseCode);

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }
            System.out.println(responseCode);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.toString());
            connectionToken = jsonNode.path("Token").asText();
            System.out.println(connectionToken);

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
            currentClient = new DynamicXocDia88ReconnectWebSocketClient(null, initialUri);
            currentClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
