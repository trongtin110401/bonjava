package com.vinplay.api.backend.models;

import com.vinplay.common.HttpCommon;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OTPELK {
    public ArrayList<UserOTP> GetListActive(){
        try {
            Long create_time_2 = new Date().getTime();
            Date d = new Date(create_time_2);
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = sdfDate.format(d);
            ArrayList<UserOTP> listActive;
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                listActive = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"timelog.keyword\":{\"gt\":\""+strDate+" 00:00:00\",\"lt\":\""+strDate+" 23:59:59\"}}}],\"must_not\":[{\"match\":{\"username.keyword\":\"da xoa\"}}],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/active/_search")
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
                    String nickname = test.getString("nickname");
                    String username = test.getString("username");
                    String phone = test.getString("phone");
                    String otp = test.getString("otp");
                    int active = test.getInt("active");
                    Long creat_time = test.getLong("creat_time");
                    Long active_time = test.getLong("active_time");
                    int turn = test.getInt("turn");
                    String timelog = test.getString("timelog");
                    UserOTP uo = new UserOTP(nickname, username, phone, otp, active, creat_time, active_time, turn, timelog);
                    listActive.add(uo);

                }

            }while (check == false);
            return listActive;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserOTP GetListActivebyPhone(String phonenumber){
        try {
            UserOTP Activex = new UserOTP();
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
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"phone.keyword\":\""+phonenumber+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/active/_search")
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
                if(n == 0){
                    Activex = null;
                }else{
                    for (int i = 0; i < n; ++i) {
                        final JSONObject person = jsonArray.getJSONObject(i);
                        JSONObject test = person.getJSONObject("_source");
                        String nickname = test.getString("nickname");
                        String username = test.getString("username");
                        String phone = test.getString("phone");
                        String otp = test.getString("otp");
                        int active = test.getInt("active");
                        Long creat_time = test.getLong("creat_time");
                        Long active_time = test.getLong("active_time");
                        int turn = test.getInt("turn");
                        String timelog = test.getString("timelog");
                        Activex.setNickname(nickname);
                        Activex.setUsername(username);
                        Activex.setPhone(phone);
                        Activex.setOtp(otp);
                        Activex.setActive(active);
                        Activex.setCreat_time(creat_time);
                        Activex.setActive_time(active_time);
                        Activex.setTurn(turn);
                        Activex.setTimelog(timelog);
                    }
                }

            }while (check == false);
            return Activex;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public UserOTP GetListActivebyNickname(String nickname){
        try {
            UserOTP Activex = new UserOTP();
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
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickname.keyword\":\""+nickname+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/active/_search")
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
                if(n == 0){
                    Activex = null;
                }else{
                    for (int i = 0; i < n; ++i) {
                        final JSONObject person = jsonArray.getJSONObject(i);
                        JSONObject test = person.getJSONObject("_source");
                        String nicknamex = test.getString("nickname");
                        String username = test.getString("username");
                        String phone = test.getString("phone");
                        String otp = test.getString("otp");
                        int active = test.getInt("active");
                        Long creat_time = test.getLong("creat_time");
                        Long active_time = test.getLong("active_time");
                        int turn = test.getInt("turn");
                        String timelog = test.getString("timelog");
                        Activex.setNickname(nicknamex);
                        Activex.setUsername(username);
                        Activex.setPhone(phone);
                        Activex.setOtp(otp);
                        Activex.setActive(active);
                        Activex.setCreat_time(creat_time);
                        Activex.setActive_time(active_time);
                        Activex.setTurn(turn);
                        Activex.setTimelog(timelog);
                    }
                }

            }while (check == false);
            return Activex;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserOTP GetListActivebyUsername(String usernamex) {
        try {
            UserOTP Activex = new UserOTP();
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
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"username.keyword\":\""+usernamex+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/active/_search")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }
                JSONObject obj = new JSONObject(data);
                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
                final int n = jsonArray.length();
                if (n == 0) {
                    Activex = null;
                } else {
                    for (int i = 0; i < n; ++i) {
                        final JSONObject person = jsonArray.getJSONObject(i);
                        JSONObject test = person.getJSONObject("_source");
                        String nickname = test.getString("nickname");
                        String username = test.getString("username");
                        String phone = test.getString("phone");
                        String otp = test.getString("otp");
                        int active = test.getInt("active");
                        Long creat_time = test.getLong("creat_time");
                        Long active_time = test.getLong("active_time");
                        int turn = test.getInt("turn");
                        String timelog = test.getString("timelog");
                        Activex.setNickname(nickname);
                        Activex.setUsername(username);
                        Activex.setPhone(phone);
                        Activex.setOtp(otp);
                        Activex.setActive(active);
                        Activex.setCreat_time(creat_time);
                        Activex.setActive_time(active_time);
                        Activex.setTurn(turn);
                        Activex.setTimelog(timelog);
                    }
                }

            } while (check == false);
            return Activex;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String GetIDActivebyNickname(String nickname){
        try {
            String id_elk = "";
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
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickname.keyword\":\""+nickname+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/active/_search")
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
                if(n == 0){
                    id_elk = "";
                }else{
                    for (int i = 0; i < n; ++i) {
                        final JSONObject person = jsonArray.getJSONObject(i);
                        JSONObject test = person.getJSONObject("_source");
                        id_elk = person.getString("_id");

                    }
                }

            }while (check == false);
            return id_elk;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void DeleteActiveELK(UserOTP uo, String id_elk){
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
                RequestBody body = RequestBody.create(mediaType, "{ \"nickname\": \""+uo.getNickname()+"\", \"username\": \""+uo.getUsername()+"\", \"phone\": \""+uo.getPhone()+"\", \"otp\": \""+uo.getOtp()+"\", \"active\": "+uo.getActive()+", \"creat_time\": "+uo.getCreat_time()+", \"active_time\": "+uo.getActive_time()+", \"turn\": "+uo.getTurn()+", \"timelog\": \""+uo.getTimelog()+"\" }");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/active/_doc/"+id_elk)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
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

    public ArrayList<UserOTP> GetListActivebyDate(String startDate, String endDate){
        try {
            ArrayList<UserOTP> listActive;
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                listActive = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"timelog.keyword\":{\"gt\":\""+startDate+"\",\"lt\":\""+endDate+"\"}}}],\"must_not\":[{\"match\":{\"username.keyword\":\"da xoa\"}}],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/active/_search")
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
                    String nickname = test.getString("nickname");
                    String username = test.getString("username");
                    String phone = test.getString("phone");
                    String otp = test.getString("otp");
                    int active = test.getInt("active");
                    Long creat_time = test.getLong("creat_time");
                    Long active_time = test.getLong("active_time");
                    int turn = test.getInt("turn");
                    String timelog = test.getString("timelog");
                    UserOTP uo = new UserOTP(nickname, username, phone, otp, active, creat_time, active_time, turn, timelog);
                    listActive.add(uo);

                }

            }while (check == false);
            return listActive;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void UpdateActiveELK(UserOTP uo, String id_elk){
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
                RequestBody body = RequestBody.create(mediaType, "{ \"nickname\": \""+uo.getNickname()+"\", \"username\": \""+uo.getUsername()+"\", \"phone\": \""+uo.getPhone()+"\", \"otp\": \""+uo.getOtp()+"\", \"active\": "+uo.getActive()+", \"creat_time\": "+uo.getCreat_time()+", \"active_time\": "+uo.getActive_time()+", \"turn\": "+uo.getTurn()+", \"timelog\": \""+uo.getTimelog()+"\" }");
                Request request = new Request.Builder()
                        .url("http://127.0.0.1:9200/active/_doc/"+id_elk)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
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
