package com.vinplay.bongda.entities;

import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class KeoBongDaResponse extends BaseResponseModel {
    public List<KeoBongDa> listTrans;

    public List<KeoBongDa> getListTrans() {
        return listTrans;
    }

    public void setListTrans(List<KeoBongDa> listTrans) {
        this.listTrans = listTrans;
    }

    public KeoBongDaResponse(boolean success, String errorCode) {


        super(success, errorCode);

    }

}
