package com.vinplay.vbee.common.response.money;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class EventReChargeMoneyProcess extends BaseResponseModel {

    String cacheNapBank;
    String cacheNap1Pay;
    String cacheNapMomo;
    String cacheNapTheCao;
    String cacheRutTienBank;
    String cacheRutTienTheCao;
    String cacheMaOTP;
    String cacheLogin;

    public EventReChargeMoneyProcess(boolean success, String errorCode, String cacheNapBank, String cacheNap1Pay, String cacheNapMomo, String cacheNapTheCao, String cacheRutTienBank, String cacheRutTienTheCao, String cacheMaOTP, String cacheLogin) {
        super(success, errorCode);
        this.cacheNapBank = cacheNapBank;
        this.cacheNap1Pay = cacheNap1Pay;
        this.cacheNapMomo = cacheNapMomo;
        this.cacheNapTheCao = cacheNapTheCao;
        this.cacheRutTienBank = cacheRutTienBank;
        this.cacheRutTienTheCao = cacheRutTienTheCao;
        this.cacheMaOTP = cacheMaOTP;
        this.cacheLogin = cacheLogin;
    }

    public String getCacheNapBank() {
        return cacheNapBank;
    }

    public void setCacheNapBank(String cacheNapBank) {
        this.cacheNapBank = cacheNapBank;
    }

    public String getCacheNap1Pay() {
        return cacheNap1Pay;
    }

    public void setCacheNap1Pay(String cacheNap1Pay) {
        this.cacheNap1Pay = cacheNap1Pay;
    }

    public String getCacheNapMomo() {
        return cacheNapMomo;
    }

    public void setCacheNapMomo(String cacheNapMomo) {
        this.cacheNapMomo = cacheNapMomo;
    }

    public String getCacheNapTheCao() {
        return cacheNapTheCao;
    }

    public void setCacheNapTheCao(String cacheNapTheCao) {
        this.cacheNapTheCao = cacheNapTheCao;
    }

    public String getCacheRutTienBank() {
        return cacheRutTienBank;
    }

    public void setCacheRutTienBank(String cacheRutTienBank) {
        this.cacheRutTienBank = cacheRutTienBank;
    }

    public String getCacheRutTienTheCao() {
        return cacheRutTienTheCao;
    }

    public void setCacheRutTienTheCao(String cacheRutTienTheCao) {
        this.cacheRutTienTheCao = cacheRutTienTheCao;
    }

    public String getCacheMaOTP() {
        return cacheMaOTP;
    }

    public void setCacheMaOTP(String cacheMaOTP) {
        this.cacheMaOTP = cacheMaOTP;
    }

    public String getCacheLogin() {
        return cacheLogin;
    }

    public void setCacheLogin(String cacheLogin) {
        this.cacheLogin = cacheLogin;
    }

    @Override
    public String toString() {
        return "EventReChargeMoneyProcess{" +
                "cacheNapBank='" + cacheNapBank + '\'' +
                ", cacheNap1Pay='" + cacheNap1Pay + '\'' +
                ", cacheNapMomo='" + cacheNapMomo + '\'' +
                ", cacheNapTheCao='" + cacheNapTheCao + '\'' +
                ", cacheRutTienBank='" + cacheRutTienBank + '\'' +
                ", cacheRutTienTheCao='" + cacheRutTienTheCao + '\'' +
                ", cacheMaOTP='" + cacheMaOTP + '\'' +
                ", cacheLogin='" + cacheLogin + '\'' +
                '}';
    }
}
