package com.vinplay.api.backend.dlmuaban;

public class yeucaumuaentity {
    private String id;

    private String train_id;
    private Long dlid;
    private String nickname;
    private Long tien;
    private String trangthai;
    private int sttcode;
    private String des;
    private String timelog;
    private String note;

    public yeucaumuaentity() {
    }

    public yeucaumuaentity(String train_id, Long dlid, String nickname, Long tien, String trangthai, int sttcode, String des, String timelog, String note) {
        this.train_id = train_id;
        this.dlid = dlid;
        this.nickname = nickname;
        this.tien = tien;
        this.trangthai = trangthai;
        this.sttcode = sttcode;
        this.des = des;
        this.timelog = timelog;
        this.note = note;
    }

    public yeucaumuaentity(String id, String train_id, Long dlid, String nickname, Long tien, String trangthai, int sttcode, String des, String timelog, String note) {
        this.id = id;
        this.train_id = train_id;
        this.dlid = dlid;
        this.nickname = nickname;
        this.tien = tien;
        this.trangthai = trangthai;
        this.sttcode = sttcode;
        this.des = des;
        this.timelog = timelog;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrain_id() {
        return train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public Long getDlid() {
        return dlid;
    }

    public void setDlid(Long dlid) {
        this.dlid = dlid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getTien() {
        return tien;
    }

    public void setTien(Long tien) {
        this.tien = tien;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public int getSttcode() {
        return sttcode;
    }

    public void setSttcode(int sttcode) {
        this.sttcode = sttcode;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getTimelog() {
        return timelog;
    }

    public void setTimelog(String timelog) {
        this.timelog = timelog;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "yeucaumuaentity{" +
                "id='" + id + '\'' +
                ", train_id='" + train_id + '\'' +
                ", dlid=" + dlid +
                ", nickname='" + nickname + '\'' +
                ", tien=" + tien +
                ", trangthai='" + trangthai + '\'' +
                ", sttcode=" + sttcode +
                ", des='" + des + '\'' +
                ", timelog='" + timelog + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
