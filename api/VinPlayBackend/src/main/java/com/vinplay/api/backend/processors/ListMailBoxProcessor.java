/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.MailBoxServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.ListMailBoxResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.ListMailBoxResponse;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class ListMailBoxProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        ListMailBoxResponse response = new ListMailBoxResponse(false, "1001");
        HttpServletRequest request = param.get();
        String nickName = request.getParameter("nn");
        int pageIndex = Integer.parseInt(request.getParameter("p"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        if (pageIndex < 0) {
            return response.toJson();
        }
        MailBoxServiceImpl service = new MailBoxServiceImpl();
        try {
            response = service.getAllMail(nickName, pageIndex, pageSize);
            response.setSuccess(true);
            response.setErrorCode("0");
        } catch (Exception e) {
            logger.debug(e);
        }
        return response.toJson();
    }
}

