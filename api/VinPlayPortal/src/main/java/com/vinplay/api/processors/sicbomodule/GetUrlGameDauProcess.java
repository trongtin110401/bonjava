package com.vinplay.api.processors.sicbomodule;

import com.vinplay.api.processors.hub777.AESEncryption;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;

public class GetUrlGameDauProcess implements BaseProcessor<HttpServletRequest, String> {

    public  String execute(Param<HttpServletRequest> param) {
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
        String gameId = request.getParameter("gameId");
        UserService userService = new UserServiceImpl();
        if (userService.checkAccesstoken(nickname, accessToken)) {
            aceResponse.setSuccess(true);
            aceResponse.setErrorCode(AESEncryption.getLinkUrl(gameId,accessToken));
            return aceResponse.toJson();
        }
        return "";
    }

}