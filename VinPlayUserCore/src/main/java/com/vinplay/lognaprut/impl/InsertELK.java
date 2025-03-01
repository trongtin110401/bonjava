package com.vinplay.lognaprut.impl;

import com.vinplay.common.HttpCommon;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class InsertELK {

    public void InsertHistoryUserTrans(HistoryTransModel historyTransModel){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    return;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, " {\r\n\"_class\": \"vn.com.syncmonges.entity.HistoryUserTransaction\",\r\n\"id\": \""+historyTransModel.getId()+"\",\r\n\"giaodich\": \""+historyTransModel.getGiaodich()+"\",\r\n\"congGiaoDich\": \""+historyTransModel.getCongGiaoDich()+"\",\r\n\"hinhthuc\": \""+historyTransModel.getHinhthuc()+"\",\r\n\"sotien\": \""+historyTransModel.getSotien()+"\",\r\n\"trangthai\": \" "+historyTransModel.getTrangthai()+"\",\r\n\"ghiChu\": \" "+historyTransModel.getGhiChu()+"\",\r\n\"nickName\": \""+historyTransModel.getNickName()+"\",\r\n\"hinhthucTrans\": \""+historyTransModel.getHinhthucTrans()+"\",\r\n\"transId\": \""+historyTransModel.getTransId()+"\",\r\n\"createAt\": \""+historyTransModel.getCreateAt()+"\"\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_doc")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//
//            }while (check == false);
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }


    public void InsertHistoryUserTransOK(HistoryTransModel historyTransModel, long idelk, String timeAt){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int count = 0;
//            do{
//                count++;
//                if(count > 3) {
//                    break;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.HistoryUserTransaction\",\r\n\"id\": \""+idelk+"\",\r\n\"giaodich\": \""+historyTransModel.getGiaodich()+"\",\r\n\"congGiaoDich\": \""+historyTransModel.congGiaoDich+"\",\r\n\"hinhthuc\": \""+historyTransModel.hinhthuc+"\",\r\n\"sotien\": \""+historyTransModel.sotien+"\",\r\n\"trangthai\": \""+historyTransModel.trangthai+"\",\r\n\"ghiChu\": \""+historyTransModel.ghiChu+"\",\r\n\"nickName\": \""+historyTransModel.nickName+"\",\r\n\"hinhthucTrans\": \""+historyTransModel.hinhthucTrans+"\",\r\n\"transId\": \""+historyTransModel.transId+"\",\r\n\"createAt\": \""+timeAt+"\"\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_doc/"+idelk)
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//
//            }while (check == false);
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public HistoryTransModel GetHistorybyTransID(String TranID){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            HistoryTransModel his = null;
//
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    return null;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"transId.keyword\":\""+TranID+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":50,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_search")
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
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject person = jsonArray.getJSONObject(i);
//                    JSONObject test = person.getJSONObject("_source");
//                    String nickName = test.getString("nickName");
//                    String id = test.getString("id");
//                    String giaodich = test.getString("giaodich");
//                    String congGiaoDich = test.getString("congGiaoDich");
//                    String hinhthuc = test.getString("hinhthuc");
//                    String sotien = test.getString("sotien");
//                    String trangthai = test.getString("trangthai");
//                    String ghiChu = test.getString("ghiChu");
//                    String hinhthucTrans = test.getString("hinhthucTrans");
//                    String transId = test.getString("transId");
//                    String createAt = test.getString("createAt");
//                    his = new HistoryTransModel(giaodich, congGiaoDich, hinhthuc, sotien, trangthai, ghiChu, nickName, hinhthucTrans, transId, id, createAt);
//                }
//
//            }while (check == false);
//            return his;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return null;

    }






}
