package com.vinplay.api.dlmuaban;

public class bankdlEntity {
    private String id;
    private Long dlid;
    private String bank;
    private String bankname;
    private String banknum;
    private String chinhanh;

    public bankdlEntity() {
    }

    public bankdlEntity(String id, Long dlid, String bank, String bankname, String banknum, String chinhanh) {
        this.id = id;
        this.dlid = dlid;
        this.bank = bank;
        this.bankname = bankname;
        this.banknum = banknum;
        this.chinhanh = chinhanh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDlid() {
        return dlid;
    }

    public void setDlid(Long dlid) {
        this.dlid = dlid;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBanknum() {
        return banknum;
    }

    public void setBanknum(String banknum) {
        this.banknum = banknum;
    }

    public String getChinhanh() {
        return chinhanh;
    }

    public void setChinhanh(String chinhanh) {
        this.chinhanh = chinhanh;
    }

    @Override
    public String toString() {
        return "bankdlEntity{" +
                "id='" + id + '\'' +
                ", dlid=" + dlid +
                ", bank='" + bank + '\'' +
                ", bankname='" + bankname + '\'' +
                ", banknum='" + banknum + '\'' +
                ", chinhanh='" + chinhanh + '\'' +
                '}';
    }
}
