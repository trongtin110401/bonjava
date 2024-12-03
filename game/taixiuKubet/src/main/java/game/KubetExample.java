package game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class KubetExample {
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

    public static void main(String[] args) {
        // URL của API
        String url = "https://ga88.ku6696.net/Api/Common/GetSKAll";

        // Dữ liệu JSON cho request
        String requestBody = "{\"SKey\":\"1\",\"CKey\":\"71101\",\"DType\":1,\"CType\":1,\"IsS\":1,\"PlayTool\":4}";

        // Cookie header
        String cookie = "_videogameCulture=vi-VN; PHPSESSID=napn3rnr8vg5q4ecjhi6rop1m4";

        // Gọi hàm tiện ích
        String streamTimestampKey = fetchStreamTimestampKey(url, requestBody, cookie);

        // In kết quả
        System.out.println("StreamTimestampKey: " + streamTimestampKey);
    }
}
