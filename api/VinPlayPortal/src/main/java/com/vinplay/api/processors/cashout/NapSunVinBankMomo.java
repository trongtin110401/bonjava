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

    public BankPartnerModel sendBenThuBaTaoCodePay(String bankCode, String type, long amount, String requestId) {
        try {
            String sign = amount + type + requestId + UUID.randomUUID();
            sign = DigestUtils.md5Hex(sign);

            AutoBankEntity autoBank = new AutoBankEntity();
            String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiRegCharge()
                    + "?apiKey=" + autoBank.getApiKey() + "&chargeType="+type+"&amount="+amount+
                    "&requestId="+requestId+"&subType="+bankCode+
                    "&callback=https://iwspay.apisieunhangao.net/api/bank/xxxxfaddf2wefdasdf&sign="+sign;
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            Response response = HttpCommon.getInstance().httpClient.newCall(request).execute();
            String data = response.body().string();
            if (data.contains("\"stt\":1")) {
                JSONObject obj = new JSONObject(data);
                JSONObject jsonArray = obj.getJSONObject("data");
                return convertJsonObjectToDate(jsonArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public BankPartnerModel convertJsonObjectToDate(JSONObject jsonObject) {
        try {
            BankPartnerModel bankPartnerModel = new BankPartnerModel();
            bankPartnerModel.id = jsonObject.getInt("id");
            bankPartnerModel.qr_url = jsonObject.getString("qr_url");
            bankPartnerModel.payment_url = jsonObject.getString("payment_url");
            bankPartnerModel.code = jsonObject.getString("code");
            bankPartnerModel.phoneNum = jsonObject.getString("phoneNum");
            bankPartnerModel.amount = jsonObject.getLong("amount");
            bankPartnerModel.phoneName = jsonObject.getString("phoneName");
            bankPartnerModel.chargeType = jsonObject.getString("chargeType");
            bankPartnerModel.bank_provider = jsonObject.getString("bank_provider");
            bankPartnerModel.timeToExpired = jsonObject.getInt("timeToExpired");
            return bankPartnerModel;
        } catch (Exception e) {
        }
        return null;
    }
}
