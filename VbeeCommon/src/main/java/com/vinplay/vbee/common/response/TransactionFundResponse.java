
package com.vinplay.vbee.common.response;

import org.bson.Document;

import java.util.List;

public class TransactionFundResponse extends BaseResponseModel {

    private int total;
    private int pageIndex;

    private int pageSize;

    private List<Document> transactions;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

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

    public List<Document> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Document> transactions) {
        this.transactions = transactions;
    }

    public TransactionFundResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}

