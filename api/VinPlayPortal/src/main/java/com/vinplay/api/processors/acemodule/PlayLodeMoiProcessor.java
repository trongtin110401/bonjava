package com.vinplay.api.processors.acemodule;

import com.google.gson.Gson;
import com.vinplay.api.entities.DepositLodeModel;
import com.vinplay.common.HttpCommon;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.BaseResponseModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class PlayLodeMoiProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
        String type = request.getParameter("t");
        Gson gson = new Gson();
        Long mMoney = 0l;
        synchronized (this) {
            if (userService.checkAccesstoken(nickname, accessToken)) {
                try {
                    UserModel userModel = userService.getUserByNickName(nickname);
                    DepositLodeModel lepositLodeModel = new DepositLodeModel(userModel.getNickname(), mMoney);
                    String response = this.getRequest(lepositLodeModel);
                    aceResponse.setSuccess(true);
                    aceResponse.setErrorCode(response);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    aceResponse.setErrorCode("chuyển tiền thất bại " + e.getMessage());
                }

            }
        }
        return aceResponse.toJson();

    }

    private String getRequest(DepositLodeModel depositRequest) throws IOException {
        String urlRequest = DepositLodeModel.DOMAIN + "quickplay?brand=" + DepositLodeModel.GAMENAME + "&hash=" + depositRequest.getMd5Deposit() + "&username=" + depositRequest.userName + "&amount=" + depositRequest.amount;
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(urlRequest)
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        if (body.contains("\"errorCode\":0")) {
//            body = body.replace("bandoluuniem.net", "chuchimto.site");
            return body;
        } else {

            throw new IOException("ERROR");
        }
    }
}
