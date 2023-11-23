package com.vinplay.cashout;

import com.vinplay.usercore.utils.GameCommon;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.json.JSONArray;
import org.json.JSONObject;


import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class HunghapayClient {
    public String Username;
    public String Password;
    public HunghapayClient(String username, String password){
        this.Username = username;
        this.Password = password;
    }
    public String Login(){
        try{
            this.installAllTrustManager();
            URL url = new URL("https://api.hunghapay.com/v2/PayCard/DangNhap?userName="+this.Username+"&password="+this.Password);
            HttpsURLConnection request = (HttpsURLConnection)url.openConnection();
            request.setConnectTimeout(90000);
            request.setUseCaches(false);
            request.setDoOutput(true);
            request.setDoInput(true);
            HttpURLConnection.setFollowRedirects(true);
            request.setInstanceFollowRedirects(true);
            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request.setRequestMethod("POST");
            BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String result = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                result = result.concat(line);
            }
            return result;
        }catch (Exception e){
            return null;
        }

    }
    public HungHaCardResponse BuyCard(String cardName, long cardValue, int cardNumber){
        try{
            String token = this.Login();
            if(token.isEmpty()){
                return null;
            }
            token = token.replace("\"","");
            URL url = new URL("https://api.hunghapay.com/v2/PayCards/TelcoPay/GetCards?msg="+cardName+":"+ cardValue +":"+cardNumber);
            HttpsURLConnection request = (HttpsURLConnection)url.openConnection();


            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request.setRequestProperty("Token", token);
            request.setRequestMethod("POST");
            request.setConnectTimeout(90000);
            request.setUseCaches(false);
            request.setDoOutput(true);
            request.setDoInput(true);
            HttpURLConnection.setFollowRedirects(true);
            request.setInstanceFollowRedirects(true);
            BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String result = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                result = result.concat(line);
            }
            org.json.JSONObject json = new org.json.JSONObject(result);
            return this.convertData(json);

        }catch (Exception e){
            return null;
        }

    }
    private HungHaCardResponse convertData(JSONObject result){
        try{
            HungHaCardResponse res = new HungHaCardResponse();
            List<HungHaCard> listCard = new ArrayList<HungHaCard>();
            int errorCode = result.getInt("errorCode");
            if(errorCode != 0){
                return null;
            }
            String arrayCardString = result.getString("Data");
            arrayCardString = arrayCardString.replace("\\","");
            JSONArray arrayCard = new JSONArray(arrayCardString);
           // JSONArray arrayCard = result.getJSONArray("Data");
            for (int i = 0; i < arrayCard.length(); i++){
                JSONObject card = arrayCard.getJSONObject(i);
                HungHaCard cardObj = new HungHaCard(
                        card.getString("PinCode"),
                        card.getString("Telco"),
                        card.getString("Serial"),
                        card.getString("Amount"),
                        card.getString("Trace")
                );
                listCard.add(cardObj);
            }
            res.ListCard = listCard;
            res.Total = arrayCard.length();
            return res;
        }catch (Exception e){
            return null;
        }
    }
    public void installMyPolicy() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

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
        X509HostnameVerifier verifier = new X509HostnameVerifier() {

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
        HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier) verifier);
    }

    public void installAllTrustManager() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

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
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String urlHostname, SSLSession _session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
