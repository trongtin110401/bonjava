/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.List;

public class ExpenseInfoResponse
        extends BaseResponseModel {
    public ExpenseInfoResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private List<ExpenseResponse> expenses;

    public List<ExpenseResponse> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseResponse> expenses) {
        this.expenses = expenses;
    }
}

