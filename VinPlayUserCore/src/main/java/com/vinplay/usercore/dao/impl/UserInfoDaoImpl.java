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
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.response.GetUserInfoResponse
 *  com.vinplay.vbee.common.response.UserInfoResponse
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.usercore.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.usercore.dao.UserInfoDao;
import com.vinplay.usercore.entities.ExportUser;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.response.GetUserInfoResponse;
import com.vinplay.vbee.common.response.UserInfoResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class UserInfoDaoImpl
implements UserInfoDao {
    @Override
    public List<UserInfoResponse> searchUserInfo(String nickName, String ip, String startDate, String endDate, String type, int page) {
        final ArrayList<UserInfoResponse> result = new ArrayList<UserInfoResponse>();
        int num_start = (page - 1) * 50;
        int num_end = 50;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        if (nickName != null && !nickName.equals("")) {
            conditions.put("nick_name", nickName);
        }
        if (ip != null && !ip.equals("")) {
            conditions.put("ip", ip);
        }
        if (type != null && !type.equals("")) {
            conditions.put("type", Integer.parseInt(type));
        }
        conditions.put("agent", new Document("$ne", ""));
        if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")) {
            obj.put("$gte", (Object)startDate);
            obj.put("$lte", (Object)endDate);
            conditions.put("time_log", (Object)obj);
        }
        final UserServiceImpl service = new UserServiceImpl();
        FindIterable iterable = db.getCollection("user_login_info").find((Bson)new Document(conditions)).skip(num_start).limit(50).sort((Bson)objsort);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                String nn = document.getString((Object)"nick_name");
                UserInfoResponse userinfo = new UserInfoResponse();
                userinfo.user_id = document.getInteger((Object)"user_id");
                userinfo.user_name = document.getString((Object)"user_name");
                userinfo.nick_name = document.getString((Object)"nick_name");
                userinfo.ip = document.getString((Object)"ip");
                userinfo.agent = document.getString((Object)"agent");
                userinfo.type = document.getInteger((Object)"type");
                userinfo.time_log = document.getString((Object)"time_log");
                try {
                    UserModel model = service.getUserByNickName(nn);
                    userinfo.security = model.isHasMobileSecurity();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                result.add(userinfo);
            }
        });
        return result;
    }

    @Override
    public int countsearchUserInfo(String nickName, String ip, String startDate, String endDate, String type) {
        final ArrayList result = new ArrayList();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        if (nickName != null && !nickName.equals("")) {
            conditions.put("nick_name", nickName);
        }
        if (ip != null && !ip.equals("")) {
            conditions.put("ip", ip);
        }
        if (type != null && !type.equals("")) {
            conditions.put("type", Integer.parseInt(type));
        }
        if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")) {
            obj.put("$gte", (Object)startDate);
            obj.put("$lte", (Object)endDate);
            conditions.put("time_log", (Object)obj);
        }
        FindIterable iterable = db.getCollection("user_login_info").find((Bson)new Document(conditions)).sort((Bson)objsort);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                UserInfoResponse userinfo = new UserInfoResponse();
                userinfo.user_id = document.getInteger((Object)"user_id");
                userinfo.user_name = document.getString((Object)"user_name");
                userinfo.nick_name = document.getString((Object)"nick_name");
                userinfo.ip = document.getString((Object)"ip");
                userinfo.agent = document.getString((Object)"agent");
                userinfo.type = document.getInteger((Object)"type");
                userinfo.time_log = document.getString((Object)"time_log");
                result.add(userinfo);
            }
        });
        return result.size();
    }

    @Override
    public GetUserInfoResponse listGetNickName(String nickName) throws SQLException {
        GetUserInfoResponse userinfo = new GetUserInfoResponse();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT * FROM users WHERE nick_name=?";
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM users WHERE nick_name=?");
            stm.setString(1, nickName);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                userinfo.nick_name = rs.getString("nick_name");
                userinfo.user_name = rs.getString("user_name");
                userinfo.ip = "";
                userinfo.time_log = rs.getString("create_time");
                userinfo.phone = rs.getString("mobile");
            } else {
                userinfo.nick_name = nickName;
            }
            rs.close();
            stm.close();
        }
        return userinfo;
    }

    @Override
    public List<ExportUser> GetExportUser(String startDate, String endDate) throws SQLException {
        List<ExportUser> users = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT nick_name,mobile,recharge_money FROM vinplay.users where create_time >= ? and create_time <= ? and is_bot = 0 and dai_ly = 0 and mobile is not null and recharge_money > 0";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, startDate);
            stm.setString(2, endDate);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                ExportUser user = new ExportUser();
                user.setNick_name(rs.getString("nick_name"));
                user.setMobile(rs.getString("mobile"));       
                user.setRecharge_money(rs.getLong("recharge_money"));
                users.add(user);
            }
            rs.close();
            stm.close();
        }
        return users;
    }
}

