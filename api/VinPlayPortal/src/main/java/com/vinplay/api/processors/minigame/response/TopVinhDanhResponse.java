package com.vinplay.api.processors.minigame.response;

import com.vinplay.vbee.common.models.minigame.TopWin;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.ArrayList;
import java.util.List;

public class TopVinhDanhResponse extends BaseResponseModel {
    public TopVinhDanhResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private List<TopWin> listVinhDanh = new ArrayList<>();

    public List<TopWin> getListVinhDanh() {
        return listVinhDanh;
    }

    public void setListVinhDanh(List<TopWin> listVinhDanh) {
        this.listVinhDanh = listVinhDanh;
    }
}
