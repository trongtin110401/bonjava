package com.vinplay.usercore.entities;

public class UserOnePayTransaction {
    String username;
    String transId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public UserOnePayTransaction(String username, String transId) {
        this.username = username;
        this.transId = transId;
    }
}
