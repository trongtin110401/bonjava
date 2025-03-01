package com.vinplay.vbee.rmq;

import com.vinplay.common.HttpCommon;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiu;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.dao.impl.LogMoneyUserDaoImpl;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ELKrmq {

    public void InsertLogMoneyUserVin(LogMoneyUserMessage message, long transId, boolean isBot, boolean playGame, String time_create) {
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
//                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.log_money_user_vin\",\r\n\"id\": \"" + transId + "\",\r\n\"trans_id\": " + transId + ",\r\n\"user_id\": " + message.getUserId() + ",\r\n\"nick_name\": \"" + message.getNickname() + "\",\r\n\"service_name\": \"" + message.getServiceName() + "\",\r\n\"current_money\": " + message.getCurrentMoney() + ",\r\n\"money_exchange\": " + message.getMoneyExchange() + ",\r\n\"description\": \"" + message.getDescription() + "\",\r\n\"trans_time\": \"" + message.getCreateTime() + "\",\r\n\"action_name\": \"" + message.getActionName() + "\",\r\n\"fee\": " + message.getFee() + ",\r\n\"is_bot\": " + isBot + ",\r\n\"play_game\": " + playGame + ",\r\n\"create_time\": \"" + time_create + "\"\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_doc")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//
//            } while (check == false);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void InsertLogTranSactionTaiXiu(long id_my, long reference_id, long user_id, String user_name, long bet_value, long bet_side, long total_prize, long total_refund, long total_exchange, int money_type,
                                           long timestamp) {
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int timeretry = 10;
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    break;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"_class\": \"vn.com.syncmonges.entity.TransactionTaiXiuEs\",\r\n    \"id_my\": " + id_my + ",\r\n    \"reference_id\": " + reference_id + ",\r\n    \"user_id\": " + user_id + ",\r\n    \"user_name\": \"" + user_name + "\",\r\n    \"bet_value\": " + bet_value + ",\r\n    \"bet_side\": " + bet_side + ",\r\n    \"total_prize\": " + total_prize + ",\r\n    \"total_refund\": " + total_refund + ",\r\n    \"total_exchange\": " + total_exchange + ",\r\n    \"money_type\": " + money_type + ",\r\n    \"timestamp\": " + timestamp + ",\r\n    \"timecreate\": " + timestamp + "\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/transaction_tai_xiu/_doc")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//                timeretry--;
//            } while (check == false && timeretry > 0);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public List<TransactionTaiXiu> getTransactionTaiXiuEs(String username, long start, long end) {
//        String sig = "\"successful\":1";
//        boolean check = false;
//        int timeretry = 10;
//        List<TransactionTaiXiu> list = new ArrayList<>();
//        int retry = 3;
//        do{
//            retry--;
//            if(retry < 0) {
//                break;
//            }
//            try {
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                list = new ArrayList<>();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"query\": {\r\n        \"bool\": {\r\n            \"must\": [\r\n                {\r\n                    \"match\": {\r\n                        \"user_name\": \"thanhdung1992\"\r\n                    }\r\n                },\r\n                {\r\n                    \"range\": {\r\n                        \"timestamp\": {\r\n                            \"gt\": \"1547303203000\",\r\n                            \"lt\": \"1747303203000\"\r\n                        }\r\n                    }\r\n                }\r\n            ],\r\n            \"must_not\": [],\r\n            \"should\": []\r\n        }\r\n    },\r\n    \"from\": 0,\r\n    \"size\": 1000,\r\n    \"sort\": [],\r\n    \"aggs\": {}\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/transaction_tai_xiu/_search")
//                        .method("POST", body)
//                        .addHeader("Connection", "keep-alive")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                timeretry--;
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject person = jsonArray.getJSONObject(i);
//                    JSONObject test = person.getJSONObject("_source");
//
//                    long reference_id = test.getLong("reference_id");
//
//                    String nick_name = test.getString("user_name");
//                    long bet_value = test.getLong("bet_value");
//                    int bet_side = test.getInt("bet_side");
//                    long total_prize = test.getLong("total_prize");
//                    long total_refund = test.getLong("total_refund");
//                    long total_exchange = test.getLong("total_exchange");
//                    int money_type = test.getInt("money_type");
//                    long timestamp = test.getLong("timestamp");
//                    long timecreate = test.getLong("timecreate");
//
//
//                    TransactionTaiXiu transactionTaiXiu = new TransactionTaiXiu();
//                    transactionTaiXiu.referenceId = reference_id;
//                    transactionTaiXiu.username = nick_name;
//                    transactionTaiXiu.betValue = bet_value;
//                    transactionTaiXiu.betSide = bet_side;
//                    transactionTaiXiu.totalPrize = total_prize;
//                    transactionTaiXiu.totalRefund = total_refund;
//                    transactionTaiXiu.totalExchange = total_exchange;
//                    transactionTaiXiu.moneyType = money_type;
//                    transactionTaiXiu.timestamp = String.valueOf(new Timestamp(timestamp));
//                    list.add(transactionTaiXiu);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } while (check == false && timeretry > 0);

        return null;
    }


    public void InsertLogMoneyUserXu(LogMoneyUserMessage message, long transId, boolean isBot, boolean playGame, String time_create) {
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
//                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.log_money_user_vin\",\r\n\"id\": \"" + transId + "\",\r\n\"trans_id\": " + transId + ",\r\n\"user_id\": " + message.getUserId() + ",\r\n\"nick_name\": \"" + message.getNickname() + "\",\r\n\"service_name\": \"" + message.getServiceName() + "\",\r\n\"current_money\": " + message.getCurrentMoney() + ",\r\n\"money_exchange\": " + message.getMoneyExchange() + ",\r\n\"description\": \"" + message.getDescription() + "\",\r\n\"trans_time\": \"" + message.getCreateTime() + "\",\r\n\"action_name\": \"" + message.getActionName() + "\",\r\n\"fee\": " + message.getFee() + ",\r\n\"is_bot\": " + isBot + ",\r\n\"play_game\": " + playGame + ",\r\n\"create_time\": \"" + time_create + "\"\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_xu/_doc")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//
//            } while (check == false);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void InsertLogMoneyUserNapVin(LogMoneyUserMessage message, long transId, String time_create) {
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
//                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"trans_id\":" + transId + ",\r\n    \"user_id\":" + message.getUserId() + ",\r\n    \"nick_name\":\"" + message.getNickname() + "\",\r\n    \"service_name\":\"" + message.getServiceName() + "\",\r\n    \"current_money\":" + message.getCurrentMoney() + ",\r\n    \"money_exchange\":" + message.getMoneyExchange() + ",\r\n    \"description\":\"" + message.getDescription() + "\",\r\n    \"trans_time\":\"" + message.getCreateTime() + "\",\r\n    \"action_name\":\"" + message.getActionName() + "\",\r\n    \"fee\":" + message.getFee() + ",\r\n    \"create_time\":\"" + time_create + "\"\r\n\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_nap_vin/_doc")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//
//            } while (check == false);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void InsertLogMoneyUserTieuVin(LogMoneyUserMessage message, long transId, String time_create) {
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
//                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"trans_id\":" + transId + ",\r\n    \"user_id\":" + message.getUserId() + ",\r\n    \"nick_name\":\"" + message.getNickname() + "\",\r\n    \"service_name\":\"" + message.getServiceName() + "\",\r\n    \"current_money\":" + message.getCurrentMoney() + ",\r\n    \"money_exchange\":" + message.getMoneyExchange() + ",\r\n    \"description\":\"" + message.getDescription() + "\",\r\n    \"trans_time\":\"" + message.getCreateTime() + "\",\r\n    \"action_name\":\"" + message.getActionName() + "\",\r\n    \"fee\":" + message.getFee() + ",\r\n    \"create_time\":\"" + time_create + "\"\r\n\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_tieu_vin/_doc")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//
//            } while (check == false);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void InsertLog601(LogMoneyUserMessage message) {
//        ELKrmq elk = new ELKrmq();
////if (message.isBot() && Math.abs(message.getMoneyExchange()) <= 100000L) {
//        if (message.isBot()) {
//            //logger.info((Object)("Khong xu ly bot: " + message.getNickname() + ", money exchange= " + message.getMoneyExchange()));
//        } else {
//            LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
//            long transId = 0L;
//            int queryType = -1;
//            if (message.getMoneyType().equals("vin")) {
//                transId = ++com.vinplay.vbee.main.VBeeMain.moneyVinReferenceId;
//            } else if (message.getMoneyType().equals("xu")) {
//                transId = ++com.vinplay.vbee.main.VBeeMain.moneyXuReferenceId;
//            }
//            String time_creat = VinPlayUtils.getCurrentDateTime();
//            if (message.getMoneyType().equals("vin")) {
//                elk.InsertLogMoneyUserVin(message, transId, message.isBot(), message.isVp(), time_creat);
//            } else if (message.getMoneyType().equals("xu")) {
//                elk.InsertLogMoneyUserXu(message, transId, message.isBot(), message.isVp(), time_creat);
//            }
//            if (message.getMoneyType().equalsIgnoreCase("vin")) {
//                if (message.getMoneyExchange() > 0L) {
//                    if (Consts.NAP_VIN.contains(message.getActionName())) {
//                        elk.InsertLogMoneyUserNapVin(message, transId, time_creat);
//                    }
//                } else if (Consts.TIEU_VIN.contains(message.getActionName())) {
//                    elk.InsertLogMoneyUserTieuVin(message, transId, time_creat);
//                }
//            }
//        }
    }

}
