package com.vinplay.dal.entities.report;

import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import org.bson.Document;

public class ReportMoneyModelNew {

    private String nickName;
    private String actionName;
    private String reportDate;

    public long moneyWin = 0L;
    public long moneyLost = 0L;
    public long moneyOther = 0L;
    public long fee = 0L;
    public long moneyExchange = 0L;
    public long revenue = 0L;
    public boolean isGame = false;

    public ReportMoneyModelNew() {

    }

    public ReportMoneyModelNew(LogMoneyUserMessage log) {
        this.nickName = log.getNickname();
        this.actionName = log.getActionName();
        this.fee = log.getFee();
        this.moneyExchange = log.getMoneyExchange();
        this.reportDate = log.getCreateTime().substring(0,10);
    }

    public ReportMoneyModelNew(Document doc) {
        this.nickName = doc.getString("nick_name");
        this.actionName = doc.getString("action_name");
        this.moneyExchange = doc.getLong("money_exchange");
        this.fee = doc.getLong("fee");
        this.moneyWin = doc.getLong("money_win");
        this.moneyLost = doc.getLong("money_lost");
        this.moneyOther = doc.getLong("money_other");
        this.revenue = doc.getLong("revenue");
    }

    public void exchange(LogMoneyUserMessage log){
        this.moneyOther += log.getMoneyExchange();
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

    public long getMoneyWin() {
        return moneyWin;
    }

    public void setMoneyWin(long moneyWin) {
        this.moneyWin = moneyWin;
    }

    public long getMoneyLost() {
        return moneyLost;
    }

    public void setMoneyLost(long moneyLost) {
        this.moneyLost = moneyLost;
    }

    public long getMoneyOther() {
        return moneyOther;
    }

    public void setMoneyOther(long moneyOther) {
        this.moneyOther = moneyOther;
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

    public long getMoneyExchange() {
        return this.moneyExchange;
    }

    public void setMoneyExchange(long moneyExchange) {
        this.moneyExchange = moneyExchange;
    }

}
