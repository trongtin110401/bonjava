package com.vinplay.api.processors.acemodule;

import bitzero.util.common.business.Debug;
import com.vinplay.common.HttpCommon;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.aceModule.AuthenACEResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

public class AceAuthProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    @Override
    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        AuthenACEResponse aceResponse = new AuthenACEResponse(false,"500");
        HttpServletRequest request = (HttpServletRequest)param.get();

        String accessToken = request.getParameter("token");
        //userService.getU
        String nickname = "";



        try {
              nickname = this.getUserNameByAccessToken(accessToken);

        } catch (Exception e) {
            if(accessToken.substring(0,3).equals("net")){
                // call to net
                try {
                    Debug.info("call to sunet "+ accessToken);
                    String res = this.callTo3Party(accessToken);
                    Debug.info("call to sunet "+ res);
                    return res;
                } catch (Exception ex) {
                    //    return  aceResponse.toJson();
                }
            }

            // không có thì call sang bên go88
            return aceResponse.toJson();
        }

        if(userService.checkAccesstoken(nickname,accessToken)){
            long currentMoney = userService.getCurrentMoneyUserCache(nickname,"vin");
            try {
                UserModel userModel = userService.getUserByNickName(nickname);
                aceResponse.setAvatar(userModel.getAvatar());
                aceResponse.setDisplay_name(nickname);
                aceResponse.setToken(accessToken);
                aceResponse.setUser_id(userModel.getId());
                aceResponse.setUser_name(userModel.getUsername());
                aceResponse.setMoney(currentMoney);
                aceResponse.setSuccess(true);
                aceResponse.setErrorCode("200");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return aceResponse.toJson();
    }
    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

    private String callTo3Party(String token) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://sunnet.vin/api?c=4016&token=" + token)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
