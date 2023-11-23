package com.vinplay.api.backend.dlmuaban;

public class dlmuabanEntity {

    private String id;

    private Long dl_id;
    private String fullname;
    private String username;
    private String nickname;
    private String phone;
    private String khuvuc;
    private String telegram;
    private String facebook;
    private String zalo;
    private String bank;
    private String banknumber;
    private String bankname;
    private String note;

    public dlmuabanEntity() {
    }

    public dlmuabanEntity(Long dl_id, String fullname, String username, String nickname, String phone, String khuvuc, String telegram, String facebook, String zalo, String bank, String banknumber, String bankname, String note) {
        this.dl_id = dl_id;
        this.fullname = fullname;
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
        this.khuvuc = khuvuc;
        this.telegram = telegram;
        this.facebook = facebook;
        this.zalo = zalo;
        this.bank = bank;
        this.banknumber = banknumber;
        this.bankname = bankname;
        this.note = note;
    }

    public dlmuabanEntity(String id, Long dl_id, String fullname, String username, String nickname, String phone, String khuvuc, String telegram, String facebook, String zalo, String bank, String banknumber, String bankname, String note) {
        this.id = id;
        this.dl_id = dl_id;
        this.fullname = fullname;
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
        this.khuvuc = khuvuc;
        this.telegram = telegram;
        this.facebook = facebook;
        this.zalo = zalo;
        this.bank = bank;
        this.banknumber = banknumber;
        this.bankname = bankname;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDl_id() {
        return dl_id;
    }

    public void setDl_id(Long dl_id) {
        this.dl_id = dl_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getKhuvuc() {
        return khuvuc;
    }

    public void setKhuvuc(String khuvuc) {
        this.khuvuc = khuvuc;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getZalo() {
        return zalo;
    }

    public void setZalo(String zalo) {
        this.zalo = zalo;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBanknumber() {
        return banknumber;
    }

    public void setBanknumber(String banknumber) {
        this.banknumber = banknumber;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "dlmuabanEntity{" +
                "id='" + id + '\'' +
                ", dl_id=" + dl_id +
                ", fullname='" + fullname + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", khuvuc='" + khuvuc + '\'' +
                ", telegram='" + telegram + '\'' +
                ", facebook='" + facebook + '\'' +
                ", zalo='" + zalo + '\'' +
                ", bank='" + bank + '\'' +
                ", banknumber='" + banknumber + '\'' +
                ", bankname='" + bankname + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
