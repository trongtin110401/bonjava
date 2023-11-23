/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

public class LogAgentTranferMoneyResponse {
    public String nick_name_send;
    public String nick_name_receive;
    public long money_send;
    public long money_receive;
    public int status;
    public long fee;
    public String trans_time;
    public int top_ds;
    public int process;
    public String des_send;
    public String des_receive;

    public LogAgentTranferMoneyResponse() {
    }

    public LogAgentTranferMoneyResponse(String nick_name_send, String nick_name_receive, long money_send, long money_receive, int status, long fee, String trans_time, int top_ds, int process, String des_send, String des_receive) {
        this.nick_name_send = nick_name_send;
        this.nick_name_receive = nick_name_receive;
        this.money_send = money_send;
        this.money_receive = money_receive;
        this.status = status;
        this.fee = fee;
        this.trans_time = trans_time;
        this.top_ds = top_ds;
        this.process = process;
        this.des_send = des_send;
        this.des_receive = des_receive;
    }

    public String getNick_name_send() {
        return nick_name_send;
    }

    public void setNick_name_send(String nick_name_send) {
        this.nick_name_send = nick_name_send;
    }

    public String getNick_name_receive() {
        return nick_name_receive;
    }

    public void setNick_name_receive(String nick_name_receive) {
        this.nick_name_receive = nick_name_receive;
    }

    public long getMoney_send() {
        return money_send;
    }

    public void setMoney_send(long money_send) {
        this.money_send = money_send;
    }

    public long getMoney_receive() {
        return money_receive;
    }

    public void setMoney_receive(long money_receive) {
        this.money_receive = money_receive;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public String getTrans_time() {
        return trans_time;
    }

    public void setTrans_time(String trans_time) {
        this.trans_time = trans_time;
    }

    public int getTop_ds() {
        return top_ds;
    }

    public void setTop_ds(int top_ds) {
        this.top_ds = top_ds;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public String getDes_send() {
        return des_send;
    }

    public void setDes_send(String des_send) {
        this.des_send = des_send;
    }

    public String getDes_receive() {
        return des_receive;
    }

    public void setDes_receive(String des_receive) {
        this.des_receive = des_receive;
    }

    @Override
    public String toString() {
        return "LogAgentTranferMoneyResponse{" +
                "nick_name_send='" + nick_name_send + '\'' +
                ", nick_name_receive='" + nick_name_receive + '\'' +
                ", money_send=" + money_send +
                ", money_receive=" + money_receive +
                ", status=" + status +
                ", fee=" + fee +
                ", trans_time='" + trans_time + '\'' +
                ", top_ds=" + top_ds +
                ", process=" + process +
                ", des_send='" + des_send + '\'' +
                ", des_receive='" + des_receive + '\'' +
                '}';
    }
}

