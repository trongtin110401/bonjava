package com.vinplay.dal.entities.report;

import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import org.bson.Document;

public class ReportMoneyModelNew {

    private String nickName;
    private String actionName;
    private String reportDate;

    public long fee = 0L;
    public long revenue = 0L;
    public boolean isGame = false;
    public long totalOut;
    public long totalRefund;
    public long totalIn;

    public ReportMoneyModelNew() {

    }

    public ReportMoneyModelNew(LogMoneyUserMessage log) {
        this.nickName = log.getNickname();
        this.actionName = log.getActionName();
        this.fee = log.getFee();
        this.reportDate = log.getCreateTime().substring(0,10);
    }

    public ReportMoneyModelNew(Document doc) {
        this.nickName = doc.getString("nick_name");
        this.actionName = doc.getString("action_name");
        this.fee = doc.getLong("fee");
        this.totalOut = doc.getLong("total_out");
        this.totalRefund = doc.getLong("total_refund");
        this.totalIn = doc.getLong("total_in");
    }

    public void exchange(long moneyExchange){
        if (moneyExchange < 0) {
            this.totalOut = (-1) *moneyExchange;
        }else{
            this.totalIn = moneyExchange;
        }
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }


    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }


    public boolean isGame() {
        return isGame;
    }

    public void setGame(boolean game) {
        isGame = game;
    }


}
