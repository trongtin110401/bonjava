package com.vinplay.api.entities;

import bitzero.server.util.MD5;

public class DepositSabaModel {
    public static final String DOMAIN = "https://cmdlive.isagents.asia";
    public static final String GAMENAME = "sunvin";
    public static final String PrivateKey = "e807f1fcf82d132f9bb018ca6738a19f";
    public String gameName;
    public Long accountID;
    public String userName;
    public String nickName;
    public Long amount;
    public String signature;

    public DepositSabaModel(String gameName, Long accountID, String userName, String nickName, Long amount) {
        this.gameName = gameName;
        this.accountID = accountID;
        this.userName = userName;
        this.nickName = nickName;
        this.amount = amount;
    }

    public String getMd5Deposit() {
        String rawData = "sunvin"+accountID + userName + nickName + PrivateKey;
        System.out.println(rawData);
        return MD5.getInstance().getHash(rawData);
    }
}
