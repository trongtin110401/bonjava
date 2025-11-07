package com.vinplay.api.processors.cashout;

import com.vinplay.api.processors.AutoXuLyBank.AutoBankEntity;
import com.vinplay.common.HttpCommon;
import com.vinplay.vbee.common.models.BankPartnerModel;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class NapSunVinBankMomo {
    public String AutoCodePay(String tranID, String bankCode) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "kind=json&name=sunvin&key=22a7b2091adfac0a0bb4a4b6a2a002d8&tranID="+tranID+"&type=Bank&bankCode="+bankCode+"&size=50&accessToken=DL5LdXp6r71Nq0YKccM5MjAyMS0xMi0xMiAxNDoxODowNg==");
        Request request = new Request.Builder()
                .url("https://v28.repo88.com/api/info")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        String resBody = response.body().string();
        return resBody;
    }

    public synchronized String AutoMomo(String tranID) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "kind=json&name=sunvin&key=22a7b2091adfac0a0bb4a4b6a2a002d8&tranID="+tranID+"&type=Momo&size=50&accessToken=DL5LdXp6r71Nq0YKccM5MjAyMS0xMi0xMiAxNDoxODowNg==");
        Request request = new Request.Builder()
                .url("https://v28.repo88.com/api/info")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        String resBody = response.body().string();
        return resBody;
    }

    public synchronized String GetInfoMomo() throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://1spay.online/api/momo/info?name=sunvin&key=22a7b2091adfac0a0bb4a4b6a2a002d8")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String resBody = response.body().string();
        return resBody;
    }


    public synchronized String AutoMomoDemo(String tranID, String comment, String nickname) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n\"kind\":\"json\",\r\n\"name\":\"sunvin\",\r\n\"key\":\"22a7b2091adfac0a0bb4a4b6a2a002d8\",\r\n\"tranID\":\""+tranID+"\",\r\n\"type\":\"Momo\",\r\n\"comment\": \""+comment+"\",\r\n\"character\": \""+nickname+"\",\r\n\"accessToken\":\"DL5LdXp6r71Nq0YKccM5MjAyMS0xMi0xMiAxNDoxODowNg==\",\r\n\"size\":\"100\"\r\n}");
        Request request = new Request.Builder()
                .url("https://1spay.online/api/info")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String resBody = response.body().string();
        return resBody;
    }


    public synchronized String AutoMomoDemoNew(String tranID, String comment, String nickname) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n\"kind\":\"json\",\r\n\"name\":\"sunvin\",\r\n\"key\":\"22a7b2091adfac0a0bb4a4b6a2a002d8\",\r\n\"tranID\":\""+tranID+"\",\r\n\"type\":\"Momo\",\r\n\"comment\": \""+comment+"\",\r\n\"character\": \""+nickname+"\",\r\n\"accessToken\":\"DL5LdXp6r71Nq0YKccM5MjAyMS0xMi0xMiAxNDoxODowNg==\",\r\n\"size\":\"100\"\r\n}");
        Request request = new Request.Builder()
                .url("https://1spay.online/api/info")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String resBody = response.body().string();
        return resBody;
    }

    public BankPartnerModel sendBenThuBaTaoCodePay(String requestId) {
        try {
            AutoBankEntity autoBank = new AutoBankEntity();
            String partnerCode = autoBank.partnerCode;
            String bankCode = "MOMO";
            String refCode = requestId;
            int amount = 1;
//            String callbackUrl = "https://9577d28839e4.ngrok-free.app/api?c=4037";
            String callbackUrl = "https://do79.club/api-portal?c=4037";
            String partnerKey = autoBank.partnerKey;
            String sign = partnerCode + bankCode + amount + refCode + callbackUrl + partnerKey;
            System.out.println("String sign AutoBank tao code pay: " + sign);
            String signature = DigestUtils.md5Hex(sign).toLowerCase();

//            String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiRegCharge()
//                    + "?apiKey=" + autoBank.getApiKey() + "&chargeType="+type+"&amount="+amount+
//                    "&requestId="+requestId+"&subType="+bankCode+
//                    "&sign="+sign;

            String json = "{"
                    + "\"PartnerCode\":\"" + partnerCode + "\","
                    + "\"BankCode\":\"" + bankCode + "\","
                    + "\"RefCode\":\"" + refCode + "\","
                    + "\"Amount\":" + amount + ","
                    + "\"CallbackUrl\":\"" + callbackUrl + "\","
                    + "\"Signature\":\"" + signature + "\""
                    + "}";

            System.out.println("Request AutoBank tao code pay: " + json);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json
            );

            Request request = new Request.Builder()
                    .url(autoBank.urlOrder)
                    .post(body)
                    .build();

            Response response = HttpCommon.getInstance().httpClient.newCall(request).execute();
            String data = response.body().string();
            System.out.println("Response AutoBank tao code pay: " + data);
            if (data.contains("\"ResponseCode\":1")) {
                JSONObject obj = new JSONObject(data);
                String contentStr = obj.getString("ResponseContent");
                JSONObject jsonContent = new JSONObject(contentStr);
                return convertJsonObjectToDate(jsonContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public BankPartnerModel convertJsonObjectToDate(JSONObject jsonObject) {
        try {
            BankPartnerModel bankPartnerModel = new BankPartnerModel();
            System.out.println("RefCode: " + jsonObject.getString("RefCode"));
            bankPartnerModel.id = jsonObject.getString("RefCode");
            System.out.println("QR URL: " + jsonObject.getString("Url"));
            bankPartnerModel.qr_url = jsonObject.getString("Url");
            System.out.println("Payment URL: " + jsonObject.getString("LinkWebView"));
            bankPartnerModel.payment_url = jsonObject.getString("LinkWebView");
            System.out.println("OrderNo: " + jsonObject.getString("OrderNo"));
            bankPartnerModel.code = jsonObject.getString("OrderNo");
            System.out.println("BankAccountNumber: " + jsonObject.getString("BankAccountNumber"));
            bankPartnerModel.phoneNum = jsonObject.getString("BankAccountNumber");
            System.out.println("Amount: " + jsonObject.getInt("Amount"));
            bankPartnerModel.amount = jsonObject.getInt("Amount");
            System.out.println("BankAccountName: " + jsonObject.getString("BankAccountName"));
            bankPartnerModel.phoneName = jsonObject.getString("BankAccountName");
            System.out.println("BankName: " + jsonObject.getString("BankName"));
            bankPartnerModel.chargeType = jsonObject.getString("BankName");
            System.out.println("Timeout: " + jsonObject.getInt("Timeout"));
            bankPartnerModel.bank_provider = jsonObject.getString("BankName");
            System.out.println("Time to expired: " + jsonObject.getInt("Timeout"));
            bankPartnerModel.timeToExpired = jsonObject.getInt("Timeout");
            return bankPartnerModel;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
