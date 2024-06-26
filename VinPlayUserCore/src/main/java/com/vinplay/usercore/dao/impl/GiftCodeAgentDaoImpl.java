/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.Block
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.GiftCodeMessage
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.response.GiftCodeAgentResponse
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  com.vinplay.vbee.common.statics.TransType
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.usercore.dao.impl;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.usercore.dao.GiftCodeAgentDao;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.utils.TelegramUtil;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.GiftCodeMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.GiftCodeAgentResponse;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.statics.TransType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class GiftCodeAgentDaoImpl
implements GiftCodeAgentDao {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger((String)"backend");
    
    @Override
    public GiftCodeAgentResponse exportGiftCode(final GiftCodeMessage msg, long curentMoney, String nickName) {
        GiftCodeAgentResponse response = new GiftCodeAgentResponse();
        long moneyExport = msg.Quantity * (Integer.parseInt(msg.getPrice()) * 1000);
        if (moneyExport > curentMoney) {
            response.ErrorCode = 2;
        } else {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final String timeLog = df.format(new Date());
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            final MongoCollection giftCodeDB = db.getCollection("gift_code");
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("count_use", 0);
            conditions.put("price", msg.getPrice());
            conditions.put("type", msg.getType());
            conditions.put("release", msg.getRelease());
            long count = db.getCollection("gift_code_store").count((Bson)new Document(conditions));
            if (count >= (long)msg.getQuantity()) {
                new TelegramUtil().senMessToDaily(nickName, "Tạo gift code", -moneyExport, "Tạo " + msg.Quantity + " gift code mệnh giá : " + (Integer.parseInt(msg.getPrice()) * 1000) + " VND");
                UserServiceImpl service = new UserServiceImpl();
                MoneyResponse money = service.updateMoney(nickName, -moneyExport, "vin", "GcAgentExport", "\u0110\u1ea1i l\u00fd xu\u1ea5t Giftcode", "\u0110\u1ea1i l\u00fd " + nickName + " xu\u1ea5t giftcode m\u1ec7nh gi\u00e1: " + msg.getPrice() + "K, s\u1ed1 l\u01b0\u1ee3ng: " + msg.Quantity + " c\u00e1i.", 0L, null, TransType.NO_VIPPOINT);
                if (money.getErrorCode() == "1002") {
                    response.ErrorCode = 2;
                }
                if (money.isSuccess()) {
                    FindIterable iterable = db.getCollection("gift_code_store").find((Bson)new Document(conditions)).limit(msg.getQuantity());
                    iterable.forEach((Block)new Block<Document>(){

                        public void apply(Document document) {
                            Document doc = new Document();
                            doc.append("giftcode", (Object)document.getString((Object)"giftcode"));
                            doc.append("price", (Object)msg.getPrice());
                            doc.append("quantity", (Object)msg.getQuantity());
                            doc.append("source", (Object)msg.getSource());
                            doc.append("count_use", (Object)0);
                            doc.append("create_time", (Object)timeLog);
                            doc.append("money_type", (Object)msg.getMoneyType());
                            doc.append("release", (Object)msg.getRelease());
                            doc.append("nick_name", (Object)"");
                            doc.append("user_name", (Object)"");
                            doc.append("mobile", (Object)"");
                            doc.append("block", (Object)0);
                            doc.append("type", (Object)msg.getType());
                            doc.append("giftcodefull", (Object)(msg.getRelease() + msg.getPrice() + msg.getSource() + document.getString((Object)"giftcode")));
                            doc.append("update_time", (Object)"");
                            doc.append("agent", (Object)"1");
                            giftCodeDB.insertOne((Object)doc);
                            try {
                                GiftCodeMessage message = new GiftCodeMessage();
                                message.setGiftCode(document.getString((Object)"giftcode"));
                                RMQApi.publishMessage((String)"queue_gift_code", (BaseMessage)message, (int)1200);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    response.ErrorCode = 0;
                    response.CurrentMoney = money.getCurrentMoney();
                }
            } else {
                response.ErrorCode = 1;
            }
        }
        logger.error(response.ErrorCode);
        return response;
    }

}

