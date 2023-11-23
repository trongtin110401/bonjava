/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.messages.minigame.LogRutLocMessge
 *  com.vinplay.vbee.common.messages.minigame.LogTanLocMessage
 *  com.vinplay.vbee.common.messages.minigame.ResultTaiXiuMessage
 *  com.vinplay.vbee.common.messages.minigame.ThanhDuMessage
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuDetailMessage
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdateFundMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdateLuotRutLocMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdatePotMessage
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  org.bson.Document
 */
package com.vinplay.vbee.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.messages.minigame.LogRutLocMessge;
import com.vinplay.vbee.common.messages.minigame.LogTanLocMessage;
import com.vinplay.vbee.common.messages.minigame.ResultTaiXiuMessage;
import com.vinplay.vbee.common.messages.minigame.ThanhDuMessage;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuDetailMessage;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage;
import com.vinplay.vbee.common.messages.minigame.UpdateFundMessage;
import com.vinplay.vbee.common.messages.minigame.UpdateLuotRutLocMessage;
import com.vinplay.vbee.common.messages.minigame.UpdatePotMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.dao.OverUnderDao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.Document;

public class OverUnderDaoImpl
implements OverUnderDao {
    @Override
    public boolean saveResultTaiXiu(ResultTaiXiuMessage message) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("{CALL save_result_over_under(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
        int param = 1;
        call.setLong(param++, message.referenceId);
        call.setByte(param++, (byte)message.result);
        call.setByte(param++, (byte)message.dice1);
        call.setByte(param++, (byte)message.dice2);
        call.setByte(param++, (byte)message.dice3);
        call.setLong(param++, message.totalTai);
        call.setLong(param++, message.totalXiu);
        call.setInt(param++, message.numBetTai);
        call.setInt(param++, message.numBetXiu);
        call.setLong(param++, message.totalPrize);
        call.setLong(param++, message.totalRefundTai);
        call.setLong(param++, message.totalRefundXiu);
        call.setLong(param++, message.totalRevenue);
        call.setByte(param++, (byte)message.moneyType);
        success = call.execute();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public boolean saveTransactionTaiXiu(TransactionTaiXiuMessage message) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL save_transaction_over_under(?, ?, ?, ?, ?, ?, ?, ?)");
        int param = 1;
        call.setLong(param++, message.referenceId);
        call.setInt(param++, message.userId);
        call.setString(param++, message.username);
        call.setLong(param++, message.betValue);
        call.setByte(param++, (byte)message.betSide);
        call.setLong(param++, message.prize);
        call.setLong(param++, message.refund);
        call.setByte(param++, (byte)message.moneyType);
        success = call.execute();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public boolean saveTransactionTaiXiuDetail(TransactionTaiXiuDetailMessage message) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL save_transaction_detail_over_under(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        int param = 1;
        call.setLong(param++, message.referenceId);
        call.setString(param++, message.transactionCode);
        call.setInt(param++, message.userId);
        call.setString(param++, message.username);
        call.setLong(param++, message.betValue);
        call.setInt(param++, message.betSide);
        call.setLong(param++, message.prize);
        call.setLong(param++, message.refund);
        call.setInt(param++, message.inputTime);
        call.setByte(param++, (byte)message.moneyType);
        success = call.execute();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public boolean updateTransactionTaiXiuDetail(TransactionTaiXiuDetailMessage message) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL update_transaction_over_under_detail(?, ?, ?)");
        int param = 1;
        call.setString(param++, message.transactionCode);
        call.setLong(param++, message.prize);
        call.setLong(param++, message.refund);
        success = call.execute();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public boolean updateThanhDu(ThanhDuMessage message) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL ou_update_thanh_du(?, ?, ?, ?, ?, ?)");
        int param = 1;
        call.setString(param++, message.getUsername());
        call.setInt(param++, message.getNumber());
        call.setLong(param++, message.getTotalValue());
        call.setLong(param++, message.getCurrentReferenceId());
        call.setString(param++, message.getReferences());
        call.setByte(param++, (byte)message.getType());
        success = call.execute();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public boolean updatePot(UpdatePotMessage message) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL update_pot(?, ?)");
        int param = 1;
        call.setString(param++, message.potName);
        call.setLong(param++, message.newValue);
        success = call.execute();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public void logTanLoc(LogTanLocMessage message) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeLog = df.format(new Date());
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("tan_loc_ou");
        Document doc = new Document();
        doc.append("user_name", (Object)message.username);
        doc.append("money", (Object)message.value);
        doc.append("time_log", (Object)timeLog);
        doc.append("create_time", VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
    }

    @Override
    public void logRutLoc(LogRutLocMessge message) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeLog = df.format(new Date());
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("rut_loc_ou");
        Document doc = new Document();
        doc.append("user_name", (Object)message.username);
        doc.append("money", (Object)message.prize);
        doc.append("time_request", (Object)message.timeRequest);
        doc.append("current_fund", (Object)message.currentFund);
        doc.append("time_log", (Object)timeLog);
        doc.append("create_time", VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
    }

    @Override
    public boolean updateLuotRutLoc(UpdateLuotRutLocMessage message) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL update_luot_rut_loc_ou(?, ?)");
        int param = 1;
        call.setString(param++, message.username);
        call.setLong(param++, message.soLuotThem);
        success = call.execute();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public boolean updateFund(UpdateFundMessage message) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL update_fund(?, ?)");
        int param = 1;
        call.setString(param++, message.fundName);
        call.setLong(param++, message.newValue);
        success = call.execute();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }
}

