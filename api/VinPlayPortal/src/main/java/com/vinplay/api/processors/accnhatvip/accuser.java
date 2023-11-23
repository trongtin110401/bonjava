package com.vinplay.api.processors.accnhatvip;

public class accuser {
    private String token;
    private String cust_id;
    private String cust_login;
    private String currency;
    private String balance;
    private String status;
    private String uid;

    public accuser() {
    }

    public accuser(String token, String cust_id, String cust_login, String currency, String balance, String status, String uid) {
        this.token = token;
        this.cust_id = cust_id;
        this.cust_login = cust_login;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "accuser{" +
                "token='" + token + '\'' +
                ", cust_id='" + cust_id + '\'' +
                ", cust_login='" + cust_login + '\'' +
                ", currency='" + currency + '\'' +
                ", balance='" + balance + '\'' +
                ", status='" + status + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
