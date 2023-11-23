/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.models.cache.ReportModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.bson.Document
 */
package com.vinplay.vbee.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.models.cache.ReportModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.dao.ReportDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import org.bson.Document;

public class ReportDaoImpl
implements ReportDao {
    @Override
    public boolean saveLogMoneyForReport(String nickname, String actioname, String date, ReportModel model, boolean isBot) throws ParseException {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = null;
        col = !isBot ? db.getCollection("report_money_vin") : db.getCollection("report_money_vin_bot");
        Document doc = new Document();
        doc.append("nick_name", (Object)nickname);
        doc.append("action_name", (Object)actioname);
        doc.append("date", (Object)date);
        doc.append("money_win", (Object)model.moneyWin);
        doc.append("money_lost", (Object)model.moneyLost);
        doc.append("money_other", (Object)model.moneyOther);
        doc.append("fee", (Object)model.fee);
        doc.append("update_time", (Object)VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
        return true;
    }

    @Override
    public boolean checkBot(String nickname) throws SQLException {
        boolean res = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String sql = "SELECT is_bot FROM users WHERE nick_name=?";
        PreparedStatement stm = conn.prepareStatement("SELECT is_bot FROM users WHERE nick_name=?");
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        if (rs.next() && rs.getInt("is_bot") == 1) {
            res = true;
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return res;
    }
}

