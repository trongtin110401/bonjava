package com.vinplay.api.processors.accv28;

import com.vinplay.common.HttpCommon;
import okhttp3.*;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.Random;

public class CheckLoginV28 {
    public boolean checklogin(String uname, String pwd){
        try {
            String username = uname;
            String passwd = pwd;
            String key = "YICQFXSBCASEMZWKJBQH";
            String platforumid = "4";
            String deviceID = "RnaugI4pw7ByLrP2fkZx";

            String hash1 = username+passwd+platforumid+deviceID+key;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hash1.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
            System.out.println("hash: "+myHash);

            int randonIndex = new Random().nextInt(HttpCommon.listProxy().size());
            OkHttpClient client = HttpCommon.listProxy().get(randonIndex);
            MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
            RequestBody body = RequestBody.create(mediaType, "{\"command\":\"loginWebHash\",\"username\":\""+username+"\",\"password\":\""+passwd+"\",\"platformId\":"+platforumid+",\"advId\":\"\",\"deviceId\":\""+deviceID+"\",\"hash\":\""+myHash+"\",\"brand\":\"v8.club\"}");
            Request request = new Request.Builder()
                    .url("https://api.swinhh.io/id")
                    .method("POST", body)
                    .addHeader("Host", "api.swinhh.io")
                    .addHeader("Content-Length", "190")
                    .addHeader("Sec-Ch-Ua", "\"Chromium\";v=\"97\", \" Not;A Brand\";v=\"99\"")
                    .addHeader("Authorization", "3c24dfe54edb418ebd54c0f01f8c758f")
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .addHeader("Sec-Ch-Ua-Mobile", "?0")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36")
                    .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
                    .addHeader("Accept", "*/*")
                    .addHeader("Origin", "https://web.taiv8.fun")
                    .addHeader("Sec-Fetch-Site", "cross-site")
                    .addHeader("Sec-Fetch-Mode", "cors")
                    .addHeader("Sec-Fetch-Dest", "empty")
                    .addHeader("Referer", "https://web.taiv8.fun/")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "en-US,en;q=0.9")
                    .build();

            Response response = client.newCall(request).execute();
            String content = response.body().string();
            System.out.println("Response: "+content);
            if(content.contains("\"status\":0")){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
