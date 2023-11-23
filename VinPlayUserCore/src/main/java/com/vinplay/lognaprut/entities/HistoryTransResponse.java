package com.vinplay.lognaprut.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.response.LogAgentTranferMoneyResponse;

import java.util.ArrayList;
import java.util.List;

public class HistoryTransResponse extends BaseResponseModel {
    List<HistoryTransModel> listTrans;
    List<LogAgentTranferMoneyResponse> listTransSend;
    List<LogAgentTranferMoneyResponse> listTransReceive;

    ArrayList<UserVinBongDaModel> listChuyenTienBongDa;
    ArrayList<UserVinBongDaModel> listNhanTienBongDa;

    public int totalpage;
    public long currentMoney;
    public String nickName;

    public HistoryTransResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<HistoryTransModel> getListTrans() {
        return listTrans;
    }

    public void setListTrans(List<HistoryTransModel> listTrans) {
        this.listTrans = listTrans;
    }

    public List<LogAgentTranferMoneyResponse> getListTransSend() {
        return listTransSend;
    }

    public void setListTransSend(List<LogAgentTranferMoneyResponse> listTransSend) {
        this.listTransSend = listTransSend;
    }

    public List<LogAgentTranferMoneyResponse> getListTransReceive() {
        return listTransReceive;
    }

    public void setListTransReceive(List<LogAgentTranferMoneyResponse> listTransReceive) {
        this.listTransReceive = listTransReceive;
    }

    public ArrayList<UserVinBongDaModel> getListChuyenTienBongDa() {
        return listChuyenTienBongDa;
    }

    public void setListChuyenTienBongDa(ArrayList<UserVinBongDaModel> listChuyenTienBongDa) {
        this.listChuyenTienBongDa = listChuyenTienBongDa;
    }

    public ArrayList<UserVinBongDaModel> getListNhanTienBongDa() {
        return listNhanTienBongDa;
    }

    public void setListNhanTienBongDa(ArrayList<UserVinBongDaModel> listNhanTienBongDa) {
        this.listNhanTienBongDa = listNhanTienBongDa;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public long getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(long currentMoney) {
        this.currentMoney = currentMoney;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
