package com.vinplay.api.processors.AutoXuLyBank;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class APIProcess {

    public static String responseGetAPI(String url, RequestBody body) {
        OkHttpClient httpClient = new OkHttpClient();
        OkHttpClient client = httpClient.newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)  // Set connect timeout to 3 seconds
                .readTimeout(3, TimeUnit.SECONDS)     // Set read timeout to 3 seconds
                .build();


        Request request = new Request.Builder()
                .url(url)
                .method("GET", body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                return "{\"error\":404,\"data\":" + "not found" + "}";
            }
            Gson gson = new Gson();
            Object jsonObject = gson.fromJson(response.body().string(), Object.class);
            return gson.toJson(jsonObject);

        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\":404,\"data\":" + "not found" + "}";
        }
    }
}

