/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.entities.TransferMoneyResponse
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.models.UserModel
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.money;

import com.vinplay.api.backend.response.TransferMoneyDaiLyResponse;
import com.vinplay.usercore.entities.TransferMoneyResponse;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class ChuyenTienDaiLyUnOtpProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        int code = 1;
        TransferMoneyDaiLyResponse res = new TransferMoneyDaiLyResponse(false, String.valueOf(code));
        try {
            UserServiceImpl service;
            UserModel user;
            String nicknameSend = request.getParameter("nns");
            Long money = Long.parseLong(request.getParameter("mn"));
            String nicknameReceive = request.getParameter("nnr");
            String reason = request.getParameter("rs");
            String otp = request.getParameter("otp");
            String type = request.getParameter("type");
            reason = reason.replaceAll("\\<.*?\\>", "");
            user = (service = new UserServiceImpl()).getUserByNickName(nicknameSend);
            if (!(nicknameSend == null || nicknameReceive == null
                    || nicknameSend.isEmpty() || nicknameReceive.isEmpty()
                    || money <= 0L || reason == null || reason.isEmpty() ||
                    user == null || user.getDaily() < 1)) {
                nicknameSend = user.getNickname();
                if (otp == null || otp.isEmpty()) {
                    TransferMoneyResponse moneyres = service.transferMoneyDaiLyUnOtp(nicknameSend, nicknameReceive, money.longValue(), reason, false);
                    code = moneyres.getCode();
                }
            }
            res.setNicknameSend(nicknameSend);
            res.setNicknameReceive(nicknameReceive);
            res.setMoneySend(money);
            res.setErrorCode(String.valueOf(code));
            logger.debug(res.toJson());
            return res.toJson();
        }
        catch (Exception e) {
            logger.debug((Object)e);
        }
        return res.toJson();
    }
}

