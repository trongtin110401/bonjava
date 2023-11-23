package com.vinplay.ace.service;

import com.vinplay.ace.AceRequestEntity;
import com.vinplay.common.HttpCommon;
import okhttp3.*;

import java.io.IOException;

public class HttpAceService {
    public static Response callToAce(String url , AceRequestEntity object , String method, String tagName) throws IOException { //"http://chinxunxoan.com/sport/deposit"
        OkHttpClient client =  HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, object.toJson());
        Request request = new Request.Builder()
                .url(url)
                .method(method, body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
