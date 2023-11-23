/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinplay.gachthe;

import com.vinplay.dichvuthe.utils.DvtUtils;
import com.vinplay.maxpay.ChargeMaxpayResponse;
import com.vinplay.maxpay.MaxpayException;
import com.vinplay.maxpay.ReCheckMaxpayResponse;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
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
import org.apache.commons.codec.binary.Hex;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author HA
 */
public class MuaTheClient {    

    public MuaTheClient() {
       
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

    public JSONObject doCharge(String cardType, String pin, String seri, String transId,long amount) throws Exception {
        this.installAllTrustManager();
//        this.validateCharge(cardType, pin, seri);   
        // request
//        MuaTheRequest req = new MuaTheRequest();
//        req.setAmount(amount);
//        req.setCode(pin);
//        req.setSerial(seri);
//        req.setPartner_id(PartnerConfig.MuaCard24hPartnerId);
//        req.setRequest_id(transId);
//        req.setCommand("charging");
        String telco = "";
        if ("VTT".equals(cardType))
        {
            telco = "VIETTEL";
        }
        else if ("VNP".equals(cardType))
        {
            telco = "VINAPHONE";
        }
        else if ("VMS".equals(cardType))
        {
            telco = "MOBIFONE";
        }
        else
        {
            
        }
        if ("".equals(telco))
        {
            JSONObject req = new JSONObject();
            req.put("status", -99);
            return req;
        }        
        // sign 
        String sign = PartnerConfig.MuaCard24hPartnerKey + pin + "charging" + PartnerConfig.MuaCard24hPartnerId + transId + seri + telco;
        sign = DvtUtils.getMd5(sign);        
        JSONObject req = new JSONObject();
        req.put("amount", amount);
        req.put("code", pin);
        req.put("serial", seri);
        req.put("partner_id", PartnerConfig.MuaCard24hPartnerId);
        req.put("request_id", transId);
        req.put("command", "charging");
        req.put("telco", telco);
        req.put("sign", sign);
        URL url = new URL("http://muacard24h.com/chargingws/v2");
        HttpURLConnection request = (HttpURLConnection)url.openConnection();
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
        JSONObject json = (JSONObject)new JSONParser().parse(result);
        return json;
    }   

    private boolean validateCharge(String cardType, String pin, String seri) throws GachTheException {
        if (cardType == null || cardType.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 lo\u1ea1i th\u1ebb", 1);
        }
        if (!Arrays.asList("VTT", "VNP", "VMS", "FPT").contains(cardType)) {
            throw new GachTheException("Lo\u1ea1i th\u1ebb kh\u00f4ng h\u1ee3p l\u1ec7", 2);
        }
        if (pin == null || pin.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 m\u00e3 pin", 3);
        }
        if (seri == null || seri.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 serial", 4);
        }
        return true;
    }
}


