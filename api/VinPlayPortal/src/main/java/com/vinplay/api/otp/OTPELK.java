package com.vinplay.api.otp;

import com.vinplay.api.entities.UserOTP;
import com.vinplay.common.HttpCommon;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class OTPELK {
    public void InsertActiveELK(UserOTP uo){
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
//                RequestBody body = RequestBody.create(mediaType, "{ \"nickname\": \""+uo.getNickname()+"\", \"username\": \""+uo.getUsername()+"\", \"phone\": \""+uo.getPhone()+"\", \"otp\": \""+uo.getOtp()+"\", \"active\": "+uo.getActive()+", \"creat_time\": "+uo.getCreat_time()+", \"active_time\": "+uo.getActive_time()+", \"turn\": "+uo.getTurn()+", \"timelog\": \""+uo.getTimelog()+"\" }");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/active/_doc/")
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

    public String GetIDActivebyNickname(String nickname){
//        try {
//            String id_elk = "";
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
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickname.keyword\":\""+nickname+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/active/_search")
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
//                    id_elk = "";
//                }else{
//                    for (int i = 0; i < n; ++i) {
//                        final JSONObject person = jsonArray.getJSONObject(i);
//                        JSONObject test = person.getJSONObject("_source");
//                         id_elk = person.getString("_id");
//
//                    }
//                }
//
//            }while (check == false);
//            return id_elk;
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public void UpdateActiveELK(UserOTP uo, String id_elk){
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
//                RequestBody body = RequestBody.create(mediaType, "{ \"nickname\": \""+uo.getNickname()+"\", \"username\": \""+uo.getUsername()+"\", \"phone\": \""+uo.getPhone()+"\", \"otp\": \""+uo.getOtp()+"\", \"active\": "+uo.getActive()+", \"creat_time\": "+uo.getCreat_time()+", \"active_time\": "+uo.getActive_time()+", \"turn\": "+uo.getTurn()+", \"timelog\": \""+uo.getTimelog()+"\" }");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/active/_doc/"+id_elk)
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

}
