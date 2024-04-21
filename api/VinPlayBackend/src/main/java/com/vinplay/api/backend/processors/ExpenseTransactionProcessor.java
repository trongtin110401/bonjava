/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.GiftCodeServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.GiftCodeSearchResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.ExpenseInfoResponse;
import com.vinplay.vbee.common.response.ExpenseResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpenseTransactionProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        ExpenseInfoResponse response = new ExpenseInfoResponse(true, "200");
        String type = request.getParameter("type");
        String expense = request.getParameter("expense");
        String amount = request.getParameter("amount");
        OtherService otherService = new OtherServiceImpl();
        Document document = new Document();
        document.put("time_log", VinPlayUtils.getCurrentDateTime());
        document.put("type", type);
        document.put("expense", expense);
        document.put("amount", amount);
        otherService.saveExpenseTransaction(document);
        List<ExpenseResponse> expenses = new ArrayList<>();
        ExpenseResponse expenseResponse = new ExpenseResponse();
        expenseResponse.type = type;
        expenseResponse.expense = expense;
        expenseResponse.amount = Long.parseLong(amount);
        expenses.add(expenseResponse);
        response.setExpenses(expenses);
        return response.toJson();


    }
}

