package com.vinplay.vbee.common.rmq;

import okhttp3.OkHttpClient;

public class HttpCommon {
    public OkHttpClient httpClient;

    public static HttpCommon _instance;

    public static HttpCommon getInstance(){
        if(_instance==null){
            _instance = new HttpCommon();
        }
        return _instance;
    }

    public HttpCommon() {
        httpClient = new OkHttpClient();
    }

    public  OkHttpClient getHttpClient() {
        return httpClient;
    }
}
