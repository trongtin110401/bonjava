package com.vinplay.api.backend.processors.taiYou88;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class ListDoimainResponse extends BaseResponseModel {
    public List<DataDoimainResponse> ListDoimain;
    public ListDoimainResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}
