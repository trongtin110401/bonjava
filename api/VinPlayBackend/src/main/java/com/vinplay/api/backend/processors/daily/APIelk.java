package com.vinplay.api.backend.processors.daily;

import com.vinplay.common.HttpCommon;
import com.vinplay.daily.entities.UserDailyResponse;
import com.vinplay.daily.entities.UserWinLostResponse;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class APIelk {

    public int TotalUserbyIDDaiLy(String codeDaily){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int total_user = 0;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                total_user = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"id_daily.keyword\":\""+codeDaily+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/user_map_daily/_search")
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
                    if (!nick_name.equals("")) {
                        total_user = total_user + 1;
                    }
                }
            }while (check == false);


            return total_user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<String> UserbyIDDaiLy(String codeDaily){
        try {
            ArrayList<String> list_nickname;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_nickname = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"id_daily.keyword\":\""+codeDaily+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/user_map_daily/_search")
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
                    if (!nick_name.equals("")) {
                        list_nickname.add(nick_name);
                    }
                }
            }while (check == false);


            return list_nickname;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UserDailyResponse> UserbyIDDaiLy2(String codeDaily){
        try {
            ArrayList<UserDailyResponse> list_nickname;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_nickname = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"id_daily.keyword\":\""+codeDaily+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/user_map_daily/_search")
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
                    String time_log = test.getString("time_log");
                    if (!nick_name.equals("")) {
                        UserDailyResponse udl = new UserDailyResponse(nick_name, user_name, time_log);
                        list_nickname.add(udl);
                    }
                }
            }while (check == false);


            return list_nickname;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UserWinLostResponse> GetUserbyIDDaiLy(String codeDaily,int numStart, int maxItem){
        try {
            ArrayList<UserWinLostResponse> list_nickname;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }

                list_nickname = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"id_daily.keyword\":\""+codeDaily+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":"+numStart+",\"size\":"+maxItem+",\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/user_map_daily/_search")
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
                    String time_log = test.getString("time_log");
                    if (!nick_name.equals("")) {
                        UserWinLostResponse user = new UserWinLostResponse(nick_name, user_name, time_log);
                        list_nickname.add(user);
                    }
                }
            }while (check == false);


            return list_nickname;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public long GetTongTienNapByUser(String nickname, String time_start, String time_end){
        try{
            long tong_tien_nap = 0;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                tong_tien_nap = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\""+nickname+"\"}},{\"range\":{\"createAt.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_search")
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
                    String trangthai = test.getString("trangthai");
                    String hinhthuc = test.getString("hinhthuc");
                    long sotien = Long.parseLong(test.getString("sotien"));
                    if(trangthai.trim().equalsIgnoreCase("Thành công") || trangthai.trim().equalsIgnoreCase("Đã duyệt")){
                        if(hinhthuc.trim().equalsIgnoreCase("recharge")){
                            tong_tien_nap = tong_tien_nap + sotien;
                        }
                    }
                }
            }while (check == false);


            return tong_tien_nap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long GetTongTienRutByUser(String nickname, String time_start, String time_end){
        try{
            long tong_tien_rut = 0;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                tong_tien_rut = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\""+nickname+"\"}},{\"range\":{\"createAt.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_search")
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
                    String trangthai = test.getString("trangthai");
                    String hinhthuc = test.getString("hinhthuc");
                    long sotien = Long.parseLong(test.getString("sotien"))*(-1);
                    if(trangthai.trim().equalsIgnoreCase("Thành công") || trangthai.trim().equalsIgnoreCase("Đã duyệt")){
                        if(hinhthuc.trim().equalsIgnoreCase("Rút tiền")){
                            tong_tien_rut = tong_tien_rut + sotien;
                        }
                    }
                }
            }while (check == false);


            return tong_tien_rut;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long GetTongTienByUserSend(String nickname, String time_start, String time_end){
        try{
            long tong_tien_send = 0;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                tong_tien_send = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nick_name_send.keyword\":\""+nickname+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_chuyen_tien_dai_ly/_search")
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
                    long money_send = Long.parseLong(test.getString("money_send"));
                    tong_tien_send = tong_tien_send + money_send;
                }
            }while (check == false);

            return tong_tien_send;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long GetTongTienByUserReceive(String nickname, String time_start, String time_end){
        try{
            long tong_tien_Receive = 0;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                tong_tien_Receive = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nick_name_receive.keyword\":\""+nickname+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_chuyen_tien_dai_ly/_search")
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
                    long money_receive = Long.parseLong(test.getString("money_receive"));
                    tong_tien_Receive = tong_tien_Receive + money_receive;
                }
            }while (check == false);

            return tong_tien_Receive;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    



}
