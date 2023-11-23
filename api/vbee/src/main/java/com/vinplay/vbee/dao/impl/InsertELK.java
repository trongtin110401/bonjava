package com.vinplay.vbee.dao.impl;

import com.vinplay.common.HttpCommon;
import com.vinplay.vbee.common.messages.LogChuyenTienDaiLyMessage;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class InsertELK {

public void InsertLogMoneyUserVin(LogMoneyUserMessage message, long transId, boolean isBot, boolean playGame, String time_create){
    try {
        boolean check = false;
        String sig = "\"successful\":1";
        int retry = 3;
        do{
            retry--;
            if(retry < 0) {
                break;
            }
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.log_money_user_vin\",\r\n\"id\": \""+transId+"\",\r\n\"trans_id\": "+transId+",\r\n\"user_id\": "+message.getUserId()+",\r\n\"nick_name\": \""+message.getNickname()+"\",\r\n\"service_name\": \""+message.getServiceName()+"\",\r\n\"current_money\": "+message.getCurrentMoney()+",\r\n\"money_exchange\": "+message.getMoneyExchange()+",\r\n\"description\": \""+message.getDescription()+"\",\r\n\"trans_time\": \""+message.getCreateTime()+"\",\r\n\"action_name\": \""+message.getActionName()+"\",\r\n\"fee\": "+message.getFee()+",\r\n\"is_bot\": "+isBot+",\r\n\"play_game\": "+playGame+",\r\n\"create_time\": \""+time_create+"\"\r\n}");
            Request request = new Request.Builder()
                    .url("http://127.0.0.1:9200/log_money_user_vin/_doc")
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

public void InsertLogChuyenTienDaiLy(LogChuyenTienDaiLyMessage message, String time_create){
    try {
        boolean check = false;
        String sig = "\"successful\":1";

        int retry = 3;
        do{
            retry--;
            if(retry < 0) {
                break;
            }
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, " {\r\n\"_class\": \"vn.com.syncmonges.entity.log_chuyen_tien_dai_ly\",\r\n\"id\": \""+message.getTransactionId()+"\",\r\n\"nick_name_send\": \""+ message.getNicknameSend()+"\",\r\n\"nick_name_receive\": \""+ message.getNicknameReceive()+"\",\r\n\"money_send\": "+ message.getMoneySend()+",\r\n\"money_receive\": "+ message.getMoneyReceive()+",\r\n\"status\": "+message.getStatus()+",\r\n\"fee\": "+message.getFee()+",\r\n\"trans_time\": \""+message.getTransTime()+"\",\r\n\"top_ds\": 1,\r\n\"process\": 0,\r\n\"des_send\": \""+message.getDesSend()+"\",\r\n\"des_receive\": \""+message.getDesReceive()+"\",\r\n\"create_time\": \""+time_create+"\",\r\n\"transaction_no\": \""+message.getTransactionId()+"\",\r\n\"is_freeze_money\": "+message.getIsFreezeMoney()+",\r\n\"agent_level1\": \""+message.getAgentLevel1()+"\",\r\n\"session_id_freeze_money\": \""+message.getSessionIdFreezeMoney()+"\"\r\n}");
            Request request = new Request.Builder()
                    .url("http://127.0.0.1:9200/log_chuyen_tien_dai_ly/_doc")
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


}
