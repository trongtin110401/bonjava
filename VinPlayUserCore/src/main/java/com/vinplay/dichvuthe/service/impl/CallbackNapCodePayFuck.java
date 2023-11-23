package com.vinplay.dichvuthe.service.impl;

import com.vinplay.common.HttpCommon;
import okhttp3.*;

import java.util.Base64;

public class CallbackNapCodePayFuck {
    public void callback(String comment, String amount,String nn){
        try {
            String key = "SuNvIn88";
            String originalInput = key+comment+amount;
            String signature = Base64.getEncoder().encodeToString(originalInput.getBytes());
            boolean check = false;
            String sig = "\"errorCode\": 200";
            do{
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://lunglinhlalenluons.store/api?c=4042&comment="+comment+"&amount="+amount+"&signature="+signature+"&nn="+nn)
                        .method("GET", null)
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }
            }while (check == false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
