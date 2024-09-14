/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.LogMoneyUserMessage
 *  com.vinplay.vbee.common.statics.Consts
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq.log.processor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.dal.entities.report.ReportMoneyModelNew;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.dao.impl.LogMoneyUserDaoImpl;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class LogMoneyUserProcessor implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String) "vbee");

    //todo: Log money user thay đổi - LogMoneyUserProcessor
    public Boolean execute(Param<byte[]> param) {
        LogMoneyUserMessage message = (LogMoneyUserMessage) BaseMessage.fromBytes((byte[]) ((byte[]) param.get()));
        //if (message.isBot() && Math.abs(message.getMoneyExchange()) <= 100000L) {
        if (message.isBot()) {
            //logger.info((Object)("Khong xu ly bot: " + message.getNickname() + ", money exchange= " + message.getMoneyExchange()));
        } else {
            LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
            long transId = 0L;
            int queryType = -1;
            if (message.getMoneyType().equals("vin")) {
                transId = ++com.vinplay.vbee.main.VBeeMain.moneyVinReferenceId;
            } else if (message.getMoneyType().equals("xu")) {
                transId = ++com.vinplay.vbee.main.VBeeMain.moneyXuReferenceId;
            }

            dao.saveLogMoneyUser(message, transId, message.isBot(), message.isVp());

            //20240914
            try {
                upsertReportMoney(message);
            } catch (Exception e) {
            }


            //===================
            if (message.getMoneyType().equalsIgnoreCase("vin")) {
                if (message.getMoneyExchange() > 0L) {
                    if (Consts.NAP_VIN.contains(message.getActionName())) {
                        queryType = 3;
                        dao.saveLogMoneyUserVinOther(message, transId, queryType);
                    }
                } else if (Consts.TIEU_VIN.contains(message.getActionName())) {
                    queryType = 5;
                    dao.saveLogMoneyUserVinOther(message, transId, queryType);
                }
            }
        }
        return true;
    }

    private void upsertReportMoney(LogMoneyUserMessage log) {

        ReportMoneyModelNew report = createReportMoney(log);

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> col = db.getCollection("report_money_game");
        Map<String, Object> map = new HashMap<>();
        map.put("nick_name", report.getNickName());
        map.put("action_name", report.getActionName());
        map.put("report_date", report.getReportDate());


        // Define the query filter
        Document query = new Document(map);

        // Define the update operation
        Document update = new Document("$inc",
                new Document("total_bet", report.totalBet)
                        .append("total_refund", report.totalRefund)
                        .append("total_win", report.totalWin)
                        .append("fee", report.getFee())
                        .append("revenue", report.getRevenue())
        );

        // Define the options (upsert: true)
        UpdateOptions options = new UpdateOptions().upsert(true);

        // Perform the update operation with upsert
        col.updateOne(query, update, options);

    }

    private ReportMoneyModelNew createReportMoney(LogMoneyUserMessage log) {
        ReportMoneyModelNew report = new ReportMoneyModelNew(log);
        if (Consts.GAMES.contains(log.getActionName()) || "Exchange".contains(log.getActionName())) {
            report = processGame(report, log);
        }
        return report;
    }


    private ReportMoneyModelNew processGame(ReportMoneyModelNew model, LogMoneyUserMessage log) {
        model.isGame = true;
        if (log.getActionName().equals(Consts.TAI_XIU)) {
            if (log.getMoneyExchange() < 0) {
                model.setMoneyLost(model.getMoneyLost() + log.getMoneyExchange());
                model.totalBet =  (-1) * log.getMoneyExchange();
            } else if (log.getServiceName().contains("Hoàn trả")) {
                model.totalRefund = log.getMoneyExchange();
            } else {
                model.totalWin = log.getMoneyExchange();
            }
        } else if (
            // these code for SLOT MACHINE GAME ONLY
                (log.getActionName().equals(Games.MINI_POKER.getName())
                        || log.getActionName().equals(Games.CANDY.getName())
                        || log.getActionName().equals(Games.FAST_AND_FURIOUS.getName())
                        || log.getActionName().equals(Games.SEXY_DANCE.getName())
                        || log.getActionName().equals(Games.COWBOY.getName())
                        || log.getActionName().equals(Games.LADY_NIGHT.getName())
                        || log.getActionName().equals(Games.BONG_LAI_CAC.getName())
                        || log.getActionName().equals(Games.LIEN_MINH.getName())
                        || log.getActionName().equals(Games.LAS_VEGAS.getName())
                        || log.getActionName().equals(Games.HALLOWEEN.getName())
                        || log.getActionName().equals(Games.BIG_CITY_BOY.getName())
                        && log.getDescription().startsWith("Đặt cược"))) {
            if (log.getMoneyExchange() < 0) {
                model.totalBet = (-1) * log.getMoneyExchange();
            } else {
                model.totalWin = log.getMoneyExchange();
            }
        } else{
            if (log.getMoneyExchange() < 0) {
                model.totalBet = (-1) * log.getMoneyExchange();
            } else {
                model.totalWin = log.getMoneyExchange();
            }
        }
        model.fee = log.getFee();
        //model.moneyExchange = log.getMoneyExchange();
        model.revenue = (model.totalBet - model.totalWin - model.totalRefund);

        return model;
    }


}

