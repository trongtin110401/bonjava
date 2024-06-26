/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.mongodb.BasicDBList
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.AggregateIterable
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.result.UpdateResult
 *  com.vinplay.vbee.common.messages.dvt.RefundFeeAgentMessage
 *  com.vinplay.vbee.common.models.AgentModel
 *  com.vinplay.vbee.common.models.cache.AgentDSModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.response.AgentResponse
 *  com.vinplay.vbee.common.response.LogAgentTranferMoneyResponse
 *  com.vinplay.vbee.common.response.TranferAgentResponse
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.vinplay.dal.dao.AgentDAO;
import com.vinplay.dal.entities.agent.BonusTopDSModel;
import com.vinplay.dal.entities.agent.TranferMoneyAgent;
import com.vinplay.vbee.common.messages.dvt.RefundFeeAgentMessage;
import com.vinplay.vbee.common.models.AgentModel;
import com.vinplay.vbee.common.models.cache.AgentDSModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogAgentTranferMoneyResponse;
import com.vinplay.vbee.common.response.TranferAgentResponse;
import com.vinplay.vbee.common.utils.DateTimeUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.bson.Document;
import org.bson.conversions.Bson;

public class AgentDAOImpl
        implements AgentDAO {
    @Override
    public List<AgentResponse> listAgent() throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");
        String sql = "SELECT nameagent,nickname,phone,address,parentid,`show`,`active`,`order`,`facebook` FROM useragent WHERE status='D' and parentid=-1 and `show`=1 and active=1 order by `order` asc";
        PreparedStatement stmt = conn.prepareStatement("SELECT nameagent,nickname,phone,address,parentid,`show`,`active`,`order`,`facebook` FROM useragent WHERE status='D' and parentid=-1 and `show`=1 and active=1 order by `order` asc");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            AgentResponse agent = new AgentResponse();
            agent.fullName = rs.getString("nameagent");
            agent.nickName = rs.getString("nickname");
            agent.mobile = rs.getString("phone");
            agent.address = rs.getString("address");
            agent.parentid = rs.getInt("parentid");
            agent.show = rs.getInt("show");
            agent.active = rs.getInt("active");
            agent.orderNo = rs.getInt("order");
            agent.facebook = rs.getString("facebook");
            results.add(agent);
        }
        rs.close();
        stmt.close();
        if (conn != null) {
            conn.close();
        }
        return results;
    }

    @Override
    public List<AgentResponse> listAgentByClient(String client, String agentType) throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");
        PreparedStatement stmt = conn.prepareStatement("SELECT nameagent,nickname,phone,address,parentid,`show`,`active`,`order`,`facebook`,`site`" +
                " FROM useragent WHERE status='D' and parentid=-1 and `show`=1 and active=1 and site = '" + client + "' and address = '" + agentType + "' order by `order` asc");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            AgentResponse agent = new AgentResponse();
            String site = rs.getString("site");
            if (site != null && !site.equals(""))
                agent.fullName = "[" + site + "] " + rs.getString("nameagent");
            else
                agent.fullName = rs.getString("nameagent");
            agent.fullName = rs.getString("nameagent");
            agent.nickName = rs.getString("nickname");
            agent.mobile = rs.getString("phone");
            agent.address = rs.getString("address");
            agent.parentid = rs.getInt("parentid");
            agent.show = rs.getInt("show");
            agent.active = rs.getInt("active");
            agent.orderNo = rs.getInt("order");
            agent.facebook = rs.getString("facebook");
            results.add(agent);
        }
        rs.close();
        stmt.close();
        if (conn != null) {
            conn.close();
        }
        return results;
    }

    @Override
    public AgentResponse listAgentByKey(String key) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");
        PreparedStatement stmt = conn.prepareStatement("SELECT nameagent,nickname,phone,address,parentid,`show`,`active`,`order`,`facebook`,`site` FROM useragent WHERE `key` = '" + key + "' order by `order` asc");
        ResultSet rs = stmt.executeQuery();
        AgentResponse agent = null;
        while (rs.next()) {
            agent = new AgentResponse();
            agent.fullName = rs.getString("nameagent");
            agent.nickName = rs.getString("nickname");
            agent.mobile = rs.getString("phone");
            agent.address = rs.getString("address");
            agent.parentid = rs.getInt("parentid");
            agent.show = rs.getInt("show");
            agent.active = rs.getInt("active");
            agent.orderNo = rs.getInt("order");
            agent.facebook = rs.getString("facebook");
        }
        rs.close();
        stmt.close();
        if (conn != null) {
            conn.close();
        }
        return agent;
    }

    @Override
    public List<LogAgentTranferMoneyResponse> searchAgentTranferMoney(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String top_ds, int page) {
        final ArrayList<LogAgentTranferMoneyResponse> results = new ArrayList<LogAgentTranferMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        int numStart = (page - 1) * 50;
        int numEnd = 50;
        Object v2 = -1;
        objsort.put("_id", v2);
//        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("") || nickNameRecieve != null && !nickNameRecieve.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickNameSend);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickNameRecieve);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (top_ds != null && !top_ds.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(top_ds)));
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", obj);
        }
        iterable = db.getCollection("log_chuyen_tien_dai_ly").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(50);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogAgentTranferMoneyResponse trans = new LogAgentTranferMoneyResponse();
                trans.nick_name_send = document.getString((Object) "nick_name_send");
                trans.nick_name_receive = document.getString((Object) "nick_name_receive");
                trans.status = document.getInteger((Object) "status");
                trans.trans_time = document.getString((Object) "trans_time");
                trans.fee = document.getLong((Object) "fee");
                trans.money_send = document.getLong((Object) "money_send");
                trans.money_receive = document.getLong((Object) "money_receive");
                trans.top_ds = document.getInteger((Object) "top_ds");
                results.add(trans);
            }
        });
        return results;
    }

    @Override
    public List<AgentResponse> listUserAgent(String nickName) throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        String sql = "";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            PreparedStatement stmt;
            if (!nickName.isEmpty()) {
                sql = "SELECT nameagent,nickname,phone,address,id,parentid,`show`,active,percent_bonus_vincard FROM useragent WHERE status='D' and `active` = 1 and  nickname=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nickName);
            } else {
                sql = "SELECT nameagent,nickname,phone,address,id,parentid,`show`,active,percent_bonus_vincard FROM useragent WHERE status='D' and `active` = 1";
                stmt = conn.prepareStatement(sql);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AgentResponse agent = new AgentResponse();
                agent.fullName = rs.getString("nameagent");
                agent.nickName = rs.getString("nickname");
                agent.mobile = rs.getString("phone");
                agent.address = rs.getString("address");
                agent.id = rs.getInt("id");
                agent.parentid = rs.getInt("parentid");
                agent.show = rs.getInt("show");
                agent.active = rs.getInt("active");
                agent.percent = rs.getInt("percent_bonus_vincard");
                results.add(0, agent);
            }
            rs.close();
            stmt.close();
        }
        return results;
    }

    @Override
    public List<AgentResponse> listUserAgentByParentID(int ParentID) throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        String sql = "";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            sql = "SELECT nameagent,nickname,phone,address,id,parentid FROM useragent WHERE status='D' and parentid=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, ParentID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AgentResponse agent = new AgentResponse();
                agent.fullName = rs.getString("nameagent");
                agent.nickName = rs.getString("nickname");
                agent.mobile = rs.getString("phone");
                agent.address = rs.getString("address");
                agent.id = rs.getInt("id");
                agent.parentid = rs.getInt("parentid");
                results.add(0, agent);
            }
            rs.close();
            stmt.close();
        }
        return results;
    }

    @Override
    public TranferAgentResponse searchAgentTranfer(String nickName, String status, String timeStart, String timeEnd) throws SQLException {
        long totalBuy1 = 0L;
        long totalSale1 = 0L;
        long totalFeeBuy1 = 0L;
        long totalFeeSale1 = 0L;
        int countBuy1 = 0;
        int countSale1 = 0;
        long totalBuy2 = 0L;
        long totalSale2 = 0L;
        long totalFeeBuy2 = 0L;
        long totalFeeSale2 = 0L;
        int countBuy2 = 0;
        int countSale2 = 0;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        if (collection != null) {
            HashMap<String, Object> statusmua1 = new HashMap<String, Object>();
            HashMap<String, Object> statusmua2 = new HashMap<String, Object>();
            HashMap<String, Object> statusban1 = new HashMap<String, Object>();
            HashMap<String, Object> statusban2 = new HashMap<String, Object>();
            HashMap<String, Object> statusfeeban1 = new HashMap<String, Object>();
            HashMap<String, Object> statusfeeban2 = new HashMap<String, Object>();
            HashMap<String, Object> statusfeemua1 = new HashMap<String, Object>();
            HashMap<String, Object> statusfeemua2 = new HashMap<String, Object>();
            BasicDBObject obj = new BasicDBObject();
            BasicDBObject stt1 = new BasicDBObject("status", (Object) 1);
            BasicDBObject stt2 = new BasicDBObject("status", (Object) 2);
            BasicDBObject stt3 = new BasicDBObject("status", (Object) 3);
            BasicDBObject stt4 = new BasicDBObject("status", (Object) 4);
            BasicDBObject stt5 = new BasicDBObject("status", (Object) 5);
            BasicDBObject stt6 = new BasicDBObject("status", (Object) 6);
            BasicDBObject stt7 = new BasicDBObject("status", (Object) 7);
            BasicDBObject stt8 = new BasicDBObject("status", (Object) 8);
            statusmua1.put("top_ds", 1);
            statusmua2.put("top_ds", 1);
            statusban1.put("top_ds", 1);
            statusban2.put("top_ds", 1);
            if (!timeStart.isEmpty() && !timeEnd.isEmpty()) {
                obj.put("$gte", (Object) timeStart);
                obj.put("$lte", (Object) timeEnd);
                statusmua1.put("trans_time", (Object) obj);
                statusban1.put("trans_time", (Object) obj);
                statusfeemua1.put("trans_time", (Object) obj);
                statusfeeban1.put("trans_time", (Object) obj);
                statusmua2.put("trans_time", (Object) obj);
                statusban2.put("trans_time", (Object) obj);
                statusfeemua2.put("trans_time", (Object) obj);
                statusfeeban2.put("trans_time", (Object) obj);
            }
            if (!status.isEmpty()) {
                statusmua1.put("status", status);
                statusban1.put("status", status);
                statusfeemua1.put("status", status);
                statusfeeban1.put("status", status);
                statusmua2.put("status", status);
                statusban2.put("status", status);
                statusfeemua2.put("status", status);
                statusfeeban2.put("status", status);
            } else {
                ArrayList<BasicDBObject> sttdsmua1 = new ArrayList<BasicDBObject>();
                sttdsmua1.add(stt1);
                statusmua1.put("$or", sttdsmua1);
                statusmua1.put("nick_name_receive", nickName);
                ArrayList<BasicDBObject> sttdsmua2 = new ArrayList<BasicDBObject>();
                sttdsmua2.add(stt2);
                statusmua2.put("$or", sttdsmua2);
                statusmua2.put("nick_name_receive", nickName);
                ArrayList<BasicDBObject> sttdsban1 = new ArrayList<BasicDBObject>();
                sttdsban1.add(stt3);
                statusban1.put("$or", sttdsban1);
                statusban1.put("nick_name_send", nickName);
                ArrayList<BasicDBObject> sttdsban2 = new ArrayList<BasicDBObject>();
                sttdsban2.add(stt6);
                statusban2.put("$or", sttdsban2);
                statusban2.put("nick_name_send", nickName);
                ArrayList<BasicDBObject> sttfeemua1 = new ArrayList<BasicDBObject>();
                sttfeemua1.add(stt1);
                sttfeemua1.add(stt7);
                statusfeemua1.put("$or", sttfeemua1);
                statusfeemua1.put("nick_name_receive", nickName);
                ArrayList<BasicDBObject> sttfeemua2 = new ArrayList<BasicDBObject>();
                sttfeemua2.add(stt2);
                statusfeemua2.put("$or", sttfeemua2);
                statusfeemua2.put("nick_name_receive", nickName);
                ArrayList<BasicDBObject> sttfeeban1 = new ArrayList<BasicDBObject>();
                sttfeeban1.add(stt3);
                sttfeeban1.add(stt4);
                sttfeeban1.add(stt5);
                statusfeeban1.put("$or", sttfeeban1);
                statusfeeban1.put("nick_name_send", nickName);
                ArrayList<BasicDBObject> sttfeeban2 = new ArrayList<BasicDBObject>();
                sttfeeban2.add(stt6);
                sttfeeban2.add(stt8);
                statusfeeban2.put("$or", sttfeeban2);
                statusfeeban2.put("nick_name_send", nickName);
            }
            Document dsmua1 = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusmua1)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$money_send")))})).first();
            Document dsmua2 = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusmua2)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$money_send")))})).first();
            Document dsban1 = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusban1)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$money_send")))})).first();
            Document dsban2 = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusban2)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$money_send")))})).first();
            Document feemua1 = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusfeemua1)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$fee")).append("count", (Object) new Document("$sum", (Object) 1)))})).first();
            Document feemua2 = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusfeemua2)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$fee")).append("count", (Object) new Document("$sum", (Object) 1)))})).first();
            Document feeban1 = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusfeeban1)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$fee")).append("count", (Object) new Document("$sum", (Object) 1)))})).first();
            Document feeban2 = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusfeeban2)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$fee")).append("count", (Object) new Document("$sum", (Object) 1)))})).first();
            if (dsmua1 != null) {
                totalBuy1 = dsmua1.getLong((Object) "money");
            }
            if (dsban1 != null) {
                totalSale1 = dsban1.getLong((Object) "money");
            }
            if (feemua1 != null) {
                totalFeeBuy1 = feemua1.getLong((Object) "money");
                countBuy1 = feemua1.getInteger((Object) "count");
            }
            if (feeban1 != null) {
                totalFeeSale1 = feeban1.getLong((Object) "money");
                countSale1 = feeban1.getInteger((Object) "count");
            }
            if (dsmua2 != null) {
                totalBuy2 = dsmua2.getLong((Object) "money");
            }
            if (dsban2 != null) {
                totalSale2 = dsban2.getLong((Object) "money");
            }
            if (feemua2 != null) {
                totalFeeBuy2 = feemua2.getLong((Object) "money");
                countBuy2 = feemua2.getInteger((Object) "count");
            }
            if (feeban2 != null) {
                totalFeeSale2 = feeban2.getLong((Object) "money");
                countSale2 = feeban2.getInteger((Object) "count");
            }
        }
        TranferAgentResponse trans = new TranferAgentResponse(nickName, totalBuy1, totalSale1, totalFeeBuy1, totalFeeSale1, countBuy1, countSale1, totalBuy2, totalSale2, totalFeeBuy2, totalFeeSale2, countBuy2, countSale2);
        return trans;
    }

    @Override
    public AgentDSModel getDS(String nickName, String timeStart, String timeEnd, boolean bAgent1) throws SQLException {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> statusmua = new HashMap<String, Object>();
        HashMap<String, Object> statusban = new HashMap<String, Object>();
        if (!timeStart.isEmpty() && !timeEnd.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            statusmua.put("trans_time", (Object) obj);
            statusban.put("trans_time", (Object) obj);
        }
        statusmua.put("top_ds", 1);
        statusmua.put("nick_name_receive", nickName);
        statusban.put("top_ds", 1);
        statusban.put("nick_name_send", nickName);
        if (bAgent1) {
            statusmua.put("status", 1);
            statusban.put("status", 3);
        } else {
            statusmua.put("status", 2);
            statusban.put("status", 6);
        }
        long dsMua = 0L;
        long dsBan = 0L;
        int gdMua = 0;
        int gdBan = 0;
        Document dsmua = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusmua)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$money_send")).append("count", (Object) new Document("$sum", (Object) 1)))})).first();
        Document dsban = (Document) collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(statusban)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$money_send")).append("count", (Object) new Document("$sum", (Object) 1)))})).first();
        if (dsmua != null) {
            dsMua = dsmua.getLong((Object) "money");
            gdMua = dsmua.getInteger((Object) "count");
        }
        if (dsban != null) {
            dsBan = dsban.getLong((Object) "money");
            gdBan = dsban.getInteger((Object) "count");
        }
        long ds = dsMua + dsBan;
        int gd = gdMua + gdBan;
        AgentDSModel res = new AgentDSModel(dsMua, dsBan, ds, gdMua, gdBan, gd);
        return res;
    }

    @Override
    public Map<String, ArrayList<String>> getAllAgent() throws SQLException {
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            String sql = " SELECT id, nickname, percent_bonus_vincard  FROM useragent  WHERE status = 'D'    AND parentid = -1    AND active = 1 ";
            PreparedStatement stmt = conn.prepareStatement(" SELECT id, nickname, percent_bonus_vincard  FROM useragent  WHERE status = 'D'    AND parentid = -1    AND active = 1 ");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ArrayList<String> temp = new ArrayList<String>();
                int id = rs.getInt("id");
                String agent1 = rs.getString("nickname");
                int percent = rs.getInt("percent_bonus_vincard");
                StringBuilder agent2 = new StringBuilder("");
                String sql2 = " SELECT nickname FROM useragent WHERE status = 'D' AND parentid = ? ";
                PreparedStatement stmt2 = conn.prepareStatement(" SELECT nickname FROM useragent WHERE status = 'D' AND parentid = ? ");
                stmt2.setInt(1, id);
                ResultSet rs2 = stmt2.executeQuery();
                while (rs2.next()) {
                    agent2.append(rs2.getString("nickname")).append(",");
                }
                if (agent2.length() >= 1) {
                    agent2.delete(agent2.length() - 1, agent2.length());
                }
                rs2.close();
                stmt2.close();
                temp.add(0, agent2.toString());
                temp.add(1, String.valueOf(percent));
                map.put(agent1, temp);
            }
            rs.close();
            stmt.close();
        }
        return map;
    }

    @Override
    public Map<String, String> getAllNameAgent() throws SQLException {
        HashMap<String, String> map = new HashMap<String, String>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            String sql = "SELECT id,nickname FROM useragent WHERE status='D' and parentid = -1 and active = 1";
            PreparedStatement stmt = conn.prepareStatement("SELECT id,nickname FROM useragent WHERE status='D' and parentid = -1 and active = 1");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String agent1 = rs.getString("nickname");
                StringBuilder agent2 = new StringBuilder("");
                String sql2 = "SELECT nickname FROM useragent WHERE status='D' and parentid = ?";
                PreparedStatement stmt2 = conn.prepareStatement("SELECT nickname FROM useragent WHERE status='D' and parentid = ?");
                stmt2.setInt(1, id);
                ResultSet rs2 = stmt2.executeQuery();
                while (rs2.next()) {
                    agent2.append(rs2.getString("nickname")).append(",");
                }
                if (agent2.length() >= 1) {
                    agent2.delete(agent2.length() - 1, agent2.length());
                }
                rs2.close();
                stmt2.close();
                map.put(agent1, agent2.toString());
            }
            rs.close();
            stmt.close();
        }
        return map;
    }

    @Override
    public boolean checkRefundFeeAgent(String nickname, String month) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("nick_name", nickname);
        conditions.put("month", month);
        conditions.put("code", Integer.parseInt("0"));
        FindIterable iterable = db.getCollection("log_refund_fee_agent").find((Bson) new Document(conditions)).limit(1);
        Document document = iterable != null ? (Document) iterable.first() : null;
        return document != null;
    }

    @Override
    public List<RefundFeeAgentMessage> getLogRefundFeeAgent(String nickname, String month) {
        final ArrayList<RefundFeeAgentMessage> results = new ArrayList<RefundFeeAgentMessage>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, String> conditions = new HashMap<String, String>();
        if (!nickname.isEmpty()) {
            conditions.put("nick_name", nickname);
        }
        if (!month.isEmpty()) {
            conditions.put("month", month);
        }
        FindIterable iterable = db.getCollection("log_refund_fee_agent").find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                RefundFeeAgentMessage trans = new RefundFeeAgentMessage(document.getString((Object) "nick_name"), document.getLong((Object) "fee_1").longValue(), document.getDouble((Object) "ratio_1").doubleValue(), document.getLong((Object) "fee_2").longValue(), document.getDouble((Object) "ratio_2").doubleValue(), document.getLong((Object) "fee_2_more") == null ? 0L : document.getLong((Object) "fee_2_more"), document.getDouble((Object) "ratio_2_more") == null ? 0.0 : document.getDouble((Object) "ratio_2_more"), document.getLong((Object) "fee").longValue(), document.getString((Object) "month"), document.getInteger((Object) "code").intValue(), document.getString((Object) "description"), document.getLong((Object) "fee_vinplay_card") == null ? 0L : document.getLong((Object) "fee_vinplay_card"), document.getLong((Object) "fee_vin_cash") == null ? 0L : document.getLong((Object) "fee_vin_cash"), document.getInteger((Object) "percent") == null ? 0 : document.getInteger((Object) "percent"));
                String createTime = document.getString((Object) "time_log");
                trans.setCreateTime(createTime);
                results.add(trans);
            }
        });
        return results;
    }

    @Override
    public long countsearchAgentTranferMoney(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String top_ds) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("")) {
            conditions.put("nick_name_send", nickNameSend);
        }
        if (nickNameRecieve != null && !nickNameRecieve.equals("")) {
            conditions.put("nick_name_receive", nickNameRecieve);
        }
        if (top_ds != null && !top_ds.equals("")) {
            conditions.put("top_ds", Integer.parseInt(top_ds));
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.parseInt(status));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        long totalRecord = db.getCollection("log_chuyen_tien_dai_ly").count((Bson) new Document(conditions));
        return totalRecord;
    }

    @Override
    public long totalMoneyVinReceiveFromAgent(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("") || nickNameRecieve != null && !nickNameRecieve.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickNameSend);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickNameRecieve);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(topDS)));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$money_receive")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public long totalMoneyVinSendFromAgent(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("") || nickNameRecieve != null && !nickNameRecieve.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickNameSend);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickNameRecieve);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(topDS)));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$money_send")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public long totalMoneyVinFeeFromAgent(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("") || nickNameRecieve != null && !nickNameRecieve.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickNameSend);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickNameRecieve);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(topDS)));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "").append("money", (Object) new Document("$sum", (Object) "$fee")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public List<LogAgentTranferMoneyResponse> searchAgentTranferMoneyVinSale(String nickName, String timeStart, String timeEnd, String type, Boolean typeThongKe, int page, int totalRecord) {
        BasicDBObject nnSend;
        BasicDBObject nnReceive;
        ArrayList<BasicDBObject> myList;
        BasicDBObject query2;
        BasicDBObject query1;
        ArrayList<BasicDBObject> lstNN;
        final ArrayList<LogAgentTranferMoneyResponse> results = new ArrayList<LogAgentTranferMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        BasicDBObject objNN = new BasicDBObject();
        BasicDBObject objSTT = new BasicDBObject();
        BasicDBList lstAll = new BasicDBList();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        int numStart = (page - 1) * totalRecord;
        objsort.put("_id", -1);
        if (type.equals("1")) {
            if (nickName != null && !nickName.equals("")) {
                nnSend = new BasicDBObject("nick_name_send", (Object) nickName);
                nnReceive = new BasicDBObject("nick_name_receive", (Object) nickName);
                lstNN = new ArrayList<BasicDBObject>();
                lstNN.add(nnSend);
                lstNN.add(nnReceive);
                objNN.put("$or", lstNN);
            }
            query1 = new BasicDBObject("status", (Object) 1);
            query2 = new BasicDBObject("status", (Object) 2);
            myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            objSTT.put("$or", myList);
            lstAll.add((Object) objNN);
            lstAll.add((Object) objSTT);
            conditions.put("$and", (Object) lstAll);
        }
        if (type.equals("2")) {
            if (nickName != null && !nickName.equals("")) {
                nnSend = new BasicDBObject("nick_name_send", (Object) nickName);
                nnReceive = new BasicDBObject("nick_name_receive", (Object) nickName);
                lstNN = new ArrayList();
                lstNN.add(nnSend);
                lstNN.add(nnReceive);
                objNN.put("$or", lstNN);
            }
            query1 = new BasicDBObject("status", (Object) 3);
            query2 = new BasicDBObject("status", (Object) 6);
            myList = new ArrayList();
            myList.add(query1);
            myList.add(query2);
            objSTT.put("$or", myList);
            lstAll.add((Object) objNN);
            lstAll.add((Object) objSTT);
            conditions.put("$and", (Object) lstAll);
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }

        if (Objects.equals(typeThongKe, Boolean.TRUE)) {
            iterable = db.getCollection("log_chuyen_tien_dai_ly").find((Bson) new Document(conditions)).sort((Bson) objsort);
        } else {
            iterable = db.getCollection("log_chuyen_tien_dai_ly").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(totalRecord);
        }


        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogAgentTranferMoneyResponse trans = new LogAgentTranferMoneyResponse();
                trans.nick_name_send = document.getString((Object) "nick_name_send");
                trans.nick_name_receive = document.getString((Object) "nick_name_receive");
                trans.status = document.getInteger((Object) "status");
                trans.trans_time = document.getString((Object) "trans_time");
                trans.fee = document.getLong((Object) "fee");
                trans.money_send = document.getLong((Object) "money_send");
                trans.money_receive = document.getLong((Object) "money_receive");
                results.add(trans);
            }
        });
        return results;
    }

    @Override
    public long countSearchAgentTranferMoneyVinSale(String nickName, String timeStart, String timeEnd, String type) {
        BasicDBObject query2;
        BasicDBObject query1;
        ArrayList<BasicDBObject> lstNN;
        ArrayList<BasicDBObject> myList;
        BasicDBObject nnSend;
        BasicDBObject nnReceive;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        BasicDBObject objNN = new BasicDBObject();
        BasicDBObject objSTT = new BasicDBObject();
        BasicDBList lstAll = new BasicDBList();
        objsort.put("_id", -1);
        if (type.equals("1")) {
            if (!nickName.equals(null) && !nickName.equals("")) {
                nnSend = new BasicDBObject("nick_name_send", (Object) nickName);
                nnReceive = new BasicDBObject("nick_name_receive", (Object) nickName);
                lstNN = new ArrayList<BasicDBObject>();
                lstNN.add(nnSend);
                lstNN.add(nnReceive);
                objNN.put("$or", lstNN);
            }
            query1 = new BasicDBObject("status", (Object) 1);
            query2 = new BasicDBObject("status", (Object) 2);
            myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            objSTT.put("$or", myList);
            lstAll.add((Object) objNN);
            lstAll.add((Object) objSTT);
            conditions.put("$and", (Object) lstAll);
        }
        if (type.equals("2")) {
            if (!nickName.equals(null) && !nickName.equals("")) {
                nnSend = new BasicDBObject("nick_name_send", (Object) nickName);
                nnReceive = new BasicDBObject("nick_name_receive", (Object) nickName);
                lstNN = new ArrayList();
                lstNN.add(nnSend);
                lstNN.add(nnReceive);
                objNN.put("$or", lstNN);
            }
            query1 = new BasicDBObject("status", (Object) 3);
            query2 = new BasicDBObject("status", (Object) 6);
            myList = new ArrayList();
            myList.add(query1);
            myList.add(query2);
            objSTT.put("$or", myList);
            lstAll.add((Object) objNN);
            lstAll.add((Object) objSTT);
            conditions.put("$and", (Object) lstAll);
        }
        if (!(timeStart.equals(null) || timeStart.equals("") || timeEnd.equals(null) || timeEnd.equals(""))) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        long totalRecord = db.getCollection("log_chuyen_tien_dai_ly").count((Bson) new Document(conditions));
        return totalRecord;
    }

    @Override
    public long totalMoneyVinReceiveFromAgentByStatus(String nickName, String type, String timeStart, String timeEnd) {
        BasicDBObject nnReceive;
        BasicDBObject nnSend;
        BasicDBObject query2;
        BasicDBObject query1;
        ArrayList<BasicDBObject> lstNN;
        ArrayList<BasicDBObject> myList;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        BasicDBObject objNN = new BasicDBObject();
        BasicDBObject objSTT = new BasicDBObject();
        BasicDBList lstAll = new BasicDBList();
        objsort.put("_id", -1);
        if (type.equals("1")) {
            if (!nickName.equals(null) && !nickName.equals("")) {
                nnSend = new BasicDBObject("nick_name_send", (Object) nickName);
                nnReceive = new BasicDBObject("nick_name_receive", (Object) nickName);
                lstNN = new ArrayList<BasicDBObject>();
                lstNN.add(nnSend);
                lstNN.add(nnReceive);
                objNN.put("$or", lstNN);
            }
            query1 = new BasicDBObject("status", (Object) 1);
            query2 = new BasicDBObject("status", (Object) 2);
            myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            objSTT.put("$or", myList);
            lstAll.add((Object) objNN);
            lstAll.add((Object) objSTT);
            conditions.put("$and", (Object) lstAll);
        }
        if (type.equals("2")) {
            if (!nickName.equals(null) && !nickName.equals("")) {
                nnSend = new BasicDBObject("nick_name_send", (Object) nickName);
                nnReceive = new BasicDBObject("nick_name_receive", (Object) nickName);
                lstNN = new ArrayList();
                lstNN.add(nnSend);
                lstNN.add(nnReceive);
                objNN.put("$or", lstNN);
            }
            query1 = new BasicDBObject("status", (Object) 3);
            query2 = new BasicDBObject("status", (Object) 6);
            myList = new ArrayList();
            myList.add(query1);
            myList.add(query2);
            objSTT.put("$or", myList);
            lstAll.add((Object) objNN);
            lstAll.add((Object) objSTT);
            conditions.put("$and", (Object) lstAll);
        }
        if (!(timeStart.equals(null) || timeStart.equals("") || timeEnd.equals(null) || timeEnd.equals(""))) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$money_receive")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public long totalMoneyVinSendFromAgentByStatus(String nickName, String type, String timeStart, String timeEnd) {
        BasicDBObject nnReceive;
        BasicDBObject nnSend;
        BasicDBObject query2;
        BasicDBObject query1;
        ArrayList<BasicDBObject> lstNN;
        ArrayList<BasicDBObject> myList;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        BasicDBObject objNN = new BasicDBObject();
        BasicDBObject objSTT = new BasicDBObject();
        BasicDBList lstAll = new BasicDBList();
        objsort.put("_id", -1);
        if (type.equals("1")) {
            if (!nickName.equals(null) && !nickName.equals("")) {
                nnSend = new BasicDBObject("nick_name_send", (Object) nickName);
                nnReceive = new BasicDBObject("nick_name_receive", (Object) nickName);
                lstNN = new ArrayList<BasicDBObject>();
                lstNN.add(nnSend);
                lstNN.add(nnReceive);
                objNN.put("$or", lstNN);
            }
            query1 = new BasicDBObject("status", (Object) 1);
            query2 = new BasicDBObject("status", (Object) 2);
            myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            objSTT.put("$or", myList);
            lstAll.add((Object) objNN);
            lstAll.add((Object) objSTT);
            conditions.put("$and", (Object) lstAll);
        }
        if (type.equals("2")) {
            if (!nickName.equals(null) && !nickName.equals("")) {
                nnSend = new BasicDBObject("nick_name_send", (Object) nickName);
                nnReceive = new BasicDBObject("nick_name_receive", (Object) nickName);
                lstNN = new ArrayList();
                lstNN.add(nnSend);
                lstNN.add(nnReceive);
                objNN.put("$or", lstNN);
            }
            query1 = new BasicDBObject("status", (Object) 3);
            query2 = new BasicDBObject("status", (Object) 6);
            myList = new ArrayList();
            myList.add(query1);
            myList.add(query2);
            objSTT.put("$or", myList);
            lstAll.add((Object) objNN);
            lstAll.add((Object) objSTT);
            conditions.put("$and", (Object) lstAll);
        }
        if (!(timeStart.equals(null) || timeStart.equals("") || timeEnd.equals(null) || timeEnd.equals(""))) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$money_send")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public boolean updateTopDsFromAgent(String nickNameSend, String nickNameReceive, String timeLog, String topds) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, String> conditions = new HashMap<String, String>();
        conditions.put("trans_time", timeLog);
        conditions.put("nick_name_send", nickNameSend);
        conditions.put("nick_name_receive", nickNameReceive);
        col.updateOne((Bson) new Document(conditions), (Bson) new Document("$set", (Object) new Document("top_ds", (Object) Integer.parseInt(topds))));
        return true;
    }

    @Override
    public boolean updateTopDsFromAgentMySQL(String nickNameSend, String nickNameReceive, String timeLog, String topds) throws SQLException {
        String sql = " UPDATE vinplay.log_tranfer_agent  SET top_ds = ?,      update_time = ?  WHERE trans_time = ?        AND nick_name_send = ?        AND nick_name_receive = ? ";
        PreparedStatement stmt = null;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");) {
            stmt = conn.prepareStatement(" UPDATE vinplay.log_tranfer_agent  SET top_ds = ?,      update_time = ?  WHERE trans_time = ?        AND nick_name_send = ?        AND nick_name_receive = ? ");
            stmt.setInt(1, Integer.parseInt(topds));
            stmt.setString(2, DateTimeUtils.getCurrentTime((String) "dd-MM-yyyy HH:mm:ss"));
            stmt.setString(3, timeLog);
            stmt.setString(4, nickNameSend);
            stmt.setString(5, nickNameReceive);
            stmt.executeUpdate();
            stmt.close();
        }
        return true;
    }

    @Override
    public boolean logBonusTopDS(BonusTopDSModel model) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("log_bonus_top_ds_agent");
        Document doc = new Document();
        doc.append("nick_name", (Object) model.getNickname());
        doc.append("ds", (Object) model.getDs());
        doc.append("top", (Object) model.getTop());
        doc.append("bonus_fix", (Object) model.getBonusFix());
        doc.append("bonus_more", (Object) model.getBonusMore());
        doc.append("ds_2", (Object) model.getDs2());
        doc.append("top_2", (Object) model.getTop2());
        doc.append("bonus_fix_2", (Object) model.getBonusFix2());
        doc.append("bonus_more_2", (Object) model.getBonusMore2());
        doc.append("bonus_total", (Object) model.getBonusTotal());
        doc.append("month", (Object) model.getMonth());
        doc.append("code", (Object) model.getCode());
        doc.append("description", (Object) model.getDescription());
        doc.append("time_log", (Object) model.getTimeLog());
        doc.append("create_time", (Object) new Date());
        doc.append("bonus_vinplay_card", (Object) model.getBonusVinplayCard());
        doc.append("bonus_vin_cash", (Object) model.getBonusVinCash());
        doc.append("percent", (Object) model.getPercent());
        col.insertOne((Object) doc);
        return true;
    }

    @Override
    public boolean checkBonusTopDS(String nickname, String month) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("nick_name", nickname);
        conditions.put("month", month);
        conditions.put("code", Integer.parseInt("0"));
        FindIterable iterable = db.getCollection("log_bonus_top_ds_agent").find((Bson) new Document(conditions)).limit(1);
        Document document = iterable != null ? (Document) iterable.first() : null;
        return document != null;
    }

    @Override
    public List<BonusTopDSModel> getLogBonusTopDS(String nickname, String month) {
        final ArrayList<BonusTopDSModel> results = new ArrayList<BonusTopDSModel>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, String> conditions = new HashMap<String, String>();
        if (nickname != null && !nickname.equals("")) {
            conditions.put("nick_name", nickname);
        }
        if (month != null && !month.equals("")) {
            conditions.put("month", month);
        }
        FindIterable iterable = db.getCollection("log_bonus_top_ds_agent").find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                BonusTopDSModel model = new BonusTopDSModel(document.getString((Object) "nick_name"), document.getLong((Object) "ds"), document.getInteger((Object) "top"), document.getLong((Object) "bonus_fix"), document.getLong((Object) "bonus_more"), document.getLong((Object) "ds_2") == null ? 0L : document.getLong((Object) "ds_2"), document.getInteger((Object) "top_2") == null ? 0 : document.getInteger((Object) "top_2"), document.getLong((Object) "bonus_fix_2") == null ? 0L : document.getLong((Object) "bonus_fix_2"), document.getLong((Object) "bonus_more_2") == null ? 0L : document.getLong((Object) "bonus_more_2"), document.getLong((Object) "bonus_total"), document.getString((Object) "month"), document.getInteger((Object) "code"), document.getString((Object) "description"), document.getString((Object) "time_log"), document.getLong((Object) "bonus_vinplay_card") == null ? 0L : document.getLong((Object) "bonus_vinplay_card"), document.getLong((Object) "bonus_vin_cash") == null ? 0L : document.getLong((Object) "bonus_vin_cash"), document.getInteger((Object) "percent") == null ? 0 : document.getInteger((Object) "percent"));
                results.add(model);
            }
        });
        return results;
    }

    @Override
    public TranferMoneyAgent getTransferMoneyAgent(String nickNameSend, String nickNameReceive, String timeLog) {
        TranferMoneyAgent results = null;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, String> conditions = new HashMap<String, String>();
        conditions.put("trans_time", timeLog);
        conditions.put("nick_name_send", nickNameSend);
        conditions.put("nick_name_receive", nickNameReceive);
        Document dc = (Document) col.find((Bson) new Document(conditions)).first();
        if (dc != null) {
            int status = dc.getInteger((Object) "status");
            long moneySend = dc.getLong((Object) "money_send");
            long moneyReceive = dc.getLong((Object) "money_receive");
            results = new TranferMoneyAgent(nickNameSend, nickNameReceive, moneySend, moneyReceive, status);
        }
        return results;
    }

    @Override
    public List<AgentResponse> listUserAgentAdmin(String nickName) throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        String sql = "";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            PreparedStatement stmt;
            if (nickName != null && !nickName.equals("")) {
                sql = "SELECT nameagent,nickname,phone,address,id,parentid,`show`,active,percent_bonus_vincard FROM useragent WHERE status='D'  and  nickname=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nickName);
            } else {
                sql = "SELECT nameagent,nickname,phone,address,id,parentid,`show`,active,percent_bonus_vincard FROM useragent WHERE status='D'";
                stmt = conn.prepareStatement(sql);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AgentResponse agent = new AgentResponse();
                agent.fullName = rs.getString("nameagent");
                agent.nickName = rs.getString("nickname");
                agent.mobile = rs.getString("phone");
                agent.address = rs.getString("address");
                agent.id = rs.getInt("id");
                agent.parentid = rs.getInt("parentid");
                agent.show = rs.getInt("show");
                agent.active = rs.getInt("active");
                agent.percent = rs.getInt("percent_bonus_vincard");
                results.add(0, agent);
            }
            rs.close();
            stmt.close();
        }
        return results;
    }

    @Override
    public List<LogAgentTranferMoneyResponse> searchAgentTranferMoney(String nickName, String status, String timeStart, String timeEnd, String top_ds, int page) {
        final ArrayList<LogAgentTranferMoneyResponse> results = new ArrayList<LogAgentTranferMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        int numStart = (page - 1) * 50;
        int numEnd = 50;
        objsort.put("_id", -1);
        if (nickName != null && !nickName.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickName);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickName);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (top_ds != null && !top_ds.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(top_ds)));
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", obj);
        }
        iterable = db.getCollection("log_chuyen_tien_dai_ly").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(50);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogAgentTranferMoneyResponse trans = new LogAgentTranferMoneyResponse();
                trans.nick_name_send = document.getString((Object) "nick_name_send");
                trans.nick_name_receive = document.getString((Object) "nick_name_receive");
                trans.status = document.getInteger((Object) "status");
                trans.trans_time = document.getString((Object) "trans_time");
                trans.fee = document.getLong((Object) "fee");
                trans.money_send = document.getLong((Object) "money_send");
                trans.money_receive = document.getLong((Object) "money_receive");
                trans.top_ds = document.getInteger((Object) "top_ds");
                trans.process = document.getInteger((Object) "process");
                trans.des_send = document.getString((Object) "des_send") != null && !document.getString((Object) "des_send").equals("") ? document.getString((Object) "des_send") : "";
                trans.des_receive = document.getString((Object) "des_receive") != null && !document.getString((Object) "des_receive").equals("") ? document.getString((Object) "des_receive") : "";
                results.add(trans);
            }
        });
        return results;
    }

    @Override
    public LogAgentTranferMoneyResponse searchAgentTranferMoney(String tid) {
        final LogAgentTranferMoneyResponse result = new LogAgentTranferMoneyResponse();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        conditions.put("transaction_no", tid);
        iterable = db.getCollection("log_chuyen_tien_dai_ly").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(0).limit(1);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                result.nick_name_send = document.getString((Object) "nick_name_send");
                result.nick_name_receive = document.getString((Object) "nick_name_receive");
                result.status = document.getInteger((Object) "status");
                result.trans_time = document.getString((Object) "trans_time");
                result.fee = document.getLong((Object) "fee");
                result.money_send = document.getLong((Object) "money_send");
                result.money_receive = document.getLong((Object) "money_receive");
                result.top_ds = document.getInteger((Object) "top_ds");
                result.process = document.getInteger((Object) "process");
                result.des_send = document.getString((Object) "des_send") != null && !document.getString((Object) "des_send").equals("") ? document.getString((Object) "des_send") : "";
                result.des_receive = document.getString((Object) "des_receive") != null && !document.getString((Object) "des_receive").equals("") ? document.getString((Object) "des_receive") : "";
            }
        });
        return result;
    }

    @Override
    public long countsearchAgentTranferMoney(String nickName, String status, String timeStart, String timeEnd, String top_ds) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickName != null && !nickName.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickName);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickName);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (top_ds != null && !top_ds.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(top_ds)));
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Serializable) obj);
        }
        long totalRecord = db.getCollection("log_chuyen_tien_dai_ly").count((Bson) new Document(conditions));
        return totalRecord;
    }

    @Override
    public long totalMoneyVinReceiveFromAgent(String nickName, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickName != null && !nickName.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickName);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickName);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(topDS)));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Serializable) obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$money_receive")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public long totalMoneyVinSendFromAgent(String nickName, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickName != null && !nickName.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickName);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickName);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(topDS)));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Serializable) obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$money_send")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public long totalMoneyVinFeeFromAgent(String nickName, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Serializable> conditions = new HashMap<String, Serializable>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickName != null && !nickName.equals("")) {
            BasicDBObject query1 = new BasicDBObject("nick_name_send", (Object) nickName);
            BasicDBObject query2 = new BasicDBObject("nick_name_receive", (Object) nickName);
            ArrayList<BasicDBObject> myList = new ArrayList<BasicDBObject>();
            myList.add(query1);
            myList.add(query2);
            conditions.put("$or", myList);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.valueOf(Integer.parseInt(status)));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.valueOf(Integer.parseInt(topDS)));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Serializable) obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "").append("money", (Object) new Document("$sum", (Object) "$fee")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public List<AgentResponse> listUserAgentLevel2ByParentID(int ParentID) throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        String sql = "";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            sql = "SELECT nameagent,nickname,phone,address,id,parentid FROM useragent WHERE status='D' and  active=1 and parentid=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, ParentID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AgentResponse agent = new AgentResponse();
                agent.fullName = rs.getString("nameagent");
                agent.nickName = rs.getString("nickname");
                agent.mobile = rs.getString("phone");
                agent.address = rs.getString("address");
                agent.id = rs.getInt("id");
                agent.parentid = rs.getInt("parentid");
                results.add(0, agent);
            }
            rs.close();
            stmt.close();
        }
        return results;
    }

    @Override
    public List<AgentResponse> listUserAgentLevel2ByID(int ID) throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        String sql = "";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            sql = "SELECT nameagent,nickname,phone,address,id,parentid FROM useragent WHERE status='D' and `show`=1 and id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AgentResponse agent = new AgentResponse();
                agent.fullName = rs.getString("nameagent");
                agent.nickName = rs.getString("nickname");
                agent.mobile = rs.getString("phone");
                agent.address = rs.getString("address");
                agent.id = rs.getInt("id");
                agent.parentid = rs.getInt("parentid");
                results.add(0, agent);
            }
            rs.close();
            stmt.close();
        }
        return results;
    }

    @Override
    public List<AgentResponse> listUserAgentActive(String nickName) throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        String sql = "";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            PreparedStatement stmt;
            if (nickName != null && !nickName.equals("")) {
                sql = "SELECT nameagent,nickname,phone,address,id,parentid,`show`,active FROM useragent WHERE status='D' and `show` = 1 and active=1 and parentid=-1 and  nickname=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nickName);
            } else {
                sql = "SELECT nameagent,nickname,phone,address,id,parentid,`show`,active FROM useragent WHERE status='D' and active=1 and parentid=-1 and `show` = 1";
                stmt = conn.prepareStatement(sql);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AgentResponse agent = new AgentResponse();
                agent.fullName = rs.getString("nameagent");
                agent.nickName = rs.getString("nickname");
                agent.mobile = rs.getString("phone");
                agent.address = rs.getString("address");
                agent.id = rs.getInt("id");
                agent.parentid = rs.getInt("parentid");
                agent.show = rs.getInt("show");
                agent.active = rs.getInt("active");
                results.add(0, agent);
            }
            rs.close();
            stmt.close();
        }
        return results;
    }

    @Override
    public List<AgentResponse> listUserAgentLevel1Active() throws SQLException {
        ArrayList<AgentResponse> results = new ArrayList<AgentResponse>();
        String sql = "";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            sql = "SELECT nameagent,nickname,phone,address,id,parentid,`show`,active FROM useragent WHERE status='D' and active=1 and parentid=-1 and `show` = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AgentResponse agent = new AgentResponse();
                agent.fullName = rs.getString("nameagent");
                agent.nickName = rs.getString("nickname");
                agent.mobile = rs.getString("phone");
                agent.address = rs.getString("address");
                agent.id = rs.getInt("id");
                agent.parentid = rs.getInt("parentid");
                agent.show = rs.getInt("show");
                agent.active = rs.getInt("active");
                results.add(0, agent);
            }
            rs.close();
            stmt.close();
        }
        return results;
    }

    @Override
    public List<LogAgentTranferMoneyResponse> searchAgentTongTranferMoney(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String top_ds, int page) {
        final ArrayList<LogAgentTranferMoneyResponse> results = new ArrayList<LogAgentTranferMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        int numStart = (page - 1) * 50;
        int numEnd = 50;
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("")) {
            conditions.put("nick_name_send", nickNameSend);
        }
        if (nickNameRecieve != null && !nickNameRecieve.equals("")) {
            conditions.put("nick_name_receive", nickNameRecieve);
        }
        if (top_ds != null && !top_ds.equals("")) {
            conditions.put("top_ds", Integer.parseInt(top_ds));
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.parseInt(status));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        iterable = db.getCollection("log_chuyen_tien_dai_ly").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(50);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogAgentTranferMoneyResponse trans = new LogAgentTranferMoneyResponse();
                trans.nick_name_send = document.getString((Object) "nick_name_send");
                trans.nick_name_receive = document.getString((Object) "nick_name_receive");
                trans.status = document.getInteger((Object) "status");
                trans.trans_time = document.getString((Object) "trans_time");
                trans.fee = document.getLong((Object) "fee");
                trans.money_send = document.getLong((Object) "money_send");
                trans.money_receive = document.getLong((Object) "money_receive");
                trans.top_ds = document.getInteger((Object) "top_ds");
                trans.process = document.getInteger((Object) "process");
                trans.des_send = document.getString((Object) "des_send") != null && !document.getString((Object) "des_send").equals("") ? document.getString((Object) "des_send") : "";
                trans.des_receive = document.getString((Object) "des_receive") != null && !document.getString((Object) "des_receive").equals("") ? document.getString((Object) "des_receive") : "";
                results.add(trans);
            }
        });
        return results;
    }

    @Override
    public long countsearchAgentTongTranferMoney(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String top_ds) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("")) {
            conditions.put("nick_name_send", nickNameSend);
        }
        if (nickNameRecieve != null && !nickNameRecieve.equals("")) {
            conditions.put("nick_name_receive", nickNameRecieve);
        }
        if (top_ds != null && !top_ds.equals("")) {
            conditions.put("top_ds", Integer.parseInt(top_ds));
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.parseInt(status));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        long totalRecord = db.getCollection("log_chuyen_tien_dai_ly").count((Bson) new Document(conditions));
        return totalRecord;
    }

    @Override
    public long totalMoneyVinReceiveFromAgentTong(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("")) {
            conditions.put("nick_name_send", nickNameSend);
        }
        if (nickNameRecieve != null && !nickNameRecieve.equals("")) {
            conditions.put("nick_name_receive", nickNameRecieve);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.parseInt(status));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.parseInt(topDS));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_receive").append("money", (Object) new Document("$sum", (Object) "$money_receive")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public long totalMoneyVinSendFromAgentTong(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("")) {
            conditions.put("nick_name_send", nickNameSend);
        }
        if (nickNameRecieve != null && !nickNameRecieve.equals("")) {
            conditions.put("nick_name_receive", nickNameRecieve);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.parseInt(status));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.parseInt(topDS));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "$nick_name_send").append("money", (Object) new Document("$sum", (Object) "$money_send")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public long totalMoneyVinFeeFromAgentTong(String nickNameSend, String nickNameRecieve, String status, String timeStart, String timeEnd, String topDS) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickNameSend != null && !nickNameSend.equals("")) {
            conditions.put("nick_name_send", nickNameSend);
        }
        if (nickNameRecieve != null && !nickNameRecieve.equals("")) {
            conditions.put("nick_name_receive", nickNameRecieve);
        }
        if (status != null && !status.equals("")) {
            conditions.put("status", Integer.parseInt(status));
        }
        if (topDS != null && !topDS.equals("")) {
            conditions.put("top_ds", Integer.parseInt(topDS));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", (Object) obj);
        }
        AggregateIterable<Document> iterable = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) new Document(conditions)), new Document("$group", (Object) new Document("_id", (Object) "").append("money", (Object) new Document("$sum", (Object) "$fee")))}));
        long totalMoney = 0L;
        for (Document row : iterable) {
            totalMoney += row.getLong((Object) "money").longValue();
        }
        return totalMoney;
    }

    @Override
    public int registerPercentBonusVincard(String nickName, int percent) throws SQLException {
        String sql = " UPDATE vinplay_admin.useragent  SET percent_bonus_vincard = ?  WHERE nickname = ? ";
        int recordNumber = 0;
        PreparedStatement stmt = null;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            stmt = conn.prepareStatement(" UPDATE vinplay_admin.useragent  SET percent_bonus_vincard = ?  WHERE nickname = ? ");
            stmt.setInt(1, percent);
            stmt.setString(2, nickName);
            recordNumber = stmt.executeUpdate();
            stmt.close();
        }
        return recordNumber;
    }

    @Override
    public List<AgentModel> getListPercentBonusVincard(String nickName) throws SQLException {
        List<AgentModel> result = new ArrayList<AgentModel>();
        if (nickName != null && !nickName.isEmpty()) {
            result = "all".equals(nickName) ? this.getAllPercentBonusVincard() : this.getPercentBonusVincardByNickName(nickName);
        }
        return result;
    }

    private List<AgentModel> getAllPercentBonusVincard() throws SQLException {
        ArrayList<AgentModel> result = new ArrayList<AgentModel>();
        String sql = " SELECT nickname, percent_bonus_vincard  FROM vinplay_admin.useragent  WHERE parentid = -1    AND active = 1    AND status = 'D'    AND `show` = 1  ORDER BY `order` ";
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(" SELECT nickname, percent_bonus_vincard  FROM vinplay_admin.useragent  WHERE parentid = -1    AND active = 1    AND status = 'D'    AND `show` = 1  ORDER BY `order` ");
            rs = stmt.executeQuery();
            while (rs.next()) {
                AgentModel agentModel = new AgentModel(rs.getString("nickname"), rs.getInt("percent_bonus_vincard"));
                result.add(agentModel);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }

    private List<AgentModel> getPercentBonusVincardByNickName(String nickName) throws SQLException {
        ArrayList<AgentModel> result = new ArrayList<AgentModel>();
        String sql = " SELECT nickname, percent_bonus_vincard  FROM vinplay_admin.useragent  WHERE parentid = -1    AND nickname = ? ";
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(" SELECT nickname, percent_bonus_vincard  FROM vinplay_admin.useragent  WHERE parentid = -1    AND nickname = ? ");
            stmt.setString(1, nickName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                AgentModel agentModel = new AgentModel(rs.getString("nickname"), rs.getInt("percent_bonus_vincard"));
                result.add(agentModel);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }

}

