package com.vinplay.gachthe;

import com.vinplay.dichvuthe.utils.DvtUtils;
import com.vinplay.usercore.utils.PartnerConfig;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class ZoanCardClient {

    public ZoanCardClient() {

    }

    private static String token = "";
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

    public JSONObject doCharge(String cardType, String pin, String seri, String transId, long amount) throws Exception {
        this.installAllTrustManager();
        doLogin();
        // get telco
        String sCardType = "";
        if (cardType.equals("vtt") || cardType.equals("VTT"))
        {
            sCardType = "VTT";
        }
        else if (cardType.equals("vnp") || cardType.equals("VNP"))
        {
            sCardType = "VINA";
        }
        else if (cardType.equals("vms") || cardType.equals("VMS"))
        {
            sCardType = "VMS";
        }
        int telcoId = GetTelco(sCardType);
        if (telcoId > 0)
        {
            // get price
            int denId = GetPriceId(amount);
            if (denId > 0)
            {
                JSONObject req = new JSONObject();
                req.put("telcoId", telcoId);
                req.put("denId", denId);
                req.put("code", pin);
                req.put("serial", seri);
                req.put("scratchCallbackUrl", PartnerConfig.ZoanCardCallbackUrl + "?orderId=" + transId);
                URL url = new URL(PartnerConfig.ZoanCardEndpoint + "/v2/cp/card?auth=" + token);
                HttpURLConnection request = (HttpURLConnection)url.openConnection();
                request.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                request.setConnectTimeout(90000);
                request.setUseCaches(false);
                request.setDoOutput(true);
                request.setDoInput(true);
                HttpURLConnection.setFollowRedirects(true);
                request.setInstanceFollowRedirects(true);
                request.setRequestProperty("Content-Type", "application/json");
                request.setRequestMethod("POST");
                OutputStream os = request.getOutputStream();
                os.write(req.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String result = "";
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result = result.concat(line);
                }
                if (result != null && !result.equals("")) {
                    JSONObject json = new JSONObject(result);
                    return json;
                }
            }
        }
        return null;
    }

    private void doLogin() throws Exception
    {
        JSONObject req = new JSONObject();
        req.put("username", PartnerConfig.ZoanCardUsername);
        req.put("password", PartnerConfig.ZoanCardPassword);
        URL url = new URL(PartnerConfig.ZoanCardEndpoint + "/user/login");
        HttpURLConnection request = (HttpURLConnection)url.openConnection();
        request.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        request.setConnectTimeout(90000);
        request.setUseCaches(false);
        request.setDoOutput(true);
        request.setDoInput(true);
        HttpURLConnection.setFollowRedirects(true);
        request.setInstanceFollowRedirects(true);
        request.setRequestProperty("Content-Type", "application/json");
        request.setRequestMethod("POST");
        OutputStream os = request.getOutputStream();
        os.write(req.toString().getBytes("UTF-8"));
        os.close();

        BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String result = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            result = result.concat(line);
        }
        if (result != null && !result.equals(""))
        {
            JSONObject json = new JSONObject(result);
            if (json != null)
            {
                JSONObject data = (JSONObject) json.get("data");
                if (data != null)
                {
                    token = String.valueOf(data.get("sid"));
                }
            }
        }
    }

    private int GetTelco(String telco) throws Exception
    {
        URL url = new URL(PartnerConfig.ZoanCardEndpoint + "/telcos?auth=" + token);
        HttpURLConnection request = (HttpURLConnection)url.openConnection();
        request.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
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
        if (result != null && !result.equals(""))
        {
            JSONObject json = new JSONObject(result);
            if (json != null)
            {
                JSONArray data = (JSONArray) json.get("data");
                if (data != null && data.length() > 0)
                {
                    for (int i = 0;i < data.length();i++)
                    {
                        JSONObject match = (JSONObject) data.get(i);
                        if (String.valueOf(match.get("code")).equals(telco))
                        {
                            return (int)match.get("id");
                        }
                    }
                }
            }
        }
        return 0;
    }

    private int GetPriceId(long amount) throws Exception
    {
        URL url = new URL(PartnerConfig.ZoanCardEndpoint + "/denominations?auth=" + token);
        HttpURLConnection request = (HttpURLConnection)url.openConnection();
        request.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
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
        if (result != null && !result.equals(""))
        {
            JSONObject json = new JSONObject(result);
            if (json != null)
            {
                JSONArray data = (JSONArray) json.get("data");
                if (data != null && data.length() > 0)
                {
                    for (int i = 0;i < data.length();i++)
                    {
                        JSONObject match = (JSONObject) data.get(i);
                        if (String.valueOf(match.get("code")).equals(amount + ""))
                        {
                            return (int)match.get("id");
                        }
                    }
                }
            }
        }
        return 0;
    }
}
