package com.vinplay.api.processors.acemodule;

import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.response.aceModule.AuthenACEResponse;
import com.vinplay.vbee.common.statics.Consts;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class ACEBongDaMoneyProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");
    private static final String PRIVATE_KEY="thisHashMIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAqTCJg5tYVTqGkaTZ";
    @Override
    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false,"500");
        HttpServletRequest request = (HttpServletRequest)param.get();

        String accessToken = request.getParameter("token");

        String money = request.getParameter("money");
        Long mMoney = Long.parseLong(money);
        String privateKey= request.getParameter("privateKey");
        if(PRIVATE_KEY.equals(privateKey)){
            String nickName = getUserNameByAccessToken(accessToken);
            if(nickName.isEmpty()) return aceResponse.toJson();
//            aceResponse =userService.updateMoneyFromAdmin(nickName, (long) mMoney, "vin",
//                    "BongDA", "Chuyển tiền bóng đá",
//                    "Chuyển tiền bóng đá", 0);
        }
        //userService.getU

        return aceResponse.toJson();
    }
    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
