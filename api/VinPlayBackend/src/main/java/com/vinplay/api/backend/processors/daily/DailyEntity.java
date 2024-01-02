package com.vinplay.api.backend.processors.daily;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class DailyEntity extends BaseResponseModel {
    private String id;

    private String username;

    private String referentCode;

    private String accessToken;

    private String nickName;

    private String bankAccount;

    private String bankNumber;

    private String accountName;

    private String cryptoAddressWallet;

    private String cryptoTypeWallet;

    public DailyEntity(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReferentCode() {
        return referentCode;
    }

    public void setReferentCode(String referentCode) {
        this.referentCode = referentCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCryptoAddressWallet() {
        return cryptoAddressWallet;
    }

    public void setCryptoAddressWallet(String cryptoAddressWallet) {
        this.cryptoAddressWallet = cryptoAddressWallet;
    }

    public String getCryptoTypeWallet() {
        return cryptoTypeWallet;
    }

    public void setCryptoTypeWallet(String cryptoTypeWallet) {
        this.cryptoTypeWallet = cryptoTypeWallet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
