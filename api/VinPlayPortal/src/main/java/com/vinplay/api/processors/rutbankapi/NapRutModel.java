package com.vinplay.api.processors.rutbankapi;

public class NapRutModel {
    private String TranID;
    private String nickName;
    private String maDaily;
    private long soTien;
    private String hinhThucTran;
    private String CreateAt;

    public NapRutModel() {
    }

    public NapRutModel(String tranID, String nickName, String maDaily, long soTien, String hinhThucTran, String createAt) {
        TranID = tranID;
        this.nickName = nickName;
        this.maDaily = maDaily;
        this.soTien = soTien;
        this.hinhThucTran = hinhThucTran;
        CreateAt = createAt;
    }

    public String getTranID() {
        return TranID;
    }

    public void setTranID(String tranID) {
        TranID = tranID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMaDaily() {
        return maDaily;
    }

    public void setMaDaily(String maDaily) {
        this.maDaily = maDaily;
    }

    public long getSoTien() {
        return soTien;
    }

    public void setSoTien(long soTien) {
        this.soTien = soTien;
    }

    public String getHinhThucTran() {
        return hinhThucTran;
    }

    public void setHinhThucTran(String hinhThucTran) {
        this.hinhThucTran = hinhThucTran;
    }

    public String getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(String createAt) {
        CreateAt = createAt;
    }

    @Override
    public String toString() {
        return "NapRutModel{" +
                "TranID='" + TranID + '\'' +
                ", nickName='" + nickName + '\'' +
                ", maDaily='" + maDaily + '\'' +
                ", soTien=" + soTien +
                ", hinhThucTran='" + hinhThucTran + '\'' +
                ", CreateAt='" + CreateAt + '\'' +
                '}';
    }
}
