package com.vinplay.api.processors.acemodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.api.entities.DepositSabaModel;
import com.vinplay.api.entities.SabaResponse;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.common.HttpCommon;
import com.vinplay.common.HttpCommon2;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dal.service.impl.AceMoneyService;
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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WithdrawSabaProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");
    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false,"500");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
        //String money = request.getParameter("money");

        Long mMoney = 0l;
        synchronized (this){
            if(userService.checkAccesstoken(nickname,accessToken)){


                if(mMoney< 0 || nickname.isEmpty()) return aceResponse.toJson();
                if(!PortalUtils.allowWithDrawAce(nickname)){
                    aceResponse.setErrorCode("nạp rút qua nhanh");
                    return  aceResponse.toJson();
                }
                try {
                    UserModel userModel = userService.getUserByNickName(nickname);
                    DepositSabaModel depositSabaModel = new DepositSabaModel(DepositSabaModel.GAMENAME, (long) userModel.getId(),userModel.getUsername(),userModel.getNickname(),mMoney);
                    SabaResponse userInfoAce = this.getUserInfor(depositSabaModel);
                    double realMoney = userInfoAce.currentBalanceGame;
                    mMoney= (long) realMoney;

                    if((long)mMoney <=0) return aceResponse.toJson();

                    depositSabaModel.amount = mMoney;

                    String response = this.getRequest(depositSabaModel);

                    HashMap<String,Object> res = (HashMap<String, Object>) new ObjectMapper().readValue(response,Object.class);
                    int code = (int) res.get("code");
                    if(code== 0){
                        aceResponse = userService.updateMoneyFromAdmin(nickname, (long) mMoney*1000l, "vin",
                                "Saba", "Nhận tiền từ Saba",
                                "Nhận tiền Saba", 0);
                        BroadCastUserMoney.pushBroadCast(nickname);

                    } else {
                        aceResponse.setErrorCode(" chuyển tiền sang game thất bại lý do: " +res.get("msg"));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    aceResponse.setErrorCode("rút tiền thất bại "+e.getMessage());
                }

            }
        }

        //userService.getU
        AceMoneyService aceMoneyService = new AceMoneyService();
        aceMoneyService.banWidrawUser(nickname, 5000);
        return aceResponse.toJson();
    }

    private SabaResponse getUserInfor(DepositSabaModel depositRequest) throws IOException {
        if(depositRequest.userName.length() > 15) {
            depositRequest.userName = depositRequest.userName.substring(0,15);
        }
        String urlRequest = DepositSabaModel.DOMAIN+"/service/balance?gameName="+DepositSabaModel.GAMENAME+"&accountID=sunvin"+depositRequest.accountID+"&userName="+depositRequest.userName+"&nickName="+depositRequest.nickName+"&signature="+depositRequest.getMd5Deposit();
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(urlRequest)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        if(body.contains("\"code\":0")) {
            SabaResponse res = new ObjectMapper().readValue(body, SabaResponse.class);
            return res;
        }else {
            throw new IOException("ERROR");
        }
    }

    private String getRequest(DepositSabaModel depositRequest) throws IOException {
        if(depositRequest.userName.length() > 15) {
            depositRequest.userName = depositRequest.userName.substring(0,15);
        }
        String urlRequest = DepositSabaModel.DOMAIN+"/service/withdraw?gameName="+DepositSabaModel.GAMENAME+"&accountID=sunvin"+depositRequest.accountID+"&userName="+depositRequest.userName+"&nickName="+depositRequest.nickName+"&amount="+depositRequest.amount+"&signature="+depositRequest.getMd5Deposit();
        OkHttpClient client = HttpCommon2.getInstance().getHttpClient().newBuilder().callTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(urlRequest) //https://xingxing.bandoluuniem.net/sicbo/withdraw
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
