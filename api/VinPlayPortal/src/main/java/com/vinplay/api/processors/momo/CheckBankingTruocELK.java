package com.vinplay.api.processors.momo;

import com.vinplay.common.HttpCommon;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CheckBankingTruocELK {

    public void InsertDonTruoc(String code, String timelog, long time, long tien){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    break;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"code\":\""+code+"\",\"xuly\":\"0\",\"timelog\":\""+timelog+"\",\"time\":"+time+",\"tien\": "+tien+"}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/taodontruoc/_doc")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//            }while (check == false);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public ArrayList<NapTruoc> GetDon(String code){
//        try {
//            ArrayList<NapTruoc> listDon;
//            String nick = "";
//            String code1 = code.toUpperCase();
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    return null;
//                }
//                listDon = new ArrayList<>();
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"xuly.keyword\":\"0\"}},{\"match\":{\"code.keyword\":\"SN1234567\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/taodontruoc/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                if(n != 0){
//                    for (int i = 0; i < n; ++i) {
//                        final JSONObject person = jsonArray.getJSONObject(i);
//                        String idelk = person.getString("_id");
//                        JSONObject test = person.getJSONObject("_source");
//                        String codex = test.getString("code");
//                        String xuly = test.getString("xuly");
//                        String timelog = test.getString("timelog");
//                        long time = test.getLong("time");
//                        long tien = test.getLong("tien");
//                        NapTruoc naptruoc = new NapTruoc(codex,xuly,timelog,time,idelk,tien);
//                        listDon.add(naptruoc);
//
//                    }
//                }
//
//            }while (check == false);
//            return listDon;
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public ArrayList<String> GetNicknameByCode(String code){
//        try {
//            ArrayList<String> listUser;
//            String code1 = code.toUpperCase();
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    return null;
//                }
//                listUser = new ArrayList<>();
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"code.keyword\":\""+code1+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/codeuserbank/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                if(n != 0){
//                    for (int i = 0; i < n; ++i) {
//                        final JSONObject person = jsonArray.getJSONObject(i);
//                        JSONObject test = person.getJSONObject("_source");
//                        String nickname = test.getString("nickname");
//                        listUser.add(nickname);
//                    }
//                }
//
//            }while (check == false);
//            return listUser;
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }



}
