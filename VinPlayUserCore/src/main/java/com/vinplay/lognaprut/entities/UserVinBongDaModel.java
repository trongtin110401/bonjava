package com.vinplay.lognaprut.entities;

public class UserVinBongDaModel {
    private String nickName;
    private String serviceName;
    private String actionName;
    private String description;
    private long currentMoney;
    private long moneyExchange;
    private long fee;
    private String transactionTime;

    public UserVinBongDaModel(String nickName, String serviceName, String actionName, String description, long currentMoney, long moneyExchange, long fee, String transactionTime) {
        this.nickName = nickName;
        this.serviceName = serviceName;
        this.actionName = actionName;
        this.description = description;
        this.currentMoney = currentMoney;
        this.moneyExchange = moneyExchange;
        this.fee = fee;
        this.transactionTime = transactionTime;
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

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
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

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Override
    public String toString() {
        return "UserVinBongDaModel{" +
                "nickName='" + nickName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", actionName='" + actionName + '\'' +
                ", description='" + description + '\'' +
                ", currentMoney=" + currentMoney +
                ", moneyExchange=" + moneyExchange +
                ", fee=" + fee +
                ", transactionTime='" + transactionTime + '\'' +
                '}';
    }

}
