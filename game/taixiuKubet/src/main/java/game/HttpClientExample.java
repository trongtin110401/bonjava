package game;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpClientExample {

    public static void main(String[] args) {
        String loginUrl = "https://login.79club.biz/acc/Login";
        String parameters = "AccountName=baotohn2024&md5=111111";
        String cookie = null;
        String connectionToken = null;

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
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
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
            return;
        }

        // Step 3: Extract ConnectionToken from the JSON response
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            connectionToken = jsonResponse.getString("ConnectionToken");
            System.out.println("Extracted ConnectionToken: " + connectionToken);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Step 4: Connect to WebSocket using the ConnectionToken with custom headers
        try {
            String wsUrl = "wss://dicelive.79club.biz/dicelive/signalr/connect?connectionToken=" + connectionToken;

            // Add custom headers
//            Map<String, String> headers = new HashMap<>();
//            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
//            headers.put("Cookie", cookie);
//            headers.put("Host", "dicelive.79club.biz");

            WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("WebSocket Connection Opened");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received Message: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket Connection Closed. Reason: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("WebSocket Error: ");
                    ex.printStackTrace();
                }
            };

            // Connect to WebSocket
            client.connect();

            // Wait for connection to be established
            while (true) {
                Thread.sleep(100);
            }
//
            // Optionally send a message (if needed)
//            client.send("Hello from WebSocket Client!");
//
//            // Keep connection alive to receive messages
//            Thread.sleep(30000);
//
//            // Close the connection
//            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
