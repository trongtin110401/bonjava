package com.vinplay.vbee.common.response.BauCuaTo2;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class BauCuaListUserResponse extends BaseResponseModel {
    private List< BauCuaUserInfomation> listBauCuaInformation;

    public BauCuaListUserResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<BauCuaUserInfomation> getListBauCuaInformation() {
        return listBauCuaInformation;
    }

    public void setListBauCuaInformation(List<BauCuaUserInfomation> listBauCuaInformation) {
        this.listBauCuaInformation = listBauCuaInformation;
    }

    @Override
    public String toString() {
        return "BauCuaListUserResponse{" +
                "listBauCuaInformation=" + listBauCuaInformation +
                '}';
    }
}
