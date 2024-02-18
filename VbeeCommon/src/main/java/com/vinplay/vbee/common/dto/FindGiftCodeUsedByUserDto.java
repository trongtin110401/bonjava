package com.vinplay.vbee.common.dto;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class FindGiftCodeUsedByUserDto extends BaseResponseModel {

    public FindGiftCodeUsedByUserDto(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private int pageIndex;

    private int pageSize;

    private List<UseGiftCodeDto> transactions;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<UseGiftCodeDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<UseGiftCodeDto> transactions) {
        this.transactions = transactions;
    }
}
