/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinplay.dichvuthe.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;

/**
 *
 * @author HA
 */
public class LogSMSDAOImpl implements LogSMSDAO {
    
    @Override
    public boolean saveLog(String request_id, String mobile, String message, String status, String transTime, String command_code)
    {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("vmg_transaction");
        Document doc = new Document();
        doc.append("request_id", (Object)request_id);
        doc.append("mobile", (Object)mobile);
        doc.append("message_MT", (Object)"OTP");
        doc.append("response_MT", (Object)"1");
        doc.append("message_MO", (Object)message);
        doc.append("response_MO", (Object)status);
        doc.append("trans_time", (Object)transTime);
        doc.append("command_code", (Object)command_code);                
        col.insertOne((Object)doc);
        return true;
    }
}
