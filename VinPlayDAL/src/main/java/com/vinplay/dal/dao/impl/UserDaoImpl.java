/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.vippoint.UserVPEventModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.response.userMission.LogReceivedRewardObj
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  com.vinplay.vbee.common.utils.UserUtil
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.dao.UserDao;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.vippoint.UserVPEventModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.response.userMission.LogReceivedRewardObj;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import com.vinplay.vbee.common.utils.UserUtil;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDaoImpl
        implements UserDao {

    @Override
    public UserModel getUserByNickName(String nickname) throws SQLException {
        String sql = "SELECT * FROM users WHERE nick_name=?";
        UserModel user = null;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname"); PreparedStatement stm = conn.prepareStatement(sql);) {
            stm.setString(1, nickname);
            try (ResultSet rs = stm.executeQuery();) {
                if (rs.next()) {
                    user = UserUtil.parseResultSetToUserModel((ResultSet) rs);
                }
            }
        }
        return user;
    }

    public UserModel getUserByUserName(String nickname) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_name=?";
        UserModel user = null;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname"); PreparedStatement stm = conn.prepareStatement("SELECT * FROM users WHERE user_name=?");) {
            stm.setString(1, nickname);
            try (ResultSet rs = stm.executeQuery();) {
                if (rs.next()) {
                    user = UserUtil.parseResultSetToUserModel((ResultSet) rs);
                }
            }
        }
        return user;
    }

    @Override
    public boolean updateRechargeMoney(String nickname, long money) throws SQLException {
        String sql = "UPDATE users SET recharge_money=? WHERE nick_name=?";
        boolean res = false;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname"); PreparedStatement stm = conn.prepareStatement("UPDATE users SET recharge_money=? WHERE nick_name=?");) {
            stm.setLong(1, money);
            stm.setString(2, nickname);
            if (stm.executeUpdate() == 1) {
                boolean bl = true;
                return bl;
            }
        }
        return false;
    }

    @Override
    public UserVPEventModel getUserVPByNickName(String nickname) throws SQLException {
        String sql = "SELECT * FROM users_vp_event WHERE nick_name=?";
        UserVPEventModel user = new UserVPEventModel();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname"); PreparedStatement stm = conn.prepareStatement("SELECT * FROM users_vp_event WHERE nick_name=?");) {
            stm.setString(1, nickname);
            try (ResultSet rs = stm.executeQuery();) {
                if (rs.next()) {
                    user = UserUtil.parseResultSetToUserVPEventModel((ResultSet) rs);
                }
            }
        }
        return user;
    }

    @Override
    public boolean updateUserMission(String nickName, String missionName, String moneyType, int matchWin) throws
            SQLException {
        String tableName = "";
        tableName = moneyType.equals("vin") ? "user_mission_vin" : "user_mission_xu";
        String sql = " UPDATE " + tableName + " SET match_win = ?,      update_time = ?  WHERE nick_name = ?    AND mission_name = ? ";
        boolean success = false;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname"); PreparedStatement stm = conn.prepareStatement(sql);) {
            stm.setInt(1, matchWin);
            stm.setString(2, DateTimeUtils.getCurrentTime());
            stm.setString(3, nickName);
            stm.setString(4, missionName);
            stm.executeUpdate();
        }
        return false;
    }

    @Override
    public List<LogReceivedRewardObj> getLogReceivedReward(String nickName, String gameName, String
            moneyType, String timeStart, String timeEnd, int page) throws SQLException {
        final ArrayList<LogReceivedRewardObj> results = new ArrayList<LogReceivedRewardObj>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        int numStart = (page - 1) * 50;
        int numEnd = 50;
        objsort.put("_id", -1);
        if (nickName != null && !nickName.equals("")) {
            conditions.put("nick_name", nickName);
        }
        if (gameName != null && !gameName.equals("")) {
            conditions.put("game_name", gameName);
        }
        if (moneyType != null && !moneyType.equals("")) {
            conditions.put("money_type", moneyType);
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("time_log", obj);
        }
        FindIterable iterable = db.getCollection("log_received_reward_mission").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(50);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogReceivedRewardObj obj = new LogReceivedRewardObj(document.getInteger((Object) "user_id", 0), document.getString((Object) "nick_name"), document.getString((Object) "game_name"), document.getInteger((Object) "level_received_reward", 0), document.getLong((Object) "money_bonus").longValue(), document.getLong((Object) "money_user").longValue(), document.getString((Object) "money_type"), document.getString((Object) "time_log"));
                results.add(obj);
            }
        });
        return results;
    }

}

