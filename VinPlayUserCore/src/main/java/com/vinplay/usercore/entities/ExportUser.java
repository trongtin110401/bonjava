/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinplay.usercore.entities;

/**
 *
 * @author Ha Doan
 */
public class ExportUser {
    private String nick_name;
    private String mobile;
    private long recharge_money;

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getRecharge_money() {
        return recharge_money;
    }

    public void setRecharge_money(long recharge_money) {
        this.recharge_money = recharge_money;
    }
}
