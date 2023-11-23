package com.vinplay.api.processors.accsunwin;

import com.vinplay.common.HttpCommon;
import okhttp3.*;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CheckLoginSunwin {
    public boolean checklogin(String uname, String pwd) {
        try {
            String username = uname;
            String passwd = pwd;
            String key = "YICQFXSBCASEMZWKJBQH";
            String platforumid = "4";
            String deviceID = "RnaugI4pw7ByLrP2fkZx";

            String hash1 = username + passwd + platforumid + deviceID + key;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hash1.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
            System.out.println("hash: " + myHash);

            int randonIndex = new Random().nextInt(listProxy().size());
            OkHttpClient client = listProxy().get(randonIndex);
            MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
            RequestBody body = RequestBody.create(mediaType, "{\"command\":\"loginWebHash\",\"username\":\"" + username + "\",\"password\":\"" + passwd + "\",\"platformId\":" + platforumid + ",\"advId\":\"\",\"deviceId\":\"" + deviceID + "\",\"hash\":\"" + myHash + "\",\"brand\":\"sun.win\"}");
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
                    .addHeader("Origin", "https://web.sunvn.net")
                    .addHeader("Sec-Fetch-Site", "cross-site")
                    .addHeader("Sec-Fetch-Mode", "cors")
                    .addHeader("Sec-Fetch-Dest", "empty")
                    .addHeader("Referer", "https://web.sunvn.net/")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "en-US,en;q=0.9")
                    .build();

            Response response = client.newCall(request).execute();
            String content = response.body().string();
            System.out.println("Response: " + content);
            if (content.contains("\"status\":0")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<OkHttpClient> listProxy() {
        Authenticator proxyAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic("shady365", "You123@");
                return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
            }
        };
        OkHttpClient client1 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("jp.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client2 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("au.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client3 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("uk.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client4 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("nl.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client5 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("sg.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client6 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("us-il.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client7 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("us.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client8 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("us-dc.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client9 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("us-ca.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();

        ArrayList<OkHttpClient> list = new ArrayList<>();
        list.add(client1);
        list.add(client2);
        list.add(client3);
        list.add(client4);
        list.add(client5);
        list.add(client6);
        list.add(client7);
        list.add(client8);
        list.add(client9);
        return list;
    }

//    public static void main(String[] args) {
//        CheckLoginSunwin ch = new CheckLoginSunwin();
//        account acc = new account("sieunhando","123456","12345","12345",true,"111");
//        boolean check = ch.checklogin(acc);
//        System.out.println("check "+check);
//    }

}
