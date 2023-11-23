package com.vinplay.ace.entity;

import com.vinplay.ace.AceRequestEntity;

public class DepositRequestEntity extends AceRequestEntity {

    String user_id;
    String user_name;
    String display_name;
    String token;
    int avatar ;
    long money;
    String keysc;
    String ticket_id;
    String brand ="sunvin";

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public DepositRequestEntity() {
    }

    public DepositRequestEntity(String user_id, String user_name, String display_name,
                                String token, int avatar, long money, String keysc) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.display_name = display_name;
        this.token = token;
        this.avatar = avatar;
        this.money = money;
        this.keysc = keysc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public String getKeysc() {
        return keysc;
    }

    public void setKeysc(String keysc) {
        this.keysc = keysc;
    }
}
