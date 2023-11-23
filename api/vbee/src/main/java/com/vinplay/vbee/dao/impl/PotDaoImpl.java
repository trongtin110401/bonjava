/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.messages.NoHuMessage
 *  com.vinplay.vbee.common.messages.PotMessage
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  org.bson.Document
 */
package com.vinplay.vbee.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.messages.NoHuMessage;
import com.vinplay.vbee.common.messages.PotMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.dao.PotDao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.bson.Document;

public class PotDaoImpl
implements PotDao {
    @Override
    public boolean addMoneyPot(PotMessage message) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL cong_tien_hu_game_bai(?,?,?)");
        int param = 1;
        call.setString(param++, message.getPotName());
        call.setLong(param++, message.getValuePot());
        call.setLong(param++, message.getValuePotSystem());
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
    public boolean nohu(NoHuMessage message) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL no_hu_game_bai(?,?,?,?,?,?,?)");
        int param = 1;
        call.setString(param++, message.getSessionId());
        call.setInt(param++, message.getUserId());
        call.setLong(param++, message.getAfterMoneyUse());
        call.setLong(param++, message.getAfterMoney());
        call.setLong(param++, message.getFreezeMoney());
        call.setLong(param++, message.getPotValue());
        call.setString(param++, message.getPotName());
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
    public long getPotValue(String potName) throws SQLException {
        long value = 0L;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        String sql = "SELECT value FROM hu_game_bai WHERE pot_name=?";
        PreparedStatement stm = conn.prepareStatement("SELECT value FROM hu_game_bai WHERE pot_name=?");
        stm.setString(1, potName);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            value = rs.getLong("value");
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return value;
    }

    @Override
    public boolean logHuGameBai(PotMessage message) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("log_hu_game_bai");
        Document doc = new Document();
        doc.append("pot_name", (Object)message.getPotName());
        doc.append("money_exchange", (Object)message.getMoneyExchange());
        doc.append("value", (Object)message.getValuePot());
        doc.append("value_pot_system", (Object)message.getValuePotSystem());
        doc.append("time_log", (Object)message.getCreateTime());
        doc.append("create_time", VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
        return true;
    }
}

