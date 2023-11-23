package com.vinplay.api.processors.acemodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.api.entities.DepositSabaModel;
import com.vinplay.api.entities.SabaResponse;
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
import java.security.GeneralSecurityException;

public class PlaySabaProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");
    public synchronized String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false,"500");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
        String type = request.getParameter("t");
        String web = request.getParameter("m");
        //String money = request.getParameter("money");

        Long mMoney = 0l;
        synchronized (this){
            if(userService.checkAccesstoken(nickname,accessToken)){
                try {
                    UserModel userModel = userService.getUserByNickName(nickname);
                    DepositSabaModel depositSabaModel = new DepositSabaModel(DepositSabaModel.GAMENAME, (long) userModel.getId(), userModel.getUsername(), userModel.getNickname(), mMoney);
                    if(type.equals("p")) {

                        String response = this.getRequest(depositSabaModel,web);
                        SabaResponse res = new ObjectMapper().readValue(response, SabaResponse.class);
                        aceResponse.setSuccess(true);
                        aceResponse.setErrorCode(res.data);
                        return aceResponse.toJson();
                    }else if(type.equals("b")) {
                        String response = this.getBalance(depositSabaModel);
                        SabaResponse res = new ObjectMapper().readValue(response, SabaResponse.class);
                        aceResponse.setSuccess(true);
                        aceResponse.setErrorCode(res.currentBalanceGame+"");
                        return aceResponse.toJson();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    aceResponse.setErrorCode("chuyển tiền thất bại "+e.getMessage());
                }

            }
        }

        //userService.getU
        return aceResponse.toJson();

    }

    private String getRequest(DepositSabaModel depositRequest,String web) throws IOException, GeneralSecurityException {
        int isMobile = 2;
//        if(web == null || web.isEmpty()) {
//            isMobile =2;
//        }
//        return "{\"code\":0,\"message\":null,\"currentBalanceGame\":0.0,\"data\":\"\"}";
        if(depositRequest.userName.length() > 15) {
            depositRequest.userName = depositRequest.userName.substring(0,15);
        }
        String urlRequest = DepositSabaModel.DOMAIN+"/service/quickplay?gameName="+DepositSabaModel.GAMENAME+"&accountID=sunvin"+depositRequest.accountID+"&userName="+depositRequest.userName+"&nickName="+depositRequest.nickName+"&signature="+depositRequest.getMd5Deposit()+"&isMobile="+isMobile;
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(urlRequest)
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        if(body.contains("\"code\":0")) {
            return body;
        }else {
            throw new IOException("ERROR");
        }
    }

    private String getBalance(DepositSabaModel depositRequest) throws IOException, GeneralSecurityException {
        if(depositRequest.userName.length() > 15) {
            depositRequest.userName = depositRequest.userName.substring(0,15);
        }
        String urlRequest = DepositSabaModel.DOMAIN+"/service/balance?gameName="+DepositSabaModel.GAMENAME+"&accountID=sunvin"+depositRequest.accountID+"&userName="+depositRequest.userName+"&nickName="+depositRequest.nickName+"&signature="+depositRequest.getMd5Deposit();
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(urlRequest)
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        if(body.contains("\"code\":0")) {
            return body;
        }else {
            throw new IOException("ERROR");
        }
    }
}
