package com.vinplay.common;

import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HttpCommon {
   public  OkHttpClient httpClient;

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

    public static ArrayList<OkHttpClient> listProxy() {
        Authenticator proxyAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic("shady365", "You123@");
                return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
            }
        };
        OkHttpClient client1 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("jp.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client2 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("au.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client3 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("uk.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client4 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("nl.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client5 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("sg.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client6 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("us-il.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client7 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("us.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client8 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("us-dc.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        OkHttpClient client9 = HttpCommon.getInstance().getHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("us-ca.proxymesh.com", 31280)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();

        ArrayList<OkHttpClient> list = new ArrayList<>();
        list.add(client1);
        list.add(client2);
        list.add(client3);
        list.add(client4);
        list.add(client5);
        list.add(client6);
        list.add(client7);
        list.add(client8);
        list.add(client9);
        return list;
    }
}
