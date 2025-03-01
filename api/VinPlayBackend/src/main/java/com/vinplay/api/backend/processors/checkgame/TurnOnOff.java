package com.vinplay.api.backend.processors.checkgame;

import com.vinplay.common.HttpCommon;
import okhttp3.*;

public class TurnOnOff {
    public void TurnOnOf(String status){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    return;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\r\n   \"check\" : \""+status+"\"\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/checkgame/_doc/1")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//
//            }while (check == false);
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }


}
