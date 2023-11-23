/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.messages.LuckyMessage
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.vbee.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.messages.LuckyMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.dao.LuckyDao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class LuckyDaoImpl
implements LuckyDao {
    @Override
    public boolean saveResultLucky(int userId, String nickname, String ipAddress, int rotateDaily, int rotateFree, int rotateInDay, int rotateByIp, int slotFree) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        call = conn.prepareCall("CALL save_result_lucky(?,?,?,?,?,?,?,?,?)");
        int param = 1;
        call.setInt(param++, userId);
        call.setString(param++, nickname);
        call.setString(param++, ipAddress);
        call.setInt(param++, rotateDaily);
        call.setInt(param++, rotateFree);
        call.setInt(param++, rotateInDay);
        call.setInt(param++, rotateByIp);
        call.setInt(param++, slotFree);
        call.registerOutParameter(param, -6);
        call.executeUpdate();
        success = rotateByIp > 0 && slotFree > 0 ? call.getInt(param) == 3 : (rotateByIp == 0 && slotFree == 0 ? call.getInt(param) == 1 : call.getInt(param) == 2);
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public boolean saveLuckyHistory(LuckyMessage message) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("lucky_new_history");
        Document doc = new Document();
        doc.append("trans_id", (Object)(++com.vinplay.vbee.main.VBeeMain.luckyReferenceId));
        doc.append("user_id", (Object)message.getUserId());
        doc.append("nick_name", (Object)message.getNickname());
        doc.append("result_vin", (Object)message.getResultVin());
        doc.append("result_xu", (Object)message.getResultXu());
        doc.append("result_slot", (Object)message.getResultSlot());
        doc.append("description", (Object)"VQMM");
        doc.append("trans_time", (Object)message.getCreateTime());
        doc.append("create_time", VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
        return true;
    }

    @Override
    public long getLastReferenceId() {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap conditions = new HashMap();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        FindIterable iterable = db.getCollection("lucky_new_history").find((Bson)new Document(conditions)).sort((Bson)objsort).limit(1);
        Document document = iterable != null ? (Document)iterable.first() : null;
        long transId = document == null ? 0L : document.getLong((Object)"trans_id");
        return transId;
    }
}

