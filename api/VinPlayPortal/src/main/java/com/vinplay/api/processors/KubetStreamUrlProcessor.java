package com.vinplay.api.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

public class KubetStreamUrlProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) throws SQLException {
        HttpServletRequest request = param.get();
        String game = request.getParameter("game");
        if (game.equals("taixiu")) {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet("https://newjson.thietkewebcobac.com/?ID=taixiua");
            try {
                HttpResponse httpResponse = httpClient.execute(httpget);
                return EntityUtils.toString(httpResponse.getEntity());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
//            return getDataXocDia();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet("https://newjson.thietkewebcobac.com/?ID=xocdiaa");
            // Thiết lập header Content-Type
            httpget.setHeader("Content-Type", "application/json");
            try {
                HttpResponse httpResponse = httpClient.execute(httpget);
                return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // URL của API
//        String url = "https://ga88.ku6696.net/Api/Common/GetSKAll";
//
//        // Dữ liệu JSON cho request
//        String requestBody = "{\"SKey\":\"1\",\"CKey\":\"71101\",\"DType\":1,\"CType\":1,\"IsS\":1,\"PlayTool\":4}";
//
//        // Cookie header
//        String cookie = "_videogameCulture=vi-VN; PHPSESSID=napn3rnr8vg5q4ecjhi6rop1m4";
//        return "dataJson = '{\"Data\":{\"channelAllTable\":[],\"channelLive\":[],\"channelSource\":[{\"key\":\"1\",\"name\":\"HAPVN01\",\"Protocol\":\"vigo\",\"url\":\"wss://gcphkmgtm.vnkuvideo.com:8882/\"},{\"key\":\"2\",\"name\":\"HAPVN02\",\"Protocol\":\"vigo\",\"url\":\"wss://awshkmgtm.vnkuvideo.com:8882/\"},{\"key\":\"3\",\"name\":\"HAPVN03\",\"Protocol\":\"vigo\",\"url\":\"wss://hwhkmgtm.vnkuvideo.com:8882/\"},{\"key\":\"4\",\"name\":\"HAPVN04\",\"Protocol\":\"vigo\",\"url\":\"wss://hollyvnmgtm.vnkuvideo.com:8882/\"},{\"key\":\"5\",\"name\":\"CDNVN05\",\"Protocol\":\"vigo\",\"url\":\"wss://mvnws.vnkuvideo.com:8882/\"},{\"key\":\"6\",\"name\":\"CDNVN06\",\"Protocol\":\"vigo\",\"url\":\"wss://inmvn.vnkuvideo.com:8882/\"},{\"key\":\"7\",\"name\":\"s1\",\"Protocol\":\"vigo\",\"url\":\"wss://inmvn.vnkuvideo.com:8882/\"},{\"key\":\"8\",\"name\":\"s2\",\"Protocol\":\"vigo\",\"url\":\"wss://mvnws.vnkuvideo.com:8882/\"},{\"key\":\"9\",\"name\":\"s3\",\"Protocol\":\"vigo\",\"url\":\"wss://hollyvnmgtm.vnkuvideo.com:8882/\"},{\"key\":\"10\",\"name\":\"s4\",\"Protocol\":\"vigo\",\"url\":\"wss://hwhkmgtm.vnkuvideo.com:8882/\"},{\"key\":\"11\",\"name\":\"s5\",\"Protocol\":\"vigo\",\"url\":\"wss://awshkmgtm.vnkuvideo.com:8882/\"},{\"key\":\"12\",\"name\":\"s6\",\"Protocol\":\"vigo\",\"url\":\"wss://gcphkmgtm.vnkuvideo.com:8882/\"},{\"key\":\"13\",\"name\":\"s7\",\"Protocol\":\"vigo\",\"url\":\"wss://ucvngtm.vnkuvideo.com:8882/\"},{\"key\":\"14\",\"name\":\"s8\",\"Protocol\":\"vigo\",\"url\":\"wss://vn08.vnkuvideo.com:8882/\"}],\"channelTable\":{\"closeup\":\"fxytb0142\",\"CreateDate\":\"0001-01-01T00:00:00\",\"d3\":\"close3d\",\"front\":\"fxytb0111\",\"key\":\"611011\",\"ModifyDate\":\"0001-01-01T00:00:00\",\"multi\":\"fxytb012\",\"name\":\"å\u0085\u0088æ\u0090\u0096éª°å¯¶A-1\",\"phone\":\"fxytb0122\",\"sd3\":\"close3d\",\"sfront\":\"fxytb0112\",\"Sort\":null},\"StreamTimestampKey\":\"" +  fetchStreamTimestampKey(url, requestBody, cookie) + "\"}}';";
    }


    public String getDataXocDia() {
        try {
            // URL
            URL url = new URL("https://newjson.thietkewebcobac.com/?ID=xocdiaa");

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Set headers
            connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            connection.setRequestProperty("accept-language", "en-US,en;q=0.9,vi;q=0.8");
            connection.setRequestProperty("cache-control", "max-age=0");
            connection.setRequestProperty("priority", "u=0, i");
            connection.setRequestProperty("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
            connection.setRequestProperty("sec-ch-ua-mobile", "?0");
            connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            connection.setRequestProperty("sec-fetch-dest", "document");
            connection.setRequestProperty("sec-fetch-mode", "navigate");
            connection.setRequestProperty("sec-fetch-site", "none");
            connection.setRequestProperty("sec-fetch-user", "?1");
            connection.setRequestProperty("upgrade-insecure-requests", "1");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Use try-with-resources for the InputStreamReader and BufferedReader
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Print response
                return response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String fetchStreamTimestampKey(String urlString, String requestBody, String cookie) {
        HttpURLConnection connection = null;
        try {
            // Tạo URL object
            URL url = new URL(urlString);

            // Mở kết nối
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Thiết lập headers
            connection.setRequestProperty("accept", "application/json, text/plain, */*");
            connection.setRequestProperty("accept-language", "en-US,en;q=0.9,vi;q=0.8");
            connection.setRequestProperty("content-type", "application/json;charset=UTF-8");
            connection.setRequestProperty("cookie", cookie);
            connection.setRequestProperty("origin", "https://ga88.ku6696.net");
            connection.setRequestProperty("referer", "https://ga88.ku6696.net/Home/Game1");
            connection.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"130\", \"Google Chrome\";v=\"130\", \"Not?A_Brand\";v=\"99\"");
            connection.setRequestProperty("sec-ch-ua-mobile", "?0");
            connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36");

            // Ghi dữ liệu vào body của request
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
                os.flush();
            }

            // Kiểm tra mã trạng thái HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Đọc response từ server
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }

                // Parse JSON và trích xuất StreamTimestampKey
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.toString());
                JsonNode streamTimestampKeyNode = rootNode.path("Data").path("StreamTimestampKey");
                if (!streamTimestampKeyNode.isMissingNode()) {
                    return streamTimestampKeyNode.asText();
                } else {
                    throw new RuntimeException("StreamTimestampKey not found in response!");
                }
            } else {
                throw new RuntimeException("HTTP request failed with status code: " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching StreamTimestampKey", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
