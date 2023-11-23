package com.vinplay.api.backend.processors.xoaloglive;

import com.vinplay.common.HttpCommon;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class XoaTransELK {
    public ArrayList<String> ListIdELK(String nickname){
        try {
            ArrayList<String> listID;
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                listID = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\""+nickname+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/history_user_transaction/_search")
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
                if(n != 0){
                    for (int i = 0; i < n; ++i) {
                        final JSONObject person = jsonArray.getJSONObject(i);
                        String idelk = person.getString("_id");
                        listID.add(idelk);
                    }
                }

            }while (check == false);
            return listID;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void Xoa(String idelk){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    break;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = RequestBody.create(mediaType, "");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/history_user_transaction/_doc/"+idelk)
                        .method("DELETE", body)
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
