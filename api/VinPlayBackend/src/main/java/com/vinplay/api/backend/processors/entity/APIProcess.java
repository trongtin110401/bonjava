package com.vinplay.api.backend.processors.entity;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class APIProcess {

    public static String responseGetAPI(String url, RequestBody body) {
        OkHttpClient httpClient = new OkHttpClient();
        OkHttpClient client = httpClient.newBuilder()
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

