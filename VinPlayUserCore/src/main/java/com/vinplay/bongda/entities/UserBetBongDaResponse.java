package com.vinplay.bongda.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class UserBetBongDaResponse extends BaseResponseModel {
    public List<UserBetBongDaModel> listTrans;

    public List<UserBetBongDaModel> getListTrans() {
        return listTrans;
    }

    public void setListTrans(List<UserBetBongDaModel> listTrans) {
        this.listTrans = listTrans;
    }

    public UserBetBongDaResponse(boolean success, String errorCode, List<UserBetBongDaModel> list) {
        super(success, errorCode);
        this.listTrans = list;
    }
}
