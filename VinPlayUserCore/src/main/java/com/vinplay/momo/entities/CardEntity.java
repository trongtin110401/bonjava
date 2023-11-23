package com.vinplay.momo.entities;

public class CardEntity {
    String seri;
    String code;
    String telco;
    int value;

    @Override
    public String toString() {
        return "CardEntity{" +
                "seri='" + seri + '\'' +
                ", code='" + code + '\'' +
                ", telco='" + telco + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public CardEntity(String seri, String code, String telco, int value) {
        this.seri = seri;
        this.code = code;
        this.telco = telco;
        this.value = value;
    }

    public CardEntity() {
    }

    public String getSeri() {
        return seri;
    }

    public void setSeri(String seri) {
        this.seri = seri;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTelco() {
        return telco;
    }

    public void setTelco(String telco) {
        this.telco = telco;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
