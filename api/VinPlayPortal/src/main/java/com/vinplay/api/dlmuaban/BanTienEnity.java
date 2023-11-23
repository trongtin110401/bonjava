package com.vinplay.api.dlmuaban;

public class BanTienEnity {
    private String id;
    private String trainID;
    private String bank_nhan;
    private String stk_nhan;
    private String name_nhan;
    private int tien;

    private String trangthai;
    private String sttcode;

    private String nickname;

    private String nickname_dl;

    private long dlid;

    private String timelog;
    private String note;

    public BanTienEnity() {
    }

    public BanTienEnity(String trainID, String bank_nhan, String stk_nhan, String name_nhan, int tien, String trangthai, String sttcode, String nickname, String nickname_dl, long dlid, String timelog, String note) {
        this.trainID = trainID;
        this.bank_nhan = bank_nhan;
        this.stk_nhan = stk_nhan;
        this.name_nhan = name_nhan;
        this.tien = tien;
        this.trangthai = trangthai;
        this.sttcode = sttcode;
        this.nickname = nickname;
        this.nickname_dl = nickname_dl;
        this.dlid = dlid;
        this.timelog = timelog;
        this.note = note;
    }

    public BanTienEnity(String id, String trainID, String bank_nhan, String stk_nhan, String name_nhan, int tien, String trangthai, String sttcode, String nickname, String nickname_dl, long dlid, String timelog, String note) {
        this.id = id;
        this.trainID = trainID;
        this.bank_nhan = bank_nhan;
        this.stk_nhan = stk_nhan;
        this.name_nhan = name_nhan;
        this.tien = tien;
        this.trangthai = trangthai;
        this.sttcode = sttcode;
        this.nickname = nickname;
        this.nickname_dl = nickname_dl;
        this.dlid = dlid;
        this.timelog = timelog;
        this.note = note;
    }

    public String getTimelog() {
        return timelog;
    }

    public void setTimelog(String timelog) {
        this.timelog = timelog;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrainID() {
        return trainID;
    }

    public void setTrainID(String trainID) {
        this.trainID = trainID;
    }

    public String getBank_nhan() {
        return bank_nhan;
    }

    public void setBank_nhan(String bank_nhan) {
        this.bank_nhan = bank_nhan;
    }

    public String getStk_nhan() {
        return stk_nhan;
    }

    public void setStk_nhan(String stk_nhan) {
        this.stk_nhan = stk_nhan;
    }

    public String getName_nhan() {
        return name_nhan;
    }

    public void setName_nhan(String name_nhan) {
        this.name_nhan = name_nhan;
    }

    public int getTien() {
        return tien;
    }

    public void setTien(int tien) {
        this.tien = tien;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getSttcode() {
        return sttcode;
    }

    public void setSttcode(String sttcode) {
        this.sttcode = sttcode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname_dl() {
        return nickname_dl;
    }

    public void setNickname_dl(String nickname_dl) {
        this.nickname_dl = nickname_dl;
    }

    public long getDlid() {
        return dlid;
    }

    public void setDlid(long dlid) {
        this.dlid = dlid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "BanTienEnity{" +
                "id='" + id + '\'' +
                ", trainID='" + trainID + '\'' +
                ", bank_nhan='" + bank_nhan + '\'' +
                ", stk_nhan='" + stk_nhan + '\'' +
                ", name_nhan='" + name_nhan + '\'' +
                ", tien=" + tien +
                ", trangthai='" + trangthai + '\'' +
                ", sttcode='" + sttcode + '\'' +
                ", nickname='" + nickname + '\'' +
                ", nickname_dl='" + nickname_dl + '\'' +
                ", dlid=" + dlid +
                ", timelog='" + timelog + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
