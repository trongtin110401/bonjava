package com.vinplay.bongda.entities;

public class UserBetBongDa {
    String ID;
    String session;
    String idTran;
    String nickname;
    long moneyBet;
    int result; // kết quả , 0 là chưa 1 là win ăn tiền phải tr<ả
    long moneyWin;
    int betType; // 1-2 , 1: Doi A , 2  doi B
    double chapTrai; // mac dinh la 0
    double tiLeAn; //
    public String CreatedAt;
    public String UpdatedAt;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getIdTran() {
        return idTran;
    }

    public void setIdTran(String idTran) {
        this.idTran = idTran;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getMoneyBet() {
        return moneyBet;
    }

    public void setMoneyBet(long moneyBet) {
        this.moneyBet = moneyBet;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public long getMoneyWin() {
        return moneyWin;
    }

    public void setMoneyWin(long moneyWin) {
        this.moneyWin = moneyWin;
    }

    public int getBetType() {
        return betType;
    }

    public void setBetType(int betType) {
        this.betType = betType;
    }

    public double getChapTrai() {
        return chapTrai;
    }

    public void setChapTrai(double chapTrai) {
        this.chapTrai = chapTrai;
    }

    public double getTiLeAn() {
        return tiLeAn;
    }

    public void setTiLeAn(double tiLeAn) {
        this.tiLeAn = tiLeAn;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public UserBetBongDa(String ID, String session, String idTran, String nickname, long moneyBet, int result, long moneyWin, int betType, double chapTrai, double tiLeAn, String createdAt, String updatedAt) {
        this.ID = ID;
        this.session = session;
        this.idTran = idTran;
        this.nickname = nickname;
        this.moneyBet = moneyBet;
        this.result = result;
        this.moneyWin = moneyWin;
        this.betType = betType;
        this.chapTrai = chapTrai;
        this.tiLeAn = tiLeAn;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
    }
}
