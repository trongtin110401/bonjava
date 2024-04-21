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

import javax.servlet.http.HttpServletRequest;

public class DeleteExpenseTransactionProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        ExpenseInfoResponse response = new ExpenseInfoResponse(true, "200");
        String id = request.getParameter("id");
        OtherService otherService = new OtherServiceImpl();
        otherService.deleteExpenseById(id);
        response.setExpenses(null);
        return response.toJson();

    }
}

