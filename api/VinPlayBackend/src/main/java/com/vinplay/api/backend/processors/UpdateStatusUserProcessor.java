/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.SecurityServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.SecurityServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import javax.servlet.http.HttpServletRequest;

public class UpdateStatusUserProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        BaseResponseModel response = new BaseResponseModel(false, "10001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickName = request.getParameter("nn");
        String action = request.getParameter("ac");
        String type = request.getParameter("type");
        SecurityServiceImpl service = new SecurityServiceImpl();
        String[] item_action = action.split(",");
        boolean result = false;
        for (String item : item_action) {
            result = service.updateStatusUser(nickName, Integer.parseInt(item), type);
            if (result) {
                response.setSuccess(true);
                response.setErrorCode("0");
                continue;
            }
            response.setErrorCode("10001");
        }
        return response.toJson();
    }
}

