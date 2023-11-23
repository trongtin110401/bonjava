package com.vinplay.bongda.entities;

public class KeoBongDa {
    public String session;
    public String Id;
    public String doiA;
    public String doiB;
    public int banThangDoiA;
    public int banThangDoiB;

    public int banThangHiep1DoiA = 0;
    public int banThangHiep2DoiA = 0;
    public int banThangHiep1DoiB = 0;
    public int banThangHiep2DoiB = 0;

    public String thoiGianDa;


    public String tiLeDoiAChapCaTran;
    public String tiLeDoiBChapCaTran;
    public String tileDoiAChapTaiXiu;
    public String tileDoiBChapTaiXiu;
    public String tileDoiAChapHiep1;
    public String tileDoiBChapHiep1;
    public String tileDoiAChapHiep2;
    public String tileDoiBChapHiep2;


    public double tileAnDoiACaTran;
    public double tileAnDoiBCaTran;
    public double tileAnDoiATaiXiu;
    public double tileAnDoiBTaiXiu;
    public double tileAnDoiAHiep1;
    public double tileAnDoiBHiep1;
    public double tileAnDoiAHiep2;
    public double tileAnDoiBHiep2;

    public String CreatedAt;

    public String UpdatedAt;

    public String url = "";

    public String getTileDoiBChapHiep1() {
        return tileDoiBChapHiep1;
    }

    public void setTileDoiBChapHiep1(String tileDoiBChapHiep1) {
        this.tileDoiBChapHiep1 = tileDoiBChapHiep1;
    }

    public String getTileDoiAChapHiep2() {
        return tileDoiAChapHiep2;
    }

    public void setTileDoiAChapHiep2(String tileDoiAChapHiep2) {
        this.tileDoiAChapHiep2 = tileDoiAChapHiep2;
    }

    public KeoBongDa(String session, String id, String doiA, String doiB, int banThangDoiA, int banThangDoiB, String thoiGianDa, String tiLeDoiAChapCaTran, String tiLeDoiBChapCaTran, String tileDoiAChapTaiXiu, String tileDoiBChapTaiXiu, String tileDoiAChapHiep1, String tileDoiBChapHiep1, String tileDoiAChapHiep2, String tileDoiBChapHiep2, double tileAnDoiACaTran, double tileAnDoiBCaTran, double tileAnDoiATaiXiu, double tileAnDoiBTaiXiu, double tileAnDoiAHiep1, double tileAnDoiBHiep1, double tileAnDoiAHiep2, double tileAnDoiBHiep2, String createdAt, String updatedAt, int status) {
        this.session = session;
        Id = id;
        this.doiA = doiA;
        this.doiB = doiB;
        this.banThangDoiA = banThangDoiA;
        this.banThangDoiB = banThangDoiB;
        this.thoiGianDa = thoiGianDa;
        this.tiLeDoiAChapCaTran = tiLeDoiAChapCaTran;
        this.tiLeDoiBChapCaTran = tiLeDoiBChapCaTran;
        this.tileDoiAChapTaiXiu = tileDoiAChapTaiXiu;
        this.tileDoiBChapTaiXiu = tileDoiBChapTaiXiu;
        this.tileDoiAChapHiep1 = tileDoiAChapHiep1;
        this.tileDoiBChapHiep1 = tileDoiBChapHiep1;
        this.tileDoiAChapHiep2 = tileDoiAChapHiep2;
        this.tileDoiBChapHiep2 = tileDoiBChapHiep2;
        this.tileAnDoiACaTran = tileAnDoiACaTran;
        this.tileAnDoiBCaTran = tileAnDoiBCaTran;
        this.tileAnDoiATaiXiu = tileAnDoiATaiXiu;
        this.tileAnDoiBTaiXiu = tileAnDoiBTaiXiu;
        this.tileAnDoiAHiep1 = tileAnDoiAHiep1;
        this.tileAnDoiBHiep1 = tileAnDoiBHiep1;
        this.tileAnDoiAHiep2 = tileAnDoiAHiep2;
        this.tileAnDoiBHiep2 = tileAnDoiBHiep2;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public KeoBongDa(String session, String id, String doiA, String doiB, int banThangDoiA, int banThangDoiB, String thoiGianDa, String tiLeDoiAChapCaTran, String tiLeDoiBChapCaTran, String tileDoiAChapTaiXiu, String tileDoiBChapTaiXiu, String tileDoiAChapHiep1, String tileDoiBChapHiep2, double tileAnDoiACaTran, double tileAnDoiBCaTran, double tileAnDoiATaiXiu, double tileAnDoiBTaiXiu, double tileAnDoiAHiep1, double tileAnDoiBHiep1, double tileAnDoiAHiep2, double tileAnDoiBHiep2, String createdAt, String updatedAt, int status) {
        this.session = session;
        Id = id;
        this.doiA = doiA;
        this.doiB = doiB;
        this.banThangDoiA = banThangDoiA;
        this.banThangDoiB = banThangDoiB;
        this.thoiGianDa = thoiGianDa;
        this.tiLeDoiAChapCaTran = tiLeDoiAChapCaTran;
        this.tiLeDoiBChapCaTran = tiLeDoiBChapCaTran;
        this.tileDoiAChapTaiXiu = tileDoiAChapTaiXiu;
        this.tileDoiBChapTaiXiu = tileDoiBChapTaiXiu;
        this.tileDoiAChapHiep1 = tileDoiAChapHiep1;
        this.tileDoiBChapHiep2 = tileDoiBChapHiep2;
        this.tileAnDoiACaTran = tileAnDoiACaTran;
        this.tileAnDoiBCaTran = tileAnDoiBCaTran;
        this.tileAnDoiATaiXiu = tileAnDoiATaiXiu;
        this.tileAnDoiBTaiXiu = tileAnDoiBTaiXiu;
        this.tileAnDoiAHiep1 = tileAnDoiAHiep1;
        this.tileAnDoiBHiep1 = tileAnDoiBHiep1;
        this.tileAnDoiAHiep2 = tileAnDoiAHiep2;
        this.tileAnDoiBHiep2 = tileAnDoiBHiep2;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        this.status = status;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDoiA() {
        return doiA;
    }

    public void setDoiA(String doiA) {
        this.doiA = doiA;
    }

    public String getDoiB() {
        return doiB;
    }

    public void setDoiB(String doiB) {
        this.doiB = doiB;
    }

    public int getBanThangDoiA() {
        return banThangDoiA;
    }

    public void setBanThangDoiA(int banThangDoiA) {
        this.banThangDoiA = banThangDoiA;
    }

    public int getBanThangDoiB() {
        return banThangDoiB;
    }

    public void setBanThangDoiB(int banThangDoiB) {
        this.banThangDoiB = banThangDoiB;
    }

    public String getThoiGianDa() {
        return thoiGianDa;
    }

    public void setThoiGianDa(String thoiGianDa) {
        this.thoiGianDa = thoiGianDa;
    }

    public String getTiLeDoiAChapCaTran() {
        return tiLeDoiAChapCaTran;
    }

    public void setTiLeDoiAChapCaTran(String tiLeDoiAChapCaTran) {
        this.tiLeDoiAChapCaTran = tiLeDoiAChapCaTran;
    }

    public String getTiLeDoiBChapCaTran() {
        return tiLeDoiBChapCaTran;
    }

    public void setTiLeDoiBChapCaTran(String tiLeDoiBChapCaTran) {
        this.tiLeDoiBChapCaTran = tiLeDoiBChapCaTran;
    }

    public String getTileDoiAChapTaiXiu() {
        return tileDoiAChapTaiXiu;
    }

    public void setTileDoiAChapTaiXiu(String tileDoiAChapTaiXiu) {
        this.tileDoiAChapTaiXiu = tileDoiAChapTaiXiu;
    }

    public String getTileDoiBChapTaiXiu() {
        return tileDoiBChapTaiXiu;
    }

    public void setTileDoiBChapTaiXiu(String tileDoiBChapTaiXiu) {
        this.tileDoiBChapTaiXiu = tileDoiBChapTaiXiu;
    }

    public String getTileDoiAChapHiep1() {
        return tileDoiAChapHiep1;
    }

    public void setTileDoiAChapHiep1(String tileDoiAChapHiep1) {
        this.tileDoiAChapHiep1 = tileDoiAChapHiep1;
    }

    public String getTileDoiBChapHiep2() {
        return tileDoiBChapHiep2;
    }

    public void setTileDoiBChapHiep2(String tileDoiBChapHiep2) {
        this.tileDoiBChapHiep2 = tileDoiBChapHiep2;
    }

    public double getTileAnDoiACaTran() {
        return tileAnDoiACaTran;
    }

    public void setTileAnDoiACaTran(double tileAnDoiACaTran) {
        this.tileAnDoiACaTran = tileAnDoiACaTran;
    }

    public double getTileAnDoiBCaTran() {
        return tileAnDoiBCaTran;
    }

    public void setTileAnDoiBCaTran(double tileAnDoiBCaTran) {
        this.tileAnDoiBCaTran = tileAnDoiBCaTran;
    }

    public double getTileAnDoiATaiXiu() {
        return tileAnDoiATaiXiu;
    }

    public void setTileAnDoiATaiXiu(double tileAnDoiATaiXiu) {
        this.tileAnDoiATaiXiu = tileAnDoiATaiXiu;
    }

    public double getTileAnDoiBTaiXiu() {
        return tileAnDoiBTaiXiu;
    }

    public void setTileAnDoiBTaiXiu(double tileAnDoiBTaiXiu) {
        this.tileAnDoiBTaiXiu = tileAnDoiBTaiXiu;
    }

    public double getTileAnDoiAHiep1() {
        return tileAnDoiAHiep1;
    }

    public void setTileAnDoiAHiep1(double tileAnDoiAHiep1) {
        this.tileAnDoiAHiep1 = tileAnDoiAHiep1;
    }

    public double getTileAnDoiBHiep1() {
        return tileAnDoiBHiep1;
    }

    public void setTileAnDoiBHiep1(double tileAnDoiBHiep1) {
        this.tileAnDoiBHiep1 = tileAnDoiBHiep1;
    }

    public double getTileAnDoiAHiep2() {
        return tileAnDoiAHiep2;
    }

    public void setTileAnDoiAHiep2(double tileAnDoiAHiep2) {
        this.tileAnDoiAHiep2 = tileAnDoiAHiep2;
    }

    public double getTileAnDoiBHiep2() {
        return tileAnDoiBHiep2;
    }

    public void setTileAnDoiBHiep2(double tileAnDoiBHiep2) {
        this.tileAnDoiBHiep2 = tileAnDoiBHiep2;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBanThangHiep1DoiA() {
        return banThangHiep1DoiA;
    }

    public void setBanThangHiep1DoiA(int banThangHiep1DoiA) {
        this.banThangHiep1DoiA = banThangHiep1DoiA;
    }

    public int getBanThangHiep2DoiA() {
        return banThangHiep2DoiA;
    }

    public void setBanThangHiep2DoiA(int banThangHiep2DoiA) {
        this.banThangHiep2DoiA = banThangHiep2DoiA;
    }

    public int getBanThangHiep1DoiB() {
        return banThangHiep1DoiB;
    }

    public void setBanThangHiep1DoiB(int banThangHiep1DoiB) {
        this.banThangHiep1DoiB = banThangHiep1DoiB;
    }

    public int getBanThangHiep2DoiB() {
        return banThangHiep2DoiB;
    }

    public void setBanThangHiep2DoiB(int banThangHiep2DoiB) {
        this.banThangHiep2DoiB = banThangHiep2DoiB;
    }

    int status;// 1: chua bat dau , 2 dang dien ra , 3 da da xong

}
