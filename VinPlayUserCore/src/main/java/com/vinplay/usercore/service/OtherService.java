/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.usercore.service;

import com.vinplay.vbee.common.response.ExpenseInfoResponse;
import com.vinplay.vbee.common.response.LinkSocialResponse;
import com.vinplay.vbee.common.response.TransactionExpenseResponse;
import com.vinplay.vbee.common.response.TransactionFundResponse;
import org.bson.Document;

public interface OtherService {

    LinkSocialResponse getLinkSocial();

    void updateLinkSocial(LinkSocialResponse response);

    void saveTransactionUpdateFund(Document document);

    void saveExpenseTransaction(Document document);

    TransactionFundResponse getTransactionFund(int pageIndex, int pageSize, String type, String startTime, String endTime, String fundName);

    TransactionExpenseResponse getTransactionExpense(int pageIndex, int pageSize, String type, String startTime, String endTime, String expense);
}

