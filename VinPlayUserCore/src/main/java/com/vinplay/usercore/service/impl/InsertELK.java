package com.vinplay.usercore.service.impl;

import com.vinplay.common.HttpCommon;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class InsertELK {

    public void InsertUserMapDaily(int userId, String username, String nickname, String codeDaily, String time_log){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.user_map_daily\",\r\n\"id\": \"666999\",\r\n\"userId\": "+userId+",\r\n\"user_name\": \""+username+"\",\r\n\"nickName\": \""+nickname+"\",\r\n\"id_daily\": \""+codeDaily+"\",\r\n\"time_log\": \""+time_log+"\"\r\n}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/user_map_daily/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();

                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserMapDLEntity getUserMapDLbyUserid(int userId){
        try {
            UserMapDLEntity us = new UserMapDLEntity();
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"userId\":\""+userId+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":250,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/user_map_daily/_search")
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
                    String nick_name = test.getString("nickName");
                    String user_name = test.getString("user_name");
                    String id_daily = test.getString("id_daily");
                    String time_log = test.getString("time_log");
                    String id_elk = person.getString("_id");
                    us.setUserID(userId);
                    us.setUser_name(user_name);
                    us.setNickName(nick_name);
                    us.setId_daily(id_daily);
                    us.setTime_log(time_log);
                    us.setId_elk(id_elk);
                }

            }while (check == false);
            return us;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void InsertUserMapDailyByIDelk(int userId, String username, String nickname, String codeDaily, String time_log, String id_elk){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.user_map_daily\",\r\n\"id\": \"666999\",\r\n\"userId\": "+userId+",\r\n\"user_name\": \""+username+"\",\r\n\"nickName\": \""+nickname+"\",\r\n\"id_daily\": \""+codeDaily+"\",\r\n\"time_log\": \""+time_log+"\"\r\n}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/user_map_daily/_doc/"+id_elk)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();

                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
