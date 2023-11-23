/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.messages.vtcpay.LogVTCPayTopupMessage
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.vtcpay.dao.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.messages.vtcpay.LogVTCPayTopupMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vtcpay.dao.VTCPayDao;
import java.util.Date;
import org.bson.Document;
import org.bson.conversions.Bson;

public class VTCPayDaoImpl
implements VTCPayDao {
    @Override
    public boolean logVTCPayTopup(LogVTCPayTopupMessage message) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("log_topup_vtcpay");
        Document doc = new Document();
        doc.append("reference_id", (Object)message.getReferenceId());
        doc.append("partner_trans_id", (Object)message.getPartnerTransId());
        doc.append("user_id", (Object)message.getUserId());
        doc.append("user_name", (Object)message.getUserName());
        doc.append("nick_name", (Object)message.getNickname());
        doc.append("price", (Object)message.getPrice());
        doc.append("money_user", (Object)message.getMoneyUser());
        doc.append("status", (Object)message.getStatusRes());
        doc.append("response_code", (Object)message.getResponseCode());
        doc.append("description", (Object)message.getResponseDes());
        doc.append("time_request", (Object)message.getTimeRequest());
        doc.append("time_response", (Object)message.getTimeResponse());
        doc.append("create_time", VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
        return true;
    }

    @Override
    public LogVTCPayTopupMessage checkTrans(String transId) {
        LogVTCPayTopupMessage response = null;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("partner_trans_id", (Object)transId);
        Document dc = (Document)db.getCollection("log_topup_vtcpay").find((Bson)conditions).first();
        if (dc != null) {
            response = new LogVTCPayTopupMessage(dc.getString((Object)"reference_id"), dc.getString((Object)"partner_trans_id"), dc.getInteger((Object)"user_id").intValue(), dc.getString((Object)"user_name"), dc.getString((Object)"nick_name"), dc.getInteger((Object)"price").intValue(), dc.getLong((Object)"money_user").longValue(), dc.getInteger((Object)"status").intValue(), dc.getString((Object)"response_code"), dc.getString((Object)"description"), dc.getString((Object)"time_request"), dc.getString((Object)"time_response"));
        }
        return response;
    }
}

