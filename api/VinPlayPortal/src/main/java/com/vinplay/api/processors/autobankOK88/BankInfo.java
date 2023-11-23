package com.vinplay.api.processors.autobankOK88;

public class BankInfo {
    private String bank;
    private String stk;
    private String name;
    private String chinhanh;

    public BankInfo() {
    }

    public BankInfo(String bank, String stk, String name, String chinhanh) {
        this.bank = bank;
        this.stk = stk;
        this.name = name;
        this.chinhanh = chinhanh;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getStk() {
        return stk;
    }

    public void setStk(String stk) {
        this.stk = stk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChinhanh() {
        return chinhanh;
    }

    public void setChinhanh(String chinhanh) {
        this.chinhanh = chinhanh;
    }

    @Override
    public String toString() {
        return "BankInfo{" +
                "bank='" + bank + '\'' +
                ", stk='" + stk + '\'' +
                ", name='" + name + '\'' +
                ", chinhanh='" + chinhanh + '\'' +
                '}';
    }
}
