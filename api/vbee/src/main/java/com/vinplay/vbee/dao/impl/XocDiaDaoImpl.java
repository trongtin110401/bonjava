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

import bitzero.util.common.business.Debug;
import com.vinplay.vbee.common.messages.TransactionXocDiaMessage;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.dao.XocDiaDao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class XocDiaDaoImpl
        implements XocDiaDao {


    @Override
    public void saveTransactionXocDia(TransactionXocDiaMessage message) throws SQLException {
        System.out.println("ready to save data xoc dia");
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        if (conn == null) {
            Debug.info("Connection in saveResultTaiXiu is null");
        }
//        CallableStatement call = null;
//        call = conn.prepareCall("CALL save_result_tai_xiu_md5(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//        int param = 1;
//
//        try {
//            success = call.execute();
//        } catch (Exception ex) {
//            Debug.info("saveResultTaiXiu error" + ex.getMessage());
//        }
//        if (call != null) {
//            call.close();
//        }
        if (conn != null) {
            conn.close();
        }
    }
}

