package com.vinplay.dal.dao.impl;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.common.HttpCommon;
import com.vinplay.vbee.common.messages.LogChuyenTienDaiLyMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import jdk.nashorn.internal.runtime.Debug;
import okhttp3.*;
import org.bson.Document;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogChuyenTienDaiLyImpl  {
    public boolean addlogChuyenTienDaiLy(LogChuyenTienDaiLyMessage message) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("log_chuyen_tien_dai_ly");
        Document doc = new Document();
        String time_create = VinPlayUtils.getCurrentDateTime();
        doc.append("nick_name_send", (Object)message.getNicknameSend());
        doc.append("nick_name_receive", (Object)message.getNicknameReceive());
        doc.append("money_send", (Object)message.getMoneySend());
        doc.append("money_receive", (Object)message.getMoneyReceive());
        doc.append("status", (Object)message.getStatus());
        doc.append("fee", (Object)message.getFee());
        doc.append("trans_time", (Object)message.getTransTime());
        doc.append("top_ds", (Object)1);
        doc.append("process", (Object)0);
        doc.append("des_send", (Object)message.getDesSend());
        doc.append("des_receive", (Object)message.getDesReceive());
        doc.append("process", (Object)0);
        doc.append("create_time", time_create);
        doc.append("transaction_no", (Object)message.getTransactionId());
        doc.append("is_freeze_money", (Object)message.getIsFreezeMoney());
        doc.append("agent_level1", (Object)message.getAgentLevel1());
        doc.append("session_id_freeze_money", (Object)message.getSessionIdFreezeMoney());
        col.insertOne((Object)doc);
        
        synchronized (this){
            if(!InsertLogChuyenTienDaiLy(message, time_create)){
                InsertLogChuyenTienDaiLy(message, time_create) ; // resend when fail
            }
        }


        return true;
    }

    public void addlogChuyenTienDaiLyMySQL(LogChuyenTienDaiLyMessage message) throws SQLException {
        String sql = " INSERT INTO vinplay.log_tranfer_agent  (  transaction_no,  agent_level1,  nick_name_send,  nick_name_receive,  money_send,  money_receive,  status,  fee,  top_ds,  process,  ti_gia,  is_freeze_money,  des_send,  des_receive,  session_id_freeze_money,  trans_time,  update_time  )  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(" INSERT INTO vinplay.log_tranfer_agent  (  transaction_no,  agent_level1,  nick_name_send,  nick_name_receive,  money_send,  money_receive,  status,  fee,  top_ds,  process,  ti_gia,  is_freeze_money,  des_send,  des_receive,  session_id_freeze_money,  trans_time,  update_time  )  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            stmt.setString(1, message.getTransactionId());
            stmt.setString(2, message.getAgentLevel1());
            stmt.setString(3, message.getNicknameSend());
            stmt.setString(4, message.getNicknameReceive());
            stmt.setLong(5, message.getMoneySend());
            stmt.setLong(6, message.getMoneyReceive());
            stmt.setInt(7, message.getStatus());
            stmt.setLong(8, message.getFee());
            stmt.setInt(9, 1);
            stmt.setInt(10, 0);
            stmt.setInt(11, 0);
            stmt.setInt(12, message.getIsFreezeMoney());
            stmt.setString(13, message.getDesSend());
            stmt.setString(14, message.getDesReceive());
            stmt.setString(15, message.getSessionIdFreezeMoney());
            stmt.setString(16, message.getTransTime());
            stmt.setString(17, DateTimeUtils.getCurrentTime((String)"yyyy-MM-dd HH:mm:ss"));
            stmt.executeUpdate();
            stmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if (conn != null) {
            conn.close();
        }
    }

    public boolean InsertLogChuyenTienDaiLy(LogChuyenTienDaiLyMessage message, String time_create){
        boolean check = false;
        try {

            String sig = "\"successful\":1";


                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, " {\r\n\"_class\": \"vn.com.syncmonges.entity.log_chuyen_tien_dai_ly\",\r\n\"id\": \""+message.getTransactionId()+"\",\r\n\"nick_name_send\": \""+ message.getNicknameSend()+"\",\r\n\"nick_name_receive\": \""+ message.getNicknameReceive()+"\",\r\n\"money_send\": "+ message.getMoneySend()+",\r\n\"money_receive\": "+ message.getMoneyReceive()+",\r\n\"status\": "+message.getStatus()+",\r\n\"fee\": "+message.getFee()+",\r\n\"trans_time\": \""+message.getTransTime()+"\",\r\n\"top_ds\": 1,\r\n\"process\": 0,\r\n\"des_send\": \""+message.getDesSend()+"\",\r\n\"des_receive\": \""+message.getDesReceive()+"\",\r\n\"create_time\": \""+time_create+"\",\r\n\"transaction_no\": \""+message.getTransactionId()+"\",\r\n\"is_freeze_money\": "+message.getIsFreezeMoney()+",\r\n\"agent_level1\": \""+message.getAgentLevel1()+"\",\r\n\"session_id_freeze_money\": \""+message.getSessionIdFreezeMoney()+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/log_chuyen_tien_dai_ly/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response =  client.newCall(request).execute();
                if(response.isSuccessful()){
                    check = true;
                }



        } catch (Exception e) {
            e.printStackTrace();

        }
        return check;
    }

}
