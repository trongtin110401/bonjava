package com.vinplay.api.otp;

import com.vinplay.common.HttpCommon;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class SendOTP {

    public synchronized void sendSMSOTP_SMS(String phone, String otp){
        try {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://139.180.187.22:33333/partner/sendOTP?accesskey=V3XzkMQ5XVSDW&phone="+phone+"&text="+otp)
                .method("GET", null)
                .build();

            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendSMSOTP(String phone, String otp){
        try {
            String request_id = String.valueOf(VinPlayUtils.generateTransId());
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://167.179.105.5:1168/Callotp?username=vivu06&password=290192skaskfnejfweijgwkje&msisdn="+phone+"&mes="+otp+"&request_id="+request_id)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
