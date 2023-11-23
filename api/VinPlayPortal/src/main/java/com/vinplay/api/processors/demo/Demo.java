package com.vinplay.api.processors.demo;

import com.google.gson.Gson;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;

import javax.servlet.http.HttpServletRequest;

public class Demo implements BaseProcessor<HttpServletRequest, String> {

    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String accessToken = request.getParameter("at");
        Gson gson = new Gson();
        UserService userService = new UserServiceImpl();
        try {
            UserModel userModel = userService.getUserByNickName(getUserNameByAccessToken(accessToken));
            return gson.toJson(userModel);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "123";

    }

    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
