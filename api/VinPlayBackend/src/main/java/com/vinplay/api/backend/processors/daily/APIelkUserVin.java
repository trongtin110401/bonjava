package com.vinplay.api.backend.processors.daily;

import com.vinplay.common.HttpCommon;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class APIelkUserVin {
    public ArrayList<UserVinEntity> GetUserVin(String time_start, String time_end, int numStart, int maxIteam){
        try {
            ArrayList<UserVinEntity> list_user_vin;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_user_vin = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":"+numStart+",\"size\":"+maxIteam+",\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_search")
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
                    String nick_name = test.getString("nick_name");
                    String service_name = test.getString("service_name");
                    String action_name = test.getString("action_name");
                    String description = test.getString("description");
                    long current_money = test.getLong("current_money");
                    long money_exchange = test.getLong("money_exchange");
                    long fee = test.getLong("fee");
                    String create_date = test.getString("create_time");
                    UserVinEntity uservin = new UserVinEntity(nick_name, service_name, action_name, description, current_money, money_exchange, fee, create_date);
                    list_user_vin.add(uservin);
                }
            }while (check == false);


            return list_user_vin;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public int GetTongUserVin(String time_start, String time_end){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int tong = 0;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                tong = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_search")
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
                tong = n;
            }while (check == false);


            return tong;
        } catch (Exception e) {
             e.printStackTrace();
        }
        return 0;
    }


    public ArrayList<UserVinEntity> GetUserVinByNickName(String nickname, String time_start, String time_end, int numStart, int maxIteam){
        try {
            ArrayList<UserVinEntity> list_user_vin;
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_user_vin = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nick_name.keyword\":\""+nickname+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":"+numStart+",\"size\":"+maxIteam+",\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_search")
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
                    String nick_name = test.getString("nick_name");
                    String service_name = test.getString("service_name");
                    String action_name = test.getString("action_name");
                    String description = test.getString("description");
                    long current_money = test.getLong("current_money");
                    long money_exchange = test.getLong("money_exchange");
                    long fee = test.getLong("fee");
                    String create_date = test.getString("create_time");
                    UserVinEntity uservin = new UserVinEntity(nick_name, service_name, action_name, description, current_money, money_exchange, fee, create_date);
                    list_user_vin.add(uservin);
                }
            }while (check == false);


            return list_user_vin;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UserVinEntity> GetUserVinByServiceName(String servicename, String time_start, String time_end, int numStart, int maxIteam){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<UserVinEntity> list_user_vin;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_user_vin = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"service_name.keyword\":\""+servicename+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":"+numStart+",\"size\":"+maxIteam+",\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_search")
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
                    String nick_name = test.getString("nick_name");
                    String service_name = test.getString("service_name");
                    String action_name = test.getString("action_name");
                    String description = test.getString("description");
                    long current_money = test.getLong("current_money");
                    long money_exchange = test.getLong("money_exchange");
                    long fee = test.getLong("fee");
                    String create_date = test.getString("create_time");
                    UserVinEntity uservin = new UserVinEntity(nick_name, service_name, action_name, description, current_money, money_exchange, fee, create_date);
                    list_user_vin.add(uservin);
                }
            }while (check == false);


            return list_user_vin;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UserVinEntity> GetUserVinByServiceNameNickname(String nickname, String servicename, String time_start, String time_end, int numStart, int maxIteam){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<UserVinEntity> list_user_vin;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_user_vin = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"service_name.keyword\":\""+servicename+"\"}},{\"match\":{\"nick_name.keyword\":\""+nickname+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":"+numStart+",\"size\":"+maxIteam+",\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_search")
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
                    String nick_name = test.getString("nick_name");
                    String service_name = test.getString("service_name");
                    String action_name = test.getString("action_name");
                    String description = test.getString("description");
                    long current_money = test.getLong("current_money");
                    long money_exchange = test.getLong("money_exchange");
                    long fee = test.getLong("fee");
                    String create_date = test.getString("create_time");
                    UserVinEntity uservin = new UserVinEntity(nick_name, service_name, action_name, description, current_money, money_exchange, fee, create_date);
                    list_user_vin.add(uservin);
                }
            }while (check == false);


            return list_user_vin;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public int TongUserVinByNickName(String nickname, String time_start, String time_end){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int tong = 0;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                tong = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nick_name.keyword\":\""+nickname+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_search")
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
                tong = n;
            }while (check == false);


            return tong;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int TongUserVinByServiceName(String servicename, String time_start, String time_end){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int tong = 0;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                tong = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"service_name.keyword\":\""+servicename+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_search")
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
                tong = n;
            }while (check == false);

            return tong;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int TongUserVinByServiceNameNickname(String nickname, String servicename, String time_start, String time_end){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int tong = 0;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return 0;
                }
                tong = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"service_name.keyword\":\""+servicename+"\"}},{\"match\":{\"nick_name.keyword\":\""+nickname+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_search")
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
                tong = n;
            }while (check == false);
            return tong;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
