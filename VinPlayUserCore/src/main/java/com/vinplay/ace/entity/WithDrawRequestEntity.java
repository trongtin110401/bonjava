package com.vinplay.ace.entity;

import com.vinplay.ace.AceRequestEntity;

public class WithDrawRequestEntity extends AceRequestEntity {

    String user_id;
    String token ;
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

    public WithDrawRequestEntity() {
    }

    public WithDrawRequestEntity(String user_id, String token, long money, String keysc) {
        this.user_id = user_id;
        this.token = token;
        this.money = money;
        this.keysc = keysc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
