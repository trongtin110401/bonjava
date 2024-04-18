package com.vinplay.api.processors.rutbankapi;

import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;

public class RutBankAPIProcess implements BaseProcessor<HttpServletRequest, String> {
    private UserService userService = new UserServiceImpl();

    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            BaseResponseModel baseResponseModel = new BaseResponseModel(false, "1001");
            HttpServletRequest request = param.get();
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            String amount = request.getParameter("amount");
            String bankname = request.getParameter("bankname");
            String banknum = request.getParameter("banknum");
            String bankacc = request.getParameter("bankacc");
            String otp = request.getParameter("otp");
            OtpServiceImpl service = new OtpServiceImpl();
            baseResponseModel = service.checkOTP(nickname,otp);
            if (!baseResponseModel.isSuccess()) {
                return baseResponseModel.toJson();
            }
            String type = request.getParameter("type");
            bankacc = bankacc.replaceAll("_", " ");
            CheckNap checknap = new CheckNap();
            naptmp ntmp = checknap.tongnapThe(nickname);
            long tiennap = ntmp.getTongnap();
            int yeu_cau_rut_1 = Integer.parseInt(amount);
            if (tiennap >= 0) {
                if ("momo".equalsIgnoreCase(type)) {
                    UserWithdrawMomo userWithdrawMomo = new UserWithdrawMomo(nickname, yeu_cau_rut_1, banknum);
                    baseResponseModel = this.userService.UpdateMoneyWhenWithdrawMomo(userWithdrawMomo);
                } else if ("bank".equalsIgnoreCase(type)) {
                    UserWithdraw userWithdraw = new UserWithdraw(nickname, yeu_cau_rut_1, banknum, bankacc, bankname);
                    baseResponseModel = this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                }
                BroadCastUserMoney.pushBroadCast(nickname);
                return baseResponseModel.toJson();
            }
            return baseResponseModel.toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

}
