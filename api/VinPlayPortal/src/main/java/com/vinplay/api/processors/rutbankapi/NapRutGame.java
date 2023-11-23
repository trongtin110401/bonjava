package com.vinplay.api.processors.rutbankapi;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.common.HttpCommon;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import okhttp3.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class NapRutGame {
    public boolean OnOffAutoRut(){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("AutoRutBank");
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("Check", "1");
            Document doccument = (Document) col.find((Bson) new Document(conditions)).first();
            if(doccument == null){
                return false;
            }else{
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void NapRut(NapRutModel nap){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("NapRutGame");
            Document doc = new Document();
            doc.append("TranID", (Object)nap.getTranID());
            doc.append("nick_name", (Object)nap.getNickName());
            doc.append("MaDaily", (Object)nap.getMaDaily());
            doc.append("SoTien", (Object)nap.getSoTien());
            doc.append("HinhThucTran", (Object)nap.getHinhThucTran());
            doc.append("CreateAt", (Object)nap.getCreateAt());
            col.insertOne((Object)doc);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMaDaily1(String nickname){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            String codedl = "";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    break;
                }
                codedl = "";
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\""+nickname+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
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
                    codedl = test.getString("id_daily");
                }
            }while (check == false);
            return codedl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getTransID(String transID){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("NapRutGame");
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("TranID", transID);
            Document doccument = (Document) col.find((Bson) new Document(conditions)).first();
            if(doccument == null){
                return false;
            }else{
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getMaDaily(String nicknamex){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("user_map_daily");
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("nickName", nicknamex);
            Document doccument = (Document) col.find((Bson) new Document(conditions)).first();
            if(doccument == null){
                return null;
            }else{
                return doccument.getString("id_daily");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
