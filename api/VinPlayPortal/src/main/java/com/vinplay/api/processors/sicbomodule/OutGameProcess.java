package com.vinplay.api.processors.sicbomodule;

import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;

public class OutGameProcess implements BaseProcessor<HttpServletRequest, String> {

    public synchronized String execute(Param<HttpServletRequest> param) {
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String idGame = request.getParameter("id");
        String cmm = request.getParameter("cmm");
        if(cmm.equals("1vsdfdfsdfs")) {
            BroadCastUserMoney.pushBroadOutGame(nickname,idGame);
        }
        return "OK";
    }
}