/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.MailBoxServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.processors;

import com.vinplay.usercore.dao.impl.MailBoxDaoImpl;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;

public class UpdateStatusMailProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        String mailId = request.getParameter("mid");
        String accessToken = request.getParameter("at");
        String nickname = this.getUserNameByAccessToken(accessToken);
        if (!mailId.isEmpty()) {
            MailBoxDaoImpl dao = new MailBoxDaoImpl();
            int result = dao.updateStatusMailBox(mailId, nickname);
            if (result == 0) {
                response.setErrorCode("0");
                response.setSuccess(true);
            } else {
                response.setErrorCode("10001");
            }
            return response.toJson();
        }
        return "MISSING PARAMETTER";
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}

