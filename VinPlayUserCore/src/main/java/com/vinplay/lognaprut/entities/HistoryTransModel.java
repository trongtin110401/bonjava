package com.vinplay.lognaprut.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HistoryTransModel {
    public String giaodich;
    public String congGiaoDich;
    public String hinhthuc;
    public String sotien;
    public String trangthai;
    public String ghiChu;
    public String nickName;
    public String hinhthucTrans;
    public String transId;
    public String id;
    public String createAt;

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGiaodich() {
        return giaodich;
    }

    public void setGiaodich(String giaodich) {
        this.giaodich = giaodich;
    }

    public String getCongGiaoDich() {
        return congGiaoDich;
    }

    public void setCongGiaoDich(String congGiaoDich) {
        this.congGiaoDich = congGiaoDich;
    }

    public String getHinhthuc() {
        return hinhthuc;
    }

    public void setHinhthuc(String hinhthuc) {
        this.hinhthuc = hinhthuc;
    }

    public String getSotien() {
        return sotien;
    }

    public void setSotien(String sotien) {
        this.sotien = sotien;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public HistoryTransModel(String giaodich, String congGiaoDich, String hinhthuc, String sotien, String trangthai, String ghiChu, String nickName, String hinhthucTrans, String transId) {
        this.giaodich = giaodich;
        this.congGiaoDich = congGiaoDich;
        this.hinhthuc = hinhthuc;
        this.sotien = sotien;
        this.trangthai = trangthai;
        this.ghiChu = ghiChu;
        this.nickName = nickName;
        this.hinhthucTrans = hinhthucTrans;
        this.transId = transId;
    }

    public HistoryTransModel(String giaodich, String congGiaoDich, String hinhthuc, String sotien, String trangthai, String ghiChu, String nickName, String hinhthucTrans, String transId, String id, String createAt) {
        this.giaodich = giaodich;
        this.congGiaoDich = congGiaoDich;
        this.hinhthuc = hinhthuc;
        this.sotien = sotien;
        this.trangthai = trangthai;
        this.ghiChu = ghiChu;
        this.nickName = nickName;
        this.hinhthucTrans = hinhthucTrans;
        this.transId = transId;
        this.id = id;
        this.createAt = createAt;
    }

    public String getHinhthucTrans() {
        return hinhthucTrans;
    }

    public void setHinhthucTrans(String hinhthucTrans) {
        this.hinhthucTrans = hinhthucTrans;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }
}
