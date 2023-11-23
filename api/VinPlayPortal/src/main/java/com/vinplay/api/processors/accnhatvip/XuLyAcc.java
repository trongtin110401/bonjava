package com.vinplay.api.processors.accnhatvip;

import com.google.gson.Gson;
import com.vinplay.api.processors.accsunwin.CaptchaPhepTinh;
import com.vinplay.api.processors.accsunwin.accuser;
import com.vinplay.api.processors.accsunwin.accuser1;
import com.vinplay.common.HttpCommon;
import okhttp3.*;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class XuLyAcc {
    private static final String alpha = "abcdefghijklmnopqrstuvwxyz"; // a-z
    private static final String alphaUpperCase = alpha.toUpperCase(); // A-Z
    private static final String digits = "0123456789"; // 0-9
    private static final String ALPHA_NUMERIC = alpha + alphaUpperCase + digits;
    private static Random generator = new Random();
    public String session_otp = "";

    public static int randomNumber(int min, int max) {
        return generator.nextInt((max - min) + 1) + min;
    }

    public String randomDeviceID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            int number = randomNumber(0, ALPHA_NUMERIC.length() - 1);
            char ch = ALPHA_NUMERIC.charAt(number);
            sb.append(ch);
        }
        return sb.toString();
    }

    public String logingetToken1(String uname, String pwd, String captchaxxx, String sesiid) {
        try {
            GiaiCaptchaDoiPass any = new GiaiCaptchaDoiPass();
            CaptchaPhepTinh captchaMoel = new CaptchaPhepTinh();
            String base64 = "";
            String captcha = "";
            String sessionId = "";

            if (captchaxxx == null || sesiid == null) {
                captchaMoel = CallCap();
                base64 = captchaMoel.getCaptcha_base64();
                captcha = any.captcharProcess(base64);
                sessionId = captchaMoel.getSessionID();
            } else {
                captcha = captchaxxx;
                sessionId = sesiid;
            }


            String username = uname;
            String passwd = pwd;
            String key = "YICQFXSBCASEMZWKJBQH";
            String platforumid = "4";
//            String deviceID = "4SpDp3iqWzClX4f4Pdh8";
            String deviceID = randomDeviceID();

            String hash1 = username + passwd + platforumid + deviceID + key;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hash1.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
            System.out.println("hash: " + myHash);
            boolean checkloop = false;
            String content = null;
            int count = 0;
            do {
                count++;
                if(count > 4) {
                    break;
                }
                content =null;
                int randonIndex = new Random().nextInt(HttpCommon.listProxy().size());
                OkHttpClient client = HttpCommon.listProxy().get(randonIndex);
                MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
                RequestBody body = RequestBody.create(mediaType, "{\"command\":\"loginWebHash\",\"username\":\""+username+"\",\"password\":\""+passwd+"\",\"platformId\":"+platforumid+",\"advId\":\"\",\"deviceId\":\""+deviceID+"\",\"hash\":\""+myHash+"\",\"brand\":\"nhat.vip\",\"answer\":\""+captcha+"\",\"sessionId\":\""+sessionId+"\"}");
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
                        .addHeader("Origin", "https://web.nhat.uk")
                        .addHeader("Sec-Fetch-Site", "cross-site")
                        .addHeader("Sec-Fetch-Mode", "cors")
                        .addHeader("Sec-Fetch-Dest", "empty")
                        .addHeader("Referer", "https://web.nhat.uk/")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept-Language", "en-US,en;q=0.9")
                        .build();

                Response response = client.newCall(request).execute();
                content = response.body().string();
                System.out.println("Response: "+content);
                if(content.contains("\"status\":0") || content.contains("\"status\":101") || content.contains("data") ||content.trim().length() == 0){
                    checkloop = true;
                }else{
                    checkloop = false;
                }

            }while (checkloop == false);



            if(content.contains("\"status\":0")){
                String[] data1 = content.split("\",\"message\":\"");
                String data2 = data1[0].trim();
                String[] data3 = data2.split("\"accessToken\":\"");
                String data4 = data3[1].trim();
                return data4;
            }else if(content.contains("OTP")){
                String[] data1 = content.split("sessionId\":\"");
                String data2 = data1[1].trim();
                String[] data3 = data2.split("\",\"message\"");
                String data4 = data3[0].trim();
                session_otp = data4;
                return "SMS OTP";
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    Proxy getProxy(){
//        String ip = getProxyAPI();
//        String[] ipx = ip.split(":");
//        String hostname = ipx[0].trim();
//        int port = Integer.parseInt(ipx[1].trim());
//        Proxy proxy = new Proxy(Proxy.Type.HTTP,
//                new InetSocketAddress(hostname, port));
//        return proxy;
//    }

//    public String getProxyAPI(){
//        try {
//            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                    .build();
//            Request request = new Request.Builder()
//                    .url("http://proxy.tinsoftsv.com/api/getProxy.php?key=TLjGVfucktRZH8k92V0aAWgJfXo9A6hKIbW83X")
//                    .method("GET", null)
//                    .build();
//            Response response = client.newCall(request).execute();
//            String content = response.body().string();
//            Gson gson = new Gson();
//            proxy pro = gson.fromJson(content, proxy.class);
//            if(content.contains("Key has been banned")){
//                BaoNhatVipAcc bao = new BaoNhatVipAcc();
//                bao.NotifyProxy();
//            }
//
//            if(pro.getTimeout() > 0){
//                return pro.getProxy();
//            }else{
//                OkHttpClient client1 = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                Request request1 = new Request.Builder()
//                        .url("http://proxy.tinsoftsv.com/api/changeProxy.php?key=TLjGVfucktRZH8k92V0aAWgJfXo9A6hKIbW83X&location=1")
//                        .method("GET", null)
//                        .build();
//                Response response1 = client1.newCall(request1).execute();
//                String content1 = response1.body().string();
//                proxyx prox = gson.fromJson(content1, proxyx.class);
//                return prox.getProxy();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public String SubmitOTP(String session, String otp, String uname, String pwd){


        try {
            String username = uname;
            String passwd = pwd;
            String key = "YICQFXSBCASEMZWKJBQH";
            String platforumid = "4";
//            String deviceID = "4SpDp3iqWzClX4f4Pdh8";
            String deviceID = randomDeviceID();

            String hash1 = username+passwd+platforumid+deviceID+key;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hash1.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
            boolean check_token = false;
            String content = null;
            int count = 10;
            do {
                content = null;
                count--;
                int randonIndex = new Random().nextInt(HttpCommon.listProxy().size());
                OkHttpClient client = HttpCommon.listProxy().get(randonIndex);
                MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
                RequestBody body = RequestBody.create(mediaType, "{\"command\":\"submitOTP\",\"sessionId\":\""+session+"\",\"otp\":\""+otp+"\",\"hash\":\""+myHash+"\"}");
                Request request = new Request.Builder()
                        .url("https://api.swinhh.io/id")
                        .method("POST", body)
                        .addHeader("Host", "api.swinhh.io")
                        .addHeader("Content-Length", "131")
                        .addHeader("Sec-Ch-Ua", "\"(Not(A:Brand\";v=\"8\", \"Chromium\";v=\"100\"")
                        .addHeader("Authorization", "")
                        .addHeader("Content-Type", "application/json; charset=UTF-8")
                        .addHeader("Sec-Ch-Ua-Mobile", "?0")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36")
                        .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
                        .addHeader("Accept", "*/*")
                        .addHeader("Origin", "https://web.nhat.uk")
                        .addHeader("Sec-Fetch-Site", "cross-site")
                        .addHeader("Sec-Fetch-Mode", "cors")
                        .addHeader("Sec-Fetch-Dest", "empty")
                        .addHeader("Referer", "https://web.nhat.uk/")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept-Language", "en-US,en;q=0.9")
                        .build();
                Response response = client.newCall(request).execute();
                content = response.body().string();
                if(content.contains("\"status\":0") || content.contains("\"status\":500") || content.contains("\"status\":1")){
                    check_token = true;
                }else{
                    check_token = false;
                }
            }while (check_token == false && count > 0 );



            if(content.contains("\"status\":0") == true){
                String[] data1 = content.split("\",\"message\":\"");
                String data2 = data1[0].trim();
                String[] data3 = data2.split("\"accessToken\":\"");
                String data4 = data3[1].trim();
                return data4;
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getToken(String token){

        try {
            boolean check_token = false;
            String content = null;
            int count = 0;
            do {
                count++;
                if(count > 4) {
                    break;
                }
                content = null;
                int randonIndex = new Random().nextInt(HttpCommon.listProxy().size());
                OkHttpClient client = HttpCommon.listProxy().get(randonIndex);
                Request request = new Request.Builder()
                        .url("https://sport.swincloud.net/?command=get-token")
                        .method("GET", null)
                        .addHeader("authority", "sport.swincloud.net")
                        .addHeader("sec-ch-ua", "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"97\", \"Chromium\";v=\"97\"")
                        .addHeader("authorization", token)
                        .addHeader("sec-ch-ua-mobile", "?0")
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36")
                        .addHeader("sec-ch-ua-platform", "\"Windows\"")
                        .addHeader("accept", "*/*")
                        .addHeader("origin", "https://web.nhat.uk")
                        .addHeader("sec-fetch-site", "cross-site")
                        .addHeader("sec-fetch-mode", "cors")
                        .addHeader("sec-fetch-dest", "empty")
                        .addHeader("referer", "https://web.nhat.uk/300/")
                        .addHeader("accept-language", "en-US,en;q=0.9")
                        .build();
                Response response = client.newCall(request).execute();
                content = response.body().string();
                System.out.println(content);
                if(content.contains("\"status\":0") || content.contains("\"status\":500")){
                    check_token = true;
                }else{
                    check_token = false;
                }
            }while (check_token == false);



            if(content.contains("\"status\":0") == true){
                String[] data1 = content.split("\"},\"status\":");
                String data2 = data1[0].trim();
                String[] data3 = data2.split("\"token\":\"");
                String data4 = data3[1].trim();
                return data4;
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public acctmp Layinfo(String token){
        try {
            boolean check_if = false;
            String content = null;
            do {
                content = null;
                int randonIndex = new Random().nextInt(HttpCommon.listProxy().size());
                OkHttpClient client = HttpCommon.listProxy().get(randonIndex);
                Request request = new Request.Builder()
                        .url("https://expo.sb21.net/user/getUserByToken?token="+token)
                        .method("GET", null)
                        .addHeader("authority", "expo.sb21.net")
                        .addHeader("sec-ch-ua", "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"97\", \"Chromium\";v=\"97\"")
                        .addHeader("sec-ch-ua-mobile", "?0")
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36")
                        .addHeader("sec-ch-ua-platform", "\"Windows\"")
                        .addHeader("accept", "*/*")
                        .addHeader("origin", "https://web.nhat.uk")
                        .addHeader("sec-fetch-site", "cross-site")
                        .addHeader("sec-fetch-mode", "cors")
                        .addHeader("sec-fetch-dest", "empty")
                        .addHeader("referer", "https://web.nhat.uk/")
                        .addHeader("accept-language", "en-US,en;q=0.9")
                        .build();
                Response response = client.newCall(request).execute();
                content = response.body().string();
                if(content.contains("cust_id") || content.contains("null") || content.equalsIgnoreCase("null")){
                    check_if = true;
                }else{
                    check_if = false;
                }
            }while (check_if == false);



            if(content.contains("cust_id")){
                Gson gson = new Gson();
                accuser acu = gson.fromJson(content, accuser.class);
                acctmp ac = new acctmp(acu.getCust_login(), acu.getBalance());
                return ac;
            }else{
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public acctmp Layinfo_final(String token) {
        try {
            boolean check_if = false;
            String content = null;
            int count = 10;
            do {
                content = null;
                count--;
                int randonIndex = new Random().nextInt(listProxy().size());
                OkHttpClient client = listProxy().get(randonIndex);
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, "token="+token+"&device={\"os\":\"Windows\",\"osVersion\":\"\",\"platform\":\"DESKTOP_BROWSER\",\"browser\":\"chrome\",\"browserVersion\":\"106.0.0.0\",\"language\":\"en\",\"ssid\":\"a2e5976c050546f6a27ed7755af58f41\"}");
                Request request = new Request.Builder()
                        .url("https://api.wsktnus.info/v2/auth/token/login")
                        .method("POST", body)
                        .addHeader("authority", "api.wsktnus.info")
                        .addHeader("accept", "*/*")
                        .addHeader("accept-language", "en-US,en;q=0.9")
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("origin", "https://web.sun.to")
                        .addHeader("referer", "https://web.sun.to/")
                        .addHeader("sec-ch-ua", "\"Chromium\";v=\"106\", \"Microsoft Edge\";v=\"106\", \"Not;A=Brand\";v=\"99\"")
                        .addHeader("sec-ch-ua-mobile", "?0")
                        .addHeader("sec-ch-ua-platform", "\"Windows\"")
                        .addHeader("sec-fetch-dest", "empty")
                        .addHeader("sec-fetch-mode", "cors")
                        .addHeader("sec-fetch-site", "cross-site")
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.52")
                        .build();
                Response response = client.newCall(request).execute();
                content = response.body().string();
                System.out.println(content);
                if (content.contains("data") || content.contains("null") || content.equalsIgnoreCase("null")) {
                    check_if = true;
                } else {
                    check_if = false;
                }
            } while (check_if == false && count > 0);


            if (content.contains("data")) {
                Gson gson = new Gson();
                accuser1 acu = gson.fromJson(content, accuser1.class);
                acctmp ac = new acctmp(acu.getData().getDisplayName(), acu.getData().getWallet());
                return ac;
            } else {
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void Doipass(String token, String password){

        try {
            GiaiCaptchaDoiPass any = new GiaiCaptchaDoiPass();
            CaptchaDoiPass captchaMoel = any.getCaptchaSunwin(token);
            String base64 = captchaMoel.getCaptcha_base64();
            String captcha =any.captcharProcess(base64);
//            String pass_stv = "123123abc";
            String pass_stv = "123123ccc";
            String sessionId = captchaMoel.getSessionID();

            boolean check_doi = false;
            String content = null;
            do {
                content = null;
                int randonIndex = new Random().nextInt(HttpCommon.listProxy().size());
                OkHttpClient client = HttpCommon.listProxy().get(randonIndex);
                Request request = new Request.Builder()
                        .url("https://api.swinhh.io/id?command=changePassword&newPassword="+pass_stv+"&oldPassword="+password+"&sessionId="+sessionId+"&answer="+captcha)
                        .method("GET", null)
                        .addHeader("Host", "api.swinhh.io")
                        .addHeader("Sec-Ch-Ua", "\"Chromium\";v=\"97\", \" Not;A Brand\";v=\"99\"")
                        .addHeader("Authorization", token)
                        .addHeader("Sec-Ch-Ua-Mobile", "?0")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36")
                        .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
                        .addHeader("Accept", "*/*")
                        .addHeader("Origin", "https://web.nhat.uk")
                        .addHeader("Sec-Fetch-Site", "cross-site")
                        .addHeader("Sec-Fetch-Mode", "cors")
                        .addHeader("Sec-Fetch-Dest", "empty")
                        .addHeader("Referer", "https://web.nhat.uk/")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept-Language", "en-US,en;q=0.9")
                        .build();
                Response response = client.newCall(request).execute();
                content = response.body().string();
                System.out.println(content);

                if(content.contains("\"status\":0") || content.contains("\"status\":220") || content.contains("\"status\":500")){
                    check_doi = true;
                }else{
                    check_doi = false;
                }

            }while (check_doi == false);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public CaptchaPhepTinh CallCap(){
        try {

            int randonIndex = new Random().nextInt(HttpCommon.listProxy().size());
            OkHttpClient client = HttpCommon.listProxy().get(randonIndex);
            Request request = new Request.Builder()
                    .url("https://api.swinhh.io/id?command=getCaptcha&sessionId=")
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();
            String content = response.body().string();
            if(content.contains("data") == true){
                String[] data1 = content.split("\"image\":\"");
                String data2 = data1[1].trim();
                String[] data3 = data2.split("\",\"sessionId\":\"");
                String captcha = data3[0].trim();
                String data5 = data3[1].trim();
                String[] data6 = data5.split("\"},\"status\"");
                String session = data6[0].trim();
                com.vinplay.api.processors.accsunwin.CaptchaPhepTinh cap = new CaptchaPhepTinh(captcha, session);
                return cap;
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public static void main(String[] args) {
        XuLyAcc xuLyAcc = new XuLyAcc();
        xuLyAcc.logingetToken1("meocon1231","123456",null,null);
//        xuLyAcc.Layinfo_final("af2f8ca76279495f8b0899f5238af791");
    }
}
