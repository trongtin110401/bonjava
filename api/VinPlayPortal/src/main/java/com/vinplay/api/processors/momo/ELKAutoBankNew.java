package com.vinplay.api.processors.momo;

import com.vinplay.api.processors.cashout.GenCommentBank;
import com.vinplay.common.HttpCommon;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ELKAutoBankNew {
    public void InsertCodeUserBankELK(String nickname, String code){
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
//                RequestBody body = RequestBody.create(mediaType, "{\"nickname\":\""+nickname+"\", \"code\":\""+code+"\"}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/codeuserbank/_doc")
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
    public String GetNicknameByCode(String code){
//        try {
//            String nick = "";
//            String code1 = code.toUpperCase();
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
//                if(n == 0){
//                    nick = null;
//                }else{
//                    for (int i = 0; i < n; ++i) {
//                        final JSONObject person = jsonArray.getJSONObject(i);
//                        JSONObject test = person.getJSONObject("_source");
//                        String nickname = test.getString("nickname");
//                        nick = nickname;
//                    }
//                }
//
//            }while (check == false);
//            return nick;
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public String GetCodeByNick(String nickname){
//        try {
//            GenCommentBank gen = new GenCommentBank();
//            boolean check = false;
//            String sig = "\"successful\":1";
//            String comment_code = "";
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    break;
//                }
//                comment_code = "";
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickname.keyword\":\""+nickname+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
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
//                if(n == 0){
////                    check = false;
////                    gen.GenContent2(nickname);
//                }else{
//                    for (int i = 0; i < n; ++i) {
//                        final JSONObject person = jsonArray.getJSONObject(i);
//                        JSONObject test = person.getJSONObject("_source");
//                        String code = test.getString("code");
//                        comment_code = code;
//                    }
//                }
//
//            }while (check == false);
//            return comment_code;
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public ArrayList<String> GetListNicknameByCode(String code){
        ArrayList<String> listnick = new ArrayList<>();
//        try {
//
//            String nick = "";
//            String code1 = code.toUpperCase();
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    break;
//                }
//                listnick = new ArrayList<>();
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
//                if(n == 0){
//                    nick = null;
//                }else{
//                    for (int i = 0; i < n; ++i) {
//                        final JSONObject person = jsonArray.getJSONObject(i);
//                        JSONObject test = person.getJSONObject("_source");
//                        String nickname = test.getString("nickname");
//                        nick = nickname;
//                        listnick.add(nickname);
//                    }
//                }
//
//            }while (check == false);
//            return listnick;
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        return listnick;
    }

}
