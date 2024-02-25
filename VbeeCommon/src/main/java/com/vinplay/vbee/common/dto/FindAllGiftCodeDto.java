package com.vinplay.vbee.common.dto;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class FindAllGiftCodeDto extends BaseResponseModel {

    public FindAllGiftCodeDto(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private int pageIndex;

    private int pageSize;

    private long total;

    private List<GiftCodeDto> transactions;

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

    public List<GiftCodeDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<GiftCodeDto> transactions) {
        this.transactions = transactions;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
