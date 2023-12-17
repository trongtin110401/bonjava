package com.vinplay.vbee.common.rmq;

import com.vinplay.vbee.common.messages.FreezeMoneyMessage;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import com.vinplay.vbee.common.messages.MoneyMessageInGame;
import com.vinplay.vbee.common.messages.MoneyMessageInMinigame;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ELKrmq {
    public void InsertLogMoneyUserVin(LogMoneyUserMessage message, long transId, boolean isBot, boolean playGame, String time_create){
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
                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.log_money_user_vin\",\r\n\"id\": \""+transId+"\",\r\n\"trans_id\": "+transId+",\r\n\"user_id\": "+message.getUserId()+",\r\n\"nick_name\": \""+message.getNickname()+"\",\r\n\"service_name\": \""+message.getServiceName()+"\",\r\n\"current_money\": "+message.getCurrentMoney()+",\r\n\"money_exchange\": "+message.getMoneyExchange()+",\r\n\"description\": \""+message.getDescription()+"\",\r\n\"trans_time\": \""+message.getCreateTime()+"\",\r\n\"action_name\": \""+message.getActionName()+"\",\r\n\"fee\": "+message.getFee()+",\r\n\"is_bot\": "+isBot+",\r\n\"play_game\": "+playGame+",\r\n\"create_time\": \""+time_create+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_vin/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertLogTranSactionTaiXiu(long id_my, long reference_id, long user_id, String user_name, long bet_value, long bet_side, long total_prize, long total_refund, long total_exchange, int money_type,
                                           long timestamp) {
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int timeretry = 10;
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"_class\": \"vn.com.syncmonges.entity.TransactionTaiXiuEs\",\r\n    \"id_my\": " + id_my + ",\r\n    \"reference_id\": " + reference_id + ",\r\n    \"user_id\": " + user_id + ",\r\n    \"user_name\": \"" + user_name + "\",\r\n    \"bet_value\": " + bet_value + ",\r\n    \"bet_side\": " + bet_side + ",\r\n    \"total_prize\": " + total_prize + ",\r\n    \"total_refund\": " + total_refund + ",\r\n    \"total_exchange\": " + total_exchange + ",\r\n    \"money_type\": " + money_type + ",\r\n    \"timestamp\": " + timestamp + ",\r\n    \"timecreate\": " + timestamp + "\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/transaction_tai_xiu/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }
                timeretry--;
            } while (check == false && timeretry > 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertLogMoneyUserXu(LogMoneyUserMessage message, long transId, boolean isBot, boolean playGame, String time_create){
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
                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.log_money_user_vin\",\r\n\"id\": \""+transId+"\",\r\n\"trans_id\": "+transId+",\r\n\"user_id\": "+message.getUserId()+",\r\n\"nick_name\": \""+message.getNickname()+"\",\r\n\"service_name\": \""+message.getServiceName()+"\",\r\n\"current_money\": "+message.getCurrentMoney()+",\r\n\"money_exchange\": "+message.getMoneyExchange()+",\r\n\"description\": \""+message.getDescription()+"\",\r\n\"trans_time\": \""+message.getCreateTime()+"\",\r\n\"action_name\": \""+message.getActionName()+"\",\r\n\"fee\": "+message.getFee()+",\r\n\"is_bot\": "+isBot+",\r\n\"play_game\": "+playGame+",\r\n\"create_time\": \""+time_create+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_xu/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertLogMoneyUserNapVin(LogMoneyUserMessage message, long transId,String time_create){
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
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"trans_id\":"+transId+",\r\n    \"user_id\":"+message.getUserId()+",\r\n    \"nick_name\":\""+message.getNickname()+"\",\r\n    \"service_name\":\""+message.getServiceName()+"\",\r\n    \"current_money\":"+message.getCurrentMoney()+",\r\n    \"money_exchange\":"+message.getMoneyExchange()+",\r\n    \"description\":\""+message.getDescription()+"\",\r\n    \"trans_time\":\""+message.getCreateTime()+"\",\r\n    \"action_name\":\""+message.getActionName()+"\",\r\n    \"fee\":"+message.getFee()+",\r\n    \"create_time\":\""+time_create+"\"\r\n\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_nap_vin/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertLogMoneyUserTieuVin(LogMoneyUserMessage message, long transId,String time_create){
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
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"trans_id\":"+transId+",\r\n    \"user_id\":"+message.getUserId()+",\r\n    \"nick_name\":\""+message.getNickname()+"\",\r\n    \"service_name\":\""+message.getServiceName()+"\",\r\n    \"current_money\":"+message.getCurrentMoney()+",\r\n    \"money_exchange\":"+message.getMoneyExchange()+",\r\n    \"description\":\""+message.getDescription()+"\",\r\n    \"trans_time\":\""+message.getCreateTime()+"\",\r\n    \"action_name\":\""+message.getActionName()+"\",\r\n    \"fee\":"+message.getFee()+",\r\n    \"create_time\":\""+time_create+"\"\r\n\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_money_user_tieu_vin/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getTotalStakesTx(String username, long start, long end) {
        String sig = "\"successful\":1";
        boolean check = false;
        int timeretry = 10;
        do {
            try {
                long totalStakes = 0;
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"query\": {\r\n        \"bool\": {\r\n            \"must\": [\r\n                {\r\n                    \"match\": {\r\n                        \"user_name\": \""+username+"\"\r\n                    }\r\n                },\r\n                {\r\n                    \"range\": {\r\n                        \"timestamp\": {\r\n                            \"gt\": \""+start+"\",\r\n                            \"lt\": \""+end+"\"\r\n                        }\r\n                    }\r\n                }\r\n            ],\r\n            \"must_not\": [],\r\n            \"should\": []\r\n        }\r\n    },\r\n    \"from\": 0,\r\n    \"size\": 1000,\r\n    \"sort\": [],\r\n    \"aggs\": {}\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/transaction_tai_xiu/_search")
                        .method("POST", body)
                        .addHeader("Connection", "keep-alive")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                timeretry--;
                if (data.contains(sig) == true) {
                    check = true;
                }
                JSONObject obj = new JSONObject(data);
                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
                final int n = jsonArray.length();
                for (int i = 0; i < n; ++i) {
                    final JSONObject person = jsonArray.getJSONObject(i);
                    JSONObject test = person.getJSONObject("_source");
                    long total_exchange = test.getLong("total_exchange");
                    totalStakes+=Math.abs(total_exchange);
                }
                return totalStakes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (check == false && timeretry > 0);

        return 0;
    }


    public void InsertUpdateMoneyUser(MoneyMessageInMinigame message, int type, String timelog){
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
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"UserId\":"+message.getUserId()+",\r\n    \"MoneyExchange\":"+message.getMoneyExchange()+",\r\n    \"AfterMoneyUse\":"+message.getAfterMoneyUse()+",\r\n    \"AfterMoney\":"+message.getAfterMoney()+",\r\n    \"MoneyType\":\""+message.getMoneyType()+"\",\r\n    \"Fee\":"+message.getFee()+",\r\n    \"ActionName\":\""+message.getActionName()+"\",\r\n    \"MoneyVP\":"+message.getMoneyVP()+",\r\n    \"Vp\":"+message.getVp()+",\r\n    \"type\":"+type+",\r\n    \"time\":\""+timelog+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/update_money_user/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertUpdateMoneyInGame(MoneyMessageInGame message, String timelog){
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
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"SessionId\":\""+message.getSessionId()+"\",\r\n    \"UserId\":"+message.getUserId()+",\r\n    \"ActionName\":\""+message.getActionName()+"\",\r\n    \"AfterMoneyUse\":"+message.getAfterMoneyUse()+",\r\n    \"AfterMoney\":"+message.getAfterMoney()+",\r\n    \"FreezeMoney\":"+message.getFreezeMoney()+",\r\n    \"MoneyType\":\""+message.getMoneyType()+"\",\r\n    \"Fee\":"+message.getFee()+", \r\n    \"MoneyVP\":"+message.getMoneyVP()+",\r\n    \"Vp\":"+message.getVp()+",\r\n    \"time\":\""+timelog+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/update_money_in_game/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertFreezeMoney(FreezeMoneyMessage message, String timelog){
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
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"SessionId\":\""+message.getSessionId()+"\",\r\n    \"UserId\":"+message.getUserId()+",\r\n    \"GameName\":\""+message.getGameName()+"\",\r\n    \"RoomId\":\""+message.getRoomId()+"\",\r\n    \"MoneyUse\":"+message.getMoneyUse()+",\r\n    \"MoneyTotal\":"+message.getMoneyTotal()+",\r\n    \"Money\":"+message.getMoney()+",\r\n    \"MoneyType\":\""+message.getMoneyType()+"\",\r\n    \"Nickname\":\""+message.getNickname()+"\",\r\n    \"time\":\""+timelog+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/freeze_money/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertRestoreMoney(FreezeMoneyMessage message, String timelog){
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
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"SessionId\":\""+message.getSessionId()+"\",\r\n    \"UserId\":111"+message.getUserId()+",\r\n    \"MoneyUse\":"+message.getMoneyUse()+",\r\n    \"MoneyTotal\":"+message.getMoneyTotal()+",\r\n    \"Money\":"+message.getMoney()+",\r\n    \"MoneyType\":\""+message.getMoneyType()+"\",\r\n    \"time\":\""+timelog+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/restore_money/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }

            }while (check == false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertLog601(LogMoneyUserMessage message){
        ELKrmq elk = new ELKrmq();
//if (message.isBot() && Math.abs(message.getMoneyExchange()) <= 100000L) {
        if (message.isBot()) {
            //logger.info((Object)("Khong xu ly bot: " + message.getNickname() + ", money exchange= " + message.getMoneyExchange()));
        } else {
            long transId = 0L;
            int queryType = -1;
            if (message.getMoneyType().equals("vin")) {
                transId = VinPlayUtils.generateTransId();
            } else if (message.getMoneyType().equals("xu")) {
                transId = VinPlayUtils.generateTransId();
            }
            String time_creat = VinPlayUtils.getCurrentDateTime();
            if (message.getMoneyType().equals("vin")) {
                elk.InsertLogMoneyUserVin(message, transId, message.isBot(), message.isVp(),time_creat);
            } else if (message.getMoneyType().equals("xu")) {
                elk.InsertLogMoneyUserXu(message, transId, message.isBot(), message.isVp(),time_creat);
            }
            if (message.getMoneyType().equalsIgnoreCase("vin")) {
                if (message.getMoneyExchange() > 0L) {
                    if (Consts.NAP_VIN.contains(message.getActionName())) {
                        elk.InsertLogMoneyUserNapVin(message, transId, time_creat);
                    }
                } else if (Consts.TIEU_VIN.contains(message.getActionName())) {
                    elk.InsertLogMoneyUserTieuVin(message, transId, time_creat);
                }
            }
        }
    }

    public void InsertLog30(MoneyMessageInMinigame message){
        ELKrmq elk = new ELKrmq();
        String time_creat = VinPlayUtils.getCurrentDateTime();
        int type = 0;
        if (message.getActionName().equals("Bot")) {
            type = 3;
        } else if (message.getActionName().equals("RechargeByCard") || message.getActionName().equals("RechargeByVinCard") || message.getActionName().equals("RechargeByMegaCard") || message.getActionName().equals("RechargeByBank") || message.getActionName().equals("RechargeByIAP") || message.getActionName().equals("RechargeBySMS") || message.getActionName().equals("TransferMoney")
                || message.getActionName().equals(Consts.RECHARGE_BY_MOMO) || message.getActionName().equals(Consts.RECHARGE_BY_BANK)
                && message.getMoneyVP() == -1) {
            type = 1;

        } else if (message.getMoneyVP() > 0 || message.getVp() != 0) {
            type = 2;
        }
        elk.InsertUpdateMoneyUser(message, type, time_creat);

    }

    public void InsertLog40(MoneyMessageInGame message) {
        ELKrmq elk = new ELKrmq();
        String time_creat = VinPlayUtils.getCurrentDateTime();
        elk.InsertUpdateMoneyInGame(message, time_creat);
    }

    public void InsertLog41(FreezeMoneyMessage message) {
        ELKrmq elk = new ELKrmq();
        String time_creat = VinPlayUtils.getCurrentDateTime();
        elk.InsertFreezeMoney(message, time_creat);
    }

    public void InsertLog42(FreezeMoneyMessage message) {
        ELKrmq elk = new ELKrmq();
        String time_creat = VinPlayUtils.getCurrentDateTime();
        elk.InsertRestoreMoney(message, time_creat);
    }
}
