/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dichvuthe.service.impl.RechargeServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage
 *  com.vinplay.vbee.common.response.UpdatePendingCardResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors;

import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage;
import com.vinplay.vbee.common.response.UpdatePendingCardResponse;
import javax.servlet.http.HttpServletRequest;

public class UpdatePendingCardProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        UpdatePendingCardResponse response = new UpdatePendingCardResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String referenceId = request.getParameter("rid");
        String actor = request.getParameter("act");
        if (referenceId != null && !referenceId.equals("") && actor != null && !actor.equals("")) {
            RechargeServiceImpl service = new RechargeServiceImpl();
            RechargeByCardMessage rechargeByCardMessage = new RechargeByCardMessage();
            try {
                rechargeByCardMessage = service.updatePendingCardStatus(referenceId, actor);
            }
            catch (Exception e) {
                e.printStackTrace();
                return response.toJson();
            }
            if (rechargeByCardMessage == null) {
                response.setRechargeByCardMessage(rechargeByCardMessage);
                response.setSuccess(false);
                response.setErrorCode("1036");
            } else if (rechargeByCardMessage.getError() != null) {
                response.setRechargeByCardMessage(rechargeByCardMessage);
                response.setSuccess(false);
                response.setErrorCode(rechargeByCardMessage.getError());
            } else {
                response.setRechargeByCardMessage(rechargeByCardMessage);
                response.setSuccess(true);
                response.setErrorCode("0");
            }
            return response.toJson();
        }
        return "MISSING PARAMETTER";
    }
}

