package com.vinplay.api.backend.processors.daily;

import com.vinplay.common.HttpCommon;
import com.vinplay.lognaprut.entities.UserVinBongDaModel;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BongDaELK {

    public final String chuyenTien = "Chuyển tiền bóng đá";
    public final String nhanTien = "Nhận tiền bóng đá";

    public ArrayList<UserVinBongDaModel> getListBongDaChuyenTien(String time_start, String time_end){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<UserVinBongDaModel> listHis = null;
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                listHis = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"action_name.keyword\":\"BongDA\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}},{\"match\":{\"service_name.keyword\":\""+chuyenTien+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
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
                    UserVinBongDaModel uservin = new UserVinBongDaModel(nick_name, service_name, action_name, description, current_money, money_exchange, fee, create_date);
                    listHis.add(uservin);

                }
            }while (check == false);
            return listHis;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UserVinBongDaModel> getListBongDaNhanTien(String time_start, String time_end){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<UserVinBongDaModel> listHis = null;
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                listHis = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"action_name.keyword\":\"BongDA\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}},{\"match\":{\"service_name.keyword\":\""+nhanTien+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
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
                    UserVinBongDaModel uservin = new UserVinBongDaModel(nick_name, service_name, action_name, description, current_money, money_exchange, fee, create_date);
                    listHis.add(uservin);

                }
            }while (check == false);
            return listHis;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UserVinBongDaModel> getListBongDaChuyenTienByNickName(String time_start, String time_end, String nickname){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<UserVinBongDaModel> listHis = null;
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                listHis = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"action_name.keyword\":\"BongDA\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}},{\"match\":{\"service_name.keyword\":\""+chuyenTien+"\"}},{\"match\":{\"nick_name.keyword\":\""+nickname+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
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
                    UserVinBongDaModel uservin = new UserVinBongDaModel(nick_name, service_name, action_name, description, current_money, money_exchange, fee, create_date);
                    listHis.add(uservin);

                }
            }while (check == false);
            return listHis;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<UserVinBongDaModel> getListBongDaNhanTienByNickName(String time_start, String time_end, String nickname){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<UserVinBongDaModel> listHis = null;
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                listHis = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"action_name.keyword\":\"BongDA\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}},{\"match\":{\"service_name.keyword\":\""+nhanTien+"\"}},{\"match\":{\"nick_name.keyword\":\""+nickname+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
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
                    UserVinBongDaModel uservin = new UserVinBongDaModel(nick_name, service_name, action_name, description, current_money, money_exchange, fee, create_date);
                    listHis.add(uservin);
                }
            }while (check == false);
            return listHis;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



}
