package com.vinplay.common;

import okhttp3.OkHttpClient;

public class HttpCommon2 {
   public  OkHttpClient httpClient;

   public static HttpCommon2 _instance;

   public static HttpCommon2 getInstance(){
       if(_instance==null){
           _instance = new HttpCommon2();
       }
       return _instance;
   }

    public HttpCommon2() {
        httpClient = new OkHttpClient();
    }

    public  OkHttpClient getHttpClient() {
        return httpClient;
    }
}
