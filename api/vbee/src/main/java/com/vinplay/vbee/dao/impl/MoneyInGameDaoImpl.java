/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.result.UpdateResult
 *  com.vinplay.vbee.common.messages.FreezeMoneyMessage
 *  com.vinplay.vbee.common.messages.MoneyMessageInGame
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.vbee.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.vinplay.vbee.common.messages.FreezeMoneyMessage;
import com.vinplay.vbee.common.messages.MoneyMessageInGame;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import com.vinplay.vbee.dao.MoneyInGameDao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MoneyInGameDaoImpl
implements MoneyInGameDao {
    @Override
    public boolean freezeMoneyInGame(FreezeMoneyMessage message) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        CallableStatement call = null;
        call = conn.prepareCall("CALL freeze_money(?,?,?,?,?,?,?,?,?)");
        int param = 1;
        call.setString(param++, message.getSessionId());
        call.setInt(param++, message.getUserId());
        call.setString(param++, message.getGameName());
        call.setString(param++, message.getRoomId());
        call.setLong(param++, message.getMoneyUse());
        call.setLong(param++, message.getMoneyTotal());
        call.setLong(param++, message.getMoney());
        call.setString(param++, message.getMoneyType());
        call.setString(param++, message.getNickname());
        call.executeUpdate();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return true;
    }

    @Override
    public boolean updateTranferAgent(String id, int isFreezeMoney, int topDsFreeze, String sessionIdFreezeMoney) throws SQLException {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("is_freeze_money", (Object)isFreezeMoney);
        updateFields.append("session_id_freeze_money", (Object)sessionIdFreezeMoney);
        db.getCollection("log_chuyen_tien_dai_ly").updateOne((Bson)new Document("transaction_no", (Object)id), (Bson)new Document("$set", (Object)updateFields));
        return true;
    }

    @Override
    public void updateTranferAgentMySQL(String id, int isFreezeMoney, int topDsFreeze, String sessionIdFreezeMoney) throws SQLException {
        String sql = " UPDATE vinplay.log_tranfer_agent  SET is_freeze_money = ?,  session_id_freeze_money = ?,  update_time = ?  WHERE transaction_no = ? ";
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(" UPDATE vinplay.log_tranfer_agent  SET is_freeze_money = ?,  session_id_freeze_money = ?,  update_time = ?  WHERE transaction_no = ? ");
            stmt.setInt(1, isFreezeMoney);
            stmt.setString(2, sessionIdFreezeMoney);
            stmt.setString(3, DateTimeUtils.getCurrentTime((String)"yyyy-MM-dd HH:mm:ss"));
            stmt.setString(4, id);
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

    @Override
    public boolean restoreMoneyInGame(FreezeMoneyMessage message) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        CallableStatement call = null;
        try {
            call = conn.prepareCall("CALL restore_money(?,?,?,?,?,?)");
            int param = 1;
            call.setString(param++, message.getSessionId());
            call.setInt(param++, message.getUserId());
            call.setLong(param++, message.getMoneyUse());
            call.setLong(param++, message.getMoneyTotal());
            call.setLong(param++, message.getMoney());
            call.setString(param++, message.getMoneyType());
            call.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return true;
    }

    @Override
    public boolean updateMoneyInGame(MoneyMessageInGame message) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        CallableStatement call = null;
        call = conn.prepareCall("CALL update_money_in_game(?,?,?,?,?,?,?,?,?,?)");
        int param = 1;
        call.setString(param++, message.getSessionId());
        call.setInt(param++, message.getUserId());
        call.setString(param++, message.getActionName());
        call.setLong(param++, message.getAfterMoneyUse());
        call.setLong(param++, message.getAfterMoney());
        call.setLong(param++, message.getFreezeMoney());
        call.setString(param++, message.getMoneyType());
        call.setLong(param++, message.getFee());
        call.setInt(param++, message.getMoneyVP());
        call.setInt(param++, message.getVp());
        call.executeUpdate();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return true;
    }
}

