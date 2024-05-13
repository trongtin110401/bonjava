/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

public class LogUserMoneyResponse {
    public long transId;
    public String userName;
    public String nickName;
    public String serviceName;
    public String description;
    public long currentMoney;
    public long moneyExchange;
    public String transactionTime;
    public String actionName;
    public long fee;
    public long createdTime;
    public String sender_nick_name;
    public String receiver_nick_name;
    public String action;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(long currentMoney) {
        this.currentMoney = currentMoney;
    }

    public long getMoneyExchange() {
        return moneyExchange;
    }

    public void setMoneyExchange(long moneyExchange) {
        this.moneyExchange = moneyExchange;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getSender_nick_name() {
        return sender_nick_name;
    }

    public void setSender_nick_name(String sender_nick_name) {
        this.sender_nick_name = sender_nick_name;
    }

    public String getReceiver_nick_name() {
        return receiver_nick_name;
    }

    public void setReceiver_nick_name(String receiver_nick_name) {
        this.receiver_nick_name = receiver_nick_name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

