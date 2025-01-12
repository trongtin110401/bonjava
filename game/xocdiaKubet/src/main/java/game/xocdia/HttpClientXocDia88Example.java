package game.xocdia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class HttpClientXocDia88Example {

    public static void main(String[] args) {
        String loginUrl = "https://verx3-575ry57fdxd4yrfddg.system32-cloudfare-356783752985678522.monster/api/Account/Login";
        String parameters = "{\"LoginType\":1,\"UserName\":\"huhu122024\",\"Password\":\"11111111a\",\"DeviceId\":\"af045fc2-3571-45a0-8bfd-0eae40b725ab\",\"DeviceType\":1}";
        String cookie = null;
        String connectionToken = null;

        try {
            // Step 1: Login and retrieve the cookie
            URL url = new URL(loginUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("accept-language", "vi-VN,vi;q=0.9");
            conn.setRequestProperty("cache-control", "no-cache");
            conn.setRequestProperty("content-type", "application/json; charset=UTF-8");
            conn.setRequestProperty("cookie", "cf_clearance=LIfEia49OcCcu8NblJh4KoYdS74jcrjTP8QR4GfU5_8-1732638554-1.2.1.1-hHD...");
            conn.setRequestProperty("origin", "https://play.xocdia88.skin");
            conn.setRequestProperty("pragma", "no-cache");
            conn.setRequestProperty("referer", "https://play.xocdia88.skin/");
            conn.setRequestProperty("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
            conn.setRequestProperty("sec-ch-ua-mobile", "?0");
            conn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            conn.setRequestProperty("sec-fetch-dest", "empty");
            conn.setRequestProperty("sec-fetch-mode", "cors");
            conn.setRequestProperty("sec-fetch-site", "cross-site");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");

            // Cho phép gửi dữ liệu
            conn.setDoOutput(true);

            // Send login parameters
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = parameters.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code and body
            int responseCode = conn.getResponseCode();
            System.out.println("Login Response Code: " + responseCode);

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
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
            return;
        }


        // Step 4: Connect to WebSocket using the ConnectionToken with custom headers
        try {
            String wsUrl = "wss://xocdialive.system32-cloudfare-356783752985678522.monster/signalr/connect?transport=webSockets&connectionToken=XX%2FqfLBsb9nZWijN3PMTytqUWucJApkqoQFX8IVfSatvECBlLhU8HFuuHfR85lnYOiRW9OsnlvECsIYfsBDQhFAwANgHc7eZSraELAb9Pb7Gdbih21VLCZgEqWSVJ0b6&connectionData=%5B%7B%22name%22%3A%22sedieLiveHub%22%7D%5D&tid=6&access_token=" + connectionToken;

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
                    if (!message.contains("\"M\":\"playerBet\"")
                            && !message.contains("\"M\":\"summaryPlayer\"")
                            && !message.contains("\"M\":\"totalWinMoney\"")
                            && !message.contains("\"M\":[]")
                            && !message.contains("\"M\":\"gameHistory\"")
                            && !message.equals("{}"))
                        System.out.println(message);
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
