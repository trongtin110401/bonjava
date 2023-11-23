package com.vinplay.bongda.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class UserRequestClientResponse extends BaseResponseModel {

    List<UserBetBongDa> listTrans;

    public List<UserBetBongDa> getListTrans() {
        return listTrans;
    }

    public void setListTrans(List<UserBetBongDa> listTrans) {
        this.listTrans = listTrans;
    }

    public UserRequestClientResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}
