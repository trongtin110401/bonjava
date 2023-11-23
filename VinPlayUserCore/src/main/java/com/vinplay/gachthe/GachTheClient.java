/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinplay.gachthe;

import com.vinplay.common.HttpCommon;
import com.vinplay.epay.MD5;
import com.vinplay.lucky79.TheCaoResponse;
import com.vinplay.maxpay.ChargeMaxpayResponse;
import com.vinplay.maxpay.MaxpayException;
import com.vinplay.maxpay.ReCheckMaxpayResponse;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author HA
 */
public class GachTheClient {    

    public GachTheClient() {
       
    }

    public void installMyPolicy() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        SSLContext sc = SSLContext.getInstance("SSL");
        X509HostnameVerifier verifier = new X509HostnameVerifier(){

            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }

            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier)verifier);
    }

    public void installAllTrustManager() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){

                @Override
                public boolean verify(String urlHostname, SSLSession _session) {
                    return true;
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject _doCharge_old(String cardType, String pin, String seri, String transId,long amount) throws Exception {
        this.installAllTrustManager();
        this.validateCharge(cardType, pin, seri);   
        URL url = new URL("http://gachthe.vn/API/NapThe?APIKey=" + PartnerConfig.GachTheSecretKey + "&Network=" + cardType.toUpperCase() + "&CardCode=" + pin + "&CardSeri=" + seri + "&CardValue=" + amount + "&URLCallback=" + PartnerConfig.GachTheCallBackUrl + "&TrxID=" + transId);
        HttpURLConnection request = (HttpURLConnection)url.openConnection();
        request.setConnectTimeout(90000);
        request.setUseCaches(false);
        request.setDoOutput(true);
        request.setDoInput(true);
        HttpURLConnection.setFollowRedirects(true);
        request.setInstanceFollowRedirects(true);
        request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        request.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String result = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            result = result.concat(line);
        }
        JSONObject json = (JSONObject)new JSONParser().parse(result);
        return json;
    }
    public JSONObject doCharge_tokyo(String cardType, String pin, String seri, String transId, long amount) throws Exception {

        this.installAllTrustManager();
        this.validateCharge(cardType, pin, seri);

        String apiToken="8c14LNUjglnT6o2NhsQIBYItrohAXIxoBZ1YfzgRuKx04WOLC6QZDu7NMuNk6cAD";
        String signature = VinPlayUtils.getMD5Hash(apiToken+amount+transId+seri);

        String urlCallBack="https://apisieunhangao.net/api?c=4001&";
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "CardSeri="+seri+"&CardCode="+pin+"&Amount="+amount+"&Signature="+signature+"&TransID="+transId+"&CardType="+cardType+"&ApiToken="+apiToken+"&UrlCallBack="+urlCallBack+"");
        Request request = new Request.Builder()
                .url("http://66.42.62.119:8082/partner/RequestPayment")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();

        JSONObject json = (JSONObject) new JSONParser().parse(response.body().string());

        return json;


    }

    public JSONObject doCharge_duPhong(String cardType, String pin, String seri, String transId, long amount) throws Exception {

        this.installAllTrustManager();
        this.validateCharge(cardType, pin, seri);

        String  command = "charging";
        String partner_id = "9630150361";
        String telo = "";
        if(cardType.equals("VT")){
            telo = "VIETTEL";
        }else if(cardType.equals("Vina")){
            telo = "VINAPHONE";
        }else if(cardType.equals("Mobi")){
            telo = "MOBIFONE";
        }else{
            telo = "Error";
        }
        String signature = GenSignMd5_DP(pin,seri, transId, telo);

        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("telco",telo)
                .addFormDataPart("code",pin)
                .addFormDataPart("serial",seri)
                .addFormDataPart("amount",amount+"")
                .addFormDataPart("request_id",transId)
                .addFormDataPart("partner_id",partner_id)
                .addFormDataPart("sign", signature)
                .addFormDataPart("command",command)
                .build();
        Request request = new Request.Builder()
                .url("http://chietkhaucao.com/chargingws/v2")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String resul_resp = response.body().string();
        String[] data1 = resul_resp.split(",");
        String data2 = "";
        String data2_plus = "";
        for(String xx : data1){
            if(xx.contains("status")){
                data2 = xx;
            }
            if(xx.contains("message")){
                data2_plus = xx;
            }
        }
        String[] data3 = data2.split(":");
        String[] data4 = data2_plus.split(":");
        String codex = data3[1];
        codex = codex.replaceAll("}","");
        int code1 = Integer.parseInt(codex);
        String code2 = data4[1];
        code2 = code2.replaceAll("}","");


        int code0 = 0;
        if(code1 == 99){
            code0 = 0;
        }else if(code1 == 1){
            code0 = 0;
        }else {
            code0 = code1;
        }
        String response_end = "{\"msg\":"+code2+",\"errorCode\":"+code0+"}";
        JSONObject json = (JSONObject) new JSONParser().parse(response_end);
        return json;

    }

    public JSONObject doCharge(String cardType, String pin, String seri, String transId, long amount) throws Exception {

        this.installAllTrustManager();
        this.validateCharge(cardType, pin, seri);
        String telo = "";
        if(cardType.equals("VT")){
            telo = "VTT";
        }else if(cardType.equals("Vina")){
            telo = "VNP";
        }else if(cardType.equals("Mobi")){
            telo = "VMS";
        }else{
            telo = "Error";
        }

        String username = "vivu06";
        String pass = "vivu06hshshshsbshhshz@";
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"username\":\""+username+"\",\"password\":\""+pass+"\",\"amount\":\""+amount+"\",\"serial\":\""+seri+"\",\"telco\":\""+telo+"\",\"pincode\":\""+pin+"\",\"requestid\":\""+transId+"\"}");
        Request request = new Request.Builder()
                .url("http://45.32.106.125:8899/checkcard")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String resul_resp = response.body().string();
        String[] data1 = resul_resp.split(",");
        String data2 = "";
        String data2_plus = "";
        String data2_plusplus = "";
        for(String xx : data1){
            if(xx.contains("errorcode")){
                data2 = xx;
            }
            if(xx.contains("description")){
                data2_plus = xx;
            }
            if(xx.contains("requestid")){
                data2_plusplus = xx;
            }
        }
        String[] data3 = data2.split(":");
        String[] data4 = data2_plus.split(":");
        String[] data5 = data2_plusplus.split(":");
        String codex = data3[1];
        codex = codex.replaceAll("}","");
        codex = codex.replaceAll("\\{","");
        codex = codex.replaceAll("\"","");
        int code1 = Integer.parseInt(codex);
        String code2 = data4[1];
        code2 = code2.replaceAll("}","");
        code2 = code2.replaceAll("\\{","");
        String code3 = data5[1];
        code3 = code3.replaceAll("}","");
        code3 = code3.replaceAll("\\{","");

        int code0 = 0;
        if(code1 == 1){
            code0 = 0;
        }else {
            code0 = code1;
        }

        String response_end = "{\"msg\":"+code2+",\"errorCode\":"+code0+"}";
        JSONObject json = (JSONObject) new JSONParser().parse(response_end);
        return json;


    }

    private boolean validateCharge(String cardType, String pin, String seri) throws GachTheException {
        if (cardType == null || cardType.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 lo\u1ea1i th\u1ebb", 1);
        }
//        if (!Arrays.asList("VTT", "VNP", "VMS", "FPT").contains(cardType)) {
//            throw new GachTheException("Lo\u1ea1i th\u1ebb kh\u00f4ng h\u1ee3p l\u1ec7", 2);
//        }
        if (pin == null || pin.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 m\u00e3 pin", 3);
        }
        if (seri == null || seri.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 serial", 4);
        }
        return true;
    }

    public String GenSignMd5_DP(String code, String seri,String request_id, String telco){
        try {
            String partner_key = "bdc34ba7cf08bd2fe9ee571536a0abc5";
            String partner_id = "9630150361";
            String command = "charging";
            String input = partner_key+code+command+partner_id+request_id+seri+telco;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}


