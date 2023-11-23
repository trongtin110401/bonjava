package com.vinplay.api.entities;

public class UserInfoAce {

    String cust_id;
    String cust_login;
    double balance;
    String status;
    String uid;

    public UserInfoAce() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserInfoAce(String cust_id, String cust_login, double balance, String status, String uid) {
        this.cust_id = cust_id;
        this.cust_login = cust_login;
        this.balance = balance;
        this.status = status;
        this.uid = uid;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getCust_login() {
        return cust_login;
    }

    public void setCust_login(String cust_login) {
        this.cust_login = cust_login;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
