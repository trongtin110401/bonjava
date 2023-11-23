package com.vinplay.api.processors.accnhatvip;

import com.google.gson.Gson;
import com.vinplay.common.HttpCommon;
import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GiaiCaptchaDoiPass {
//    OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder().proxy(this.getProxy())
//            .build();
    public static final String KeyAnyCaptcha = "361d88e2dcf44606a7a5cd0b4e030950";

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
//                    .url("http://proxy.tinsoftsv.com/api/getProxy.php?key=TLjnu1ysf2JU5mkAHuUsRkMQbE2EktG4qq8AnU")
//                    .method("GET", null)
//                    .build();
//            Response response = client.newCall(request).execute();
//            String content = response.body().string();
//            Gson gson = new Gson();
//            proxy pro = gson.fromJson(content,proxy.class);
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
//                        .url("http://proxy.tinsoftsv.com/api/changeProxy.php?key=TLjnu1ysf2JU5mkAHuUsRkMQbE2EktG4qq8AnU&location=1")
//                        .method("GET", null)
//                        .build();
//                Response response1 = client1.newCall(request1).execute();
//                String content1 = response1.body().string();
//                proxyx prox = gson.fromJson(content1,proxyx.class);
//                return prox.getProxy();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public static void main(String[] args) {
//        GiaiCaptchaDoiPass any = new GiaiCaptchaDoiPass();
//        CaptchaDoiPass captchaMoel = any.getCaptchaSunwin("5b6d0ac5557e4ef89b167dd844638d93");
//        any.Doipass("7bd58ef0148a401d8a1f0c3cf17eedad","12345678");
//
//    }

    public void Doipass(String token, String password){

        try {
            GiaiCaptchaDoiPass any = new GiaiCaptchaDoiPass();
            CaptchaDoiPass captchaMoel = any.getCaptchaSunwin(token);
            String base64 = captchaMoel.getCaptcha_base64();
            String captcha =any.captcharProcess(base64);
//            String pass_stv = "123123abc";
            String pass_stv = "123123ccc";
            String sessionId = captchaMoel.getSessionID();
            System.out.println(captcha+" - "+sessionId);

            boolean check_doi = false;
            String content = null;
            do {
                content = null;
                int randonIndex =new Random().nextInt(HttpCommon.listProxy().size());
                OkHttpClient client =  HttpCommon.listProxy().get(randonIndex);
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


    public CaptchaDoiPass getCaptchaSunwin(String token) {
        CaptchaDoiPass captchaModel = new CaptchaDoiPass();
        String captcha = "";
        String sessionID = "";
        try {

            boolean check = false;
            String content = null;
            int count = 10;
            do {
                content = null;
                count--;
                int randonIndex =new Random().nextInt(HttpCommon.listProxy().size());
                OkHttpClient client =  HttpCommon.listProxy().get(randonIndex);
                Request request = new Request.Builder()
                        .url("https://api.swinhh.io/id?command=getCaptcha")
                        .method("GET", null)
                        .addHeader("Host", "api.swinhh.io")
                        .addHeader("Sec-Ch-Ua", "\"(Not(A:Brand\";v=\"8\", \"Chromium\";v=\"100\"")
                        .addHeader("Authorization", token)
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
                if(content.contains("data")){
                    check = true;
                }else{
                    check = false;
                }

            } while (check == false && count > 0);

            if(check == true){
                String[] data1 = content.split("\"image\":\"");
                String data2 = data1[1].trim();
                String[] data3 = data2.split("\",\"sessionId\":\"");
                String data4 = data3[0].trim();
                String data5 = data3[1].trim();
                String[] data6 = data5.split("\"},");
                captcha = data4;
                sessionID = data6[0];
                captchaModel.setCaptcha_base64(captcha);
                captchaModel.setSessionID(sessionID);

            }

            return captchaModel;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return captchaModel;
    }


    public String captcharProcess(String base64)  {
        String tasid = createCaptCha(base64);
        for(int i =0 ; i < 60; i++) {
            try {
                Thread.sleep(300);
            }catch (Exception e) {
                e.printStackTrace();
            }
            String output = getResult(tasid);
            if(output != null && !output.isEmpty()) {
                return output;
            }
        }
        return "";
    }
    public String createCaptCha(String base64Image) {
        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n\t\"clientKey\": \""+KeyAnyCaptcha+"\",\r\n\t\"task\": {\r\n\t\t\"type\": \"ImageToTextTask\",\r\n\t\t\"body\": \""+base64Image+"\"\r\n\t}\r\n}");
            Request request = new Request.Builder()
                    .url("https://api.anycaptcha.com/createTask")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = HttpCommon.getInstance().httpClient.newCall(request).execute();
            String data= response.body().string();
            if(data.contains("\"errorId\":0")) {
                System.out.println("da lay duoc taskid");
                return bocTachRegex(data,"(taskId\":)(\\d+)(})",2);
            }return "";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getResult(String taskid) {
        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n\t\"clientKey\": \""+KeyAnyCaptcha+"\",\r\n\t\"taskId\": "+taskid+"\r\n}");
            Request request = new Request.Builder()
                    .url("https://api.anycaptcha.com/getTaskResult")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = HttpCommon.getInstance().httpClient.newCall(request).execute();
            String data = response.body().string();
            System.out.println("Kiem tra taskid: "+taskid+ data);
            if(data.contains("\"status\":\"ready\"")) {
                return bocTachRegex(data,"(text\":\")([a-zA-Z0-9]+)(\"})",2);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String bocTachRegex(String raw, String regex, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(raw);
        if (matcher.find()) {
            String output = matcher.group(group);
            System.out.println(output);
            return output;
        }
        return "";
    }
}
