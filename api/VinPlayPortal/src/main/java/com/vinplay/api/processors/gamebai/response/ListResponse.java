package com.vinplay.api.processors.gamebai.response;

import com.vinplay.vbee.common.response.BaseResponseModel;
import org.json.JSONObject;

import java.util.List;

public class ListResponse extends BaseResponseModel {

    List<Integer> listTable;

    public List<Integer> getListTable() {
        return listTable;
    }

    public void setListTable(List<Integer> listTable) {
        this.listTable = listTable;
    }

    public ListResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}
