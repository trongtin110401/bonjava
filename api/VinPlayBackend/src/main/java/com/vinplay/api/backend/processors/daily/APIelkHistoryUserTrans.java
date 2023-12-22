package com.vinplay.api.backend.processors.daily;

import com.vinplay.common.HttpCommon;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.vbee.common.response.LogAgentTranferMoneyResponse;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class APIelkHistoryUserTrans {

    public ArrayList<HistoryTransModel> GetHistoryTotal(String time_start, String time_end){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<HistoryTransModel> list_his;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_his = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"createAt.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
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

                    String giaodich = test.getString("giaodich");
                    String congGiaoDich = test.getString("congGiaoDich");
                    String hinhthuc = test.getString("hinhthuc");
                    String sotien = test.getString("sotien");
                    String trangthai = test.getString("trangthai");
                    String ghiChu = test.getString("ghiChu");
                    String nickName = test.getString("nickName");
                    String hinhthucTrans = test.getString("hinhthucTrans");
                    String transId = test.getString("transId");
                    String id = "";
                    String createAt = test.getString("createAt");
                    HistoryTransModel his = new HistoryTransModel(giaodich, congGiaoDich, hinhthuc, sotien, trangthai, ghiChu,nickName, hinhthucTrans, transId, id, createAt);

                    list_his.add(his);
                }
            }while (check == false);


            return list_his;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public ArrayList<HistoryTransModel> GetHistory(String nickname, String time_start, String time_end){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<HistoryTransModel> list_his;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_his = new ArrayList<>();
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

                    String giaodich = test.getString("giaodich");
                    String congGiaoDich = test.getString("congGiaoDich");
                    String hinhthuc = test.getString("hinhthuc");
                    String sotien = test.getString("sotien");
                    String trangthai = test.getString("trangthai");
                    String ghiChu = test.getString("ghiChu");
                    String nickName = test.getString("nickName");
                    String hinhthucTrans = test.getString("hinhthucTrans");
                    String transId = test.getString("transId");
                    String id = "";
                    String createAt = test.getString("createAt");
                    HistoryTransModel his = new HistoryTransModel(giaodich, congGiaoDich, hinhthuc, sotien, trangthai, ghiChu,nickName, hinhthucTrans, transId, id, createAt);

                    list_his.add(his);
                }
            }while (check == false);


            return list_his;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public ArrayList<LogAgentTranferMoneyResponse> GetLogByUserSend(String nickname, String time_start, String time_end){
        try{
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<LogAgentTranferMoneyResponse> list_send;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_send = new ArrayList<>();
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
                    String nick_name_send = test.getString("nick_name_send");
                    String nick_name_receive = test.getString("nick_name_receive");
                    long money_send = test.getLong("money_send");
                    long money_receive = test.getLong("money_receive");
                    int status = test.getInt("status");
                    long fee = test.getLong("fee");
                    String trans_time = test.getString("trans_time");
                    int top_ds = test.getInt("top_ds");
                    int process = test.getInt("process");
                    String des_send = test.getString("des_send");
                    String des_receive = test.getString("des_receive");
                    LogAgentTranferMoneyResponse logsend = new LogAgentTranferMoneyResponse(nick_name_send, nick_name_receive, money_send, money_receive, status, fee,trans_time, top_ds, process, des_send, des_receive);
                    list_send.add(logsend);

                }
            }while (check == false);



            return list_send;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<LogAgentTranferMoneyResponse> GetLogByUserReceive(String nickname, String time_start, String time_end){
        try{
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<LogAgentTranferMoneyResponse> list_Receive;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_Receive = new ArrayList<>();
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
                    String nick_name_send = test.getString("nick_name_send");
                    String nick_name_receive = test.getString("nick_name_receive");
                    long money_send = test.getLong("money_send");
                    long money_receive = test.getLong("money_receive");
                    int status = test.getInt("status");
                    long fee = test.getLong("fee");
                    String trans_time = test.getString("trans_time");
                    int top_ds = test.getInt("top_ds");
                    int process = test.getInt("process");
                    String des_send = test.getString("des_send");
                    String des_receive = test.getString("des_receive");
                    LogAgentTranferMoneyResponse logReceive = new LogAgentTranferMoneyResponse(nick_name_send, nick_name_receive, money_send, money_receive, status, fee,trans_time, top_ds, process, des_send, des_receive);
                    list_Receive.add(logReceive);

                }
            }while (check == false);


            return list_Receive;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<LogAgentTranferMoneyResponse> GetLogByUserByStatus(int status,String time_start, String time_end){
        try{
            boolean check = false;
            String sig = "\"successful\":1";
            ArrayList<LogAgentTranferMoneyResponse> list_send;

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                list_send = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"status\":\""+status+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
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
                    String nick_name_send = test.getString("nick_name_send");
                    String nick_name_receive = test.getString("nick_name_receive");
                    long money_send = test.getLong("money_send");
                    long money_receive = test.getLong("money_receive");
                    int status2 = test.getInt("status");
                    long fee = test.getLong("fee");
                    String trans_time = test.getString("trans_time");
                    int top_ds = test.getInt("top_ds");
                    int process = test.getInt("process");
                    String des_send = test.getString("des_send");
                    String des_receive = test.getString("des_receive");
                    LogAgentTranferMoneyResponse logsend = new LogAgentTranferMoneyResponse(nick_name_send, nick_name_receive, money_send, money_receive, status2, fee,trans_time, top_ds, process, des_send, des_receive);
                    list_send.add(logsend);

                }
            }while (check == false);



            return list_send;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
