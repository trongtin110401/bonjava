package com.vinplay.api.processors.NapRutThe;

import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.payment.entities.UserWithDrawCard;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;

public class RutTheProcess implements BaseProcessor<HttpServletRequest, String> {
    private UserService userService = new UserServiceImpl();
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String amountx = request.getParameter("amount");
        String telcoId = request.getParameter("telcoid");
        String Quantityx = request.getParameter("quantity");
        String accessToken = request.getParameter("at");
        String nickname = this.getUserNameByAccessToken(accessToken);
        int amount = 0;
        if(amountx != null){
            amount = Integer.parseInt(amountx);
        }
        if(amount <= 0){
            amount = 0;
        }
        int quantity = Integer.parseInt(Quantityx);
        long tien = this.userService.getCurrentMoneyUserCache(nickname, "vin");
        if(tien < 200000 || amount < 200000) {
            return "{\"errorCode\": 1, \"msg\":\"So du khong du\"}";
        }
        long sodu = tien - amount;
        if(sodu < 50000){
            return "{\"errorCode\": 1, \"msg\":\"So du khong du\"}";
        }else{
            UserWithDrawCard withDrawCard = new UserWithDrawCard(nickname, telcoId, amount, quantity);
            BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawCard(withDrawCard);
            BroadCastUserMoney.pushBroadCast(nickname);
            return "{\"errorCode\": 0, \"msg\":\"Thanh Cong\"}";
        }
//        return "{\"errorCode\": 1, \"msg\":\"He thong bao tri\"}";
    }
    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

}
