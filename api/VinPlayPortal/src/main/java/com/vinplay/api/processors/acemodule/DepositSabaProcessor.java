package com.vinplay.api.processors.acemodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.api.entities.DepositSabaModel;
import com.vinplay.api.entities.SabaResponse;
import com.vinplay.api.utils.PortalUtils;
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
import java.util.concurrent.TimeUnit;

public class DepositSabaProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
        // String money=request.getParameter("money");
        Long mMoney = 0l;
        //todo : bảo trì sicbo
//        logger.debug("4026" + " enroll on 4026");
        AceMoneyService aceMoneyService = new AceMoneyService();
        if(!aceMoneyService.getBongDa()) {
            return "{\"code\":0,\"message\":null,\"currentBalanceGame\":0.0,\"data\":\"\"}";
        }
        synchronized (this) {
            if (userService.checkAccesstoken(nickname, accessToken)) {
                long currentMoney = userService.getCurrentMoneyUserCache(nickname, "vin");
                mMoney = currentMoney;
                if (nickname.isEmpty() || currentMoney <= 0) return aceResponse.toJson();
                if(!PortalUtils.allowDepositAce(nickname)){
                    aceResponse.setErrorCode("nạp rút qua nhanh");

                    return  aceResponse.toJson();
                }
                logger.debug("4026" + " enroll on login " + mMoney + " " + nickname);
                try {
                    UserModel userModel = userService.getUserByNickName(nickname);
                    DepositSabaModel depositSabaModel = new DepositSabaModel(DepositSabaModel.GAMENAME, (long) userModel.getId(),userModel.getUsername(),userModel.getNickname(),mMoney);

                    aceResponse = userService.updateMoneyFromAdmin(nickname, (long) -mMoney, "vin",
                            "Saba", "Chuyển tiền Saba",
                            "Chuyển tiền Saba", 0);
                    BroadCastUserMoney.pushBroadCast(nickname);

                    if (aceResponse.isSuccess()) {
                        String myres = this.getRequest(depositSabaModel);
                        System.out.println(myres);
                        logger.debug("4026 " + DepositSabaProcessor.class+ " call to 3rd " + myres + " " + nickname);
                        SabaResponse res = new ObjectMapper().readValue(myres, SabaResponse.class);
                        logger.debug("4026" + DepositSabaProcessor.class+" call to 3rd " + res.code + " " + nickname);
                        if(res.code != 0){
                            aceResponse.setSuccess(false);
                            aceResponse.setErrorCode(" chuyển tiền sang bên thứ 3 thất bại lý do: " +res.message);
                            userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
                                    "Saba", "Chuyển tiền Saba thất bại",
                                    "Chuyển tiền Saba thất bại", 0);
                            BroadCastUserMoney.pushBroadCast(nickname);
                        }else {
                            aceResponse.setSuccess(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    if( e instanceof  IOException){
//                        userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
//                                "Saba", "Rollback tiền Saba",
//                                "Rollback tiền Saba", 0);
//                    }
                    aceResponse.setErrorCode("chuyển tiền thất bại "+e.getMessage());
                }

            }
        }

     //   userService.getU
//        AceMoneyService aceMoneyService = new AceMoneyService(); // khóa mõm giao dịch nạp rút trong vòng
        aceMoneyService.banDepositUser(nickname, 10000);
        return aceResponse.toJson();
    }

    private String getRequest(DepositSabaModel depositRequest) throws IOException {
        Long amountSabo = depositRequest.amount/1000l;
        if(depositRequest.userName.length() > 15) {
            depositRequest.userName = depositRequest.userName.substring(0,15);
        }
        //https://sblivenew.ragstoriches.one/service/balance?gameName=abc&accountID=1&userName=test123&nickName=abcded&amount=100&signature=a3e30c2e2171a97545f3962ba0af022b
        String urlRequest = DepositSabaModel.DOMAIN+"/service/deposit?gameName="+DepositSabaModel.GAMENAME+"&accountID=sunvin"+depositRequest.accountID+"&userName="+depositRequest.userName+"&nickName="+depositRequest.nickName+"&amount="+amountSabo+"&signature="+depositRequest.getMd5Deposit();
        OkHttpClient client = HttpCommon2.getInstance().getHttpClient().newBuilder().callTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(urlRequest)
                .build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        if(data.contains("\"code\":0")) {
            return data;
        }else {
            throw new IOException("ERROR");
        }
    }

}
