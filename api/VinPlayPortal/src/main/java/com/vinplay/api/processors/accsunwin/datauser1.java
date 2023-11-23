package com.vinplay.api.processors.accsunwin;

import java.util.Arrays;

public class datauser1 {
    private String[] walletType;
    private String token;
    private String displayName;
    private String wallet;
    private String userId;
    private String walletVersion;
    private String avatar;
    private String type;
    private String pWallet;

    public datauser1() {
    }

    public datauser1(String[] walletType, String token, String displayName, String wallet, String userId, String walletVersion, String avatar, String type, String pWallet) {
        this.walletType = walletType;
        this.token = token;
        this.displayName = displayName;
        this.wallet = wallet;
        this.userId = userId;
        this.walletVersion = walletVersion;
        this.avatar = avatar;
        this.type = type;
        this.pWallet = pWallet;
    }

    public String[] getWalletType() {
        return walletType;
    }

    public void setWalletType(String[] walletType) {
        this.walletType = walletType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWalletVersion() {
        return walletVersion;
    }

    public void setWalletVersion(String walletVersion) {
        this.walletVersion = walletVersion;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getpWallet() {
        return pWallet;
    }

    public void setpWallet(String pWallet) {
        this.pWallet = pWallet;
    }

    @Override
    public String toString() {
        return "datauser1{" +
                "walletType=" + Arrays.toString(walletType) +
                ", token='" + token + '\'' +
                ", displayName='" + displayName + '\'' +
                ", wallet='" + wallet + '\'' +
                ", userId='" + userId + '\'' +
                ", walletVersion='" + walletVersion + '\'' +
                ", avatar='" + avatar + '\'' +
                ", type='" + type + '\'' +
                ", pWallet='" + pWallet + '\'' +
                '}';
    }
}
