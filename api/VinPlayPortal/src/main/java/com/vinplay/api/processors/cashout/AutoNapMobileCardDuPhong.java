package com.vinplay.api.processors.cashout;

import com.vinplay.common.HttpCommon;
import okhttp3.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AutoNapMobileCardDuPhong {
    public String GenSignMd5(String partner_key, String code,String command, String partner_id,
                             String request_id, String seri, String telco){
        try {
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

    public String callAPIDuPhong(String partner_key, String partner_id, String telco, String code,
                                 String seri, String amount, String request_id, String command){
        try {
            String sign = GenSignMd5(partner_key, code,command, partner_id, request_id, seri, telco);
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("telco",telco)
                    .addFormDataPart("code",code)
                    .addFormDataPart("serial",seri)
                    .addFormDataPart("amount",amount)
                    .addFormDataPart("request_id",request_id)
                    .addFormDataPart("partner_id",partner_id)
                    .addFormDataPart("sign", sign)
                    .addFormDataPart("command",command)
                    .build();
            Request request = new Request.Builder()
                    .url("http://chietkhaucao.com/chargingws/v2")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String resul_resp = response.body().string();
            return resul_resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        AutoNapMobileCardDuPhong au = new AutoNapMobileCardDuPhong();
//        String output = au.callAPIDuPhong("119a30adaf08ce533c65385f422a34ca","2130579261",
//                "MOBIFONE","664196324427","089801001443088",
//                "50000", "32323333", "charging");
//        System.out.println(output);
//        System.out.println(au.GenSignMd5("119a30adaf08ce533c65385f422a34ca","664196324427",
//                "charging","2130579261","32323333","089801001443088","MOBIFONE"));
//    }



}
