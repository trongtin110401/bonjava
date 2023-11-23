package com.vinplay.api.processors.sicbomodule;

import com.vinplay.common.HttpCommon;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import okhttp3.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class GetTokenSicboProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");
    public static final String brand = "b52";
    public static final String privatekey = "b52funvn";
    public static final String host = "https://isicbo.bandoluuniem.net";
    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");

        try {
            if (userService.checkAccesstoken(nickname, accessToken)) {
                return getRequest(nickname);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "";
    }
    private String getRequest(String username) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(host+"/getAccess?username="+username+"&brand="+brand+"&nickName="+username+"&privatekey="+privatekey)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        if(data.contains("{\"status\":0")) {
            return data;
        }
        return data;
    }
}
