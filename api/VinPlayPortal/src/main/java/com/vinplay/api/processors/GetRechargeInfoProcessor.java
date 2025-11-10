/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.LogMoneyUserServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.LogMoneyUserResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import com.vinplay.api.processors.AutoXuLyBank.APIProcess;
import com.vinplay.api.processors.AutoXuLyBank.AutoBankEntity;
import com.vinplay.common.HttpCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.BankModel;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GetRechargeInfoProcessor implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            AutoBankEntity autoBank = new AutoBankEntity();
            String json = "{"
                    + "\"PartnerCode\":\"" + autoBank.partnerCode + "\""
                    + "}";
            System.out.println("Request AutoBank tao code pay: " + json);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json
            );

            Request request = new Request.Builder()
                    .url(autoBank.getBank)
                    .post(body)
                    .build();
            Response response = HttpCommon.getInstance().httpClient.newCall(request).execute();
            String data = response.body().string();
            System.out.println("Response AutoBank tao code pay: " + data);
            List<BankModel> bankModels = new ArrayList<>();
            if (data.contains("\"ResponseCode\":1")) {
                JSONObject obj = new JSONObject(data);
                String contentStr = obj.getString("ResponseContent");
                JSONArray banks = new JSONArray(contentStr);
                for (int i = 0; i < banks.length(); i++) {
                    JSONObject bank = banks.getJSONObject(i);
                    System.out.println("BankName: " + bank.getString("BankName"));
                    System.out.println("Name: " + bank.getString("Name"));
                    BankModel bankModel = new BankModel();
                    bankModel.code = bank.getString("BankName");
                    bankModel.shortName = bank.getString("Name");
                    bankModels.add(bankModel);
                }
            }
            JSONArray jsonArray = new JSONArray();
            for (BankModel bank : bankModels) {
                JSONObject jsonBank = new JSONObject();
                jsonBank.put("code", bank.code);
                jsonBank.put("shortName", bank.shortName);
                jsonArray.put(jsonBank);
            }

            JSONObject result = new JSONObject();
            result.put("data", jsonArray);

            System.out.println("Response formatted: " + result.toString());
            return result.toString();
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            return ex.getMessage() + "\n" + sStackTrace;
        }
    }

}

