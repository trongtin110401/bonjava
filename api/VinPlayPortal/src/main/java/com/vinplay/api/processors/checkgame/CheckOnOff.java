package com.vinplay.api.processors.checkgame;

import com.vinplay.common.HttpCommon;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class CheckOnOff {
    public String getCheck(){
        boolean check = false;
        String sig = "\"successful\":1";
        String turn = "";
        try {
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    break;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match_all\":{}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/checkgame/_search")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }
                JSONObject obj = new JSONObject(data);
                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
                final int n = jsonArray.length();
                for (int i = 0; i < n; ++i) {
                    final JSONObject person = jsonArray.getJSONObject(i);
                    JSONObject test = person.getJSONObject("_source");
                    turn = test.getString("check");
                }
            }while (check == false);
            return turn;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return turn;
    }
}
