package com.vinplay.api.processors.acemodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.api.entities.DepositLodeModel;
import com.vinplay.api.entities.LodeResponse;
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

public class DepositLodeMoiProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
        Long mMoney = 0l;
        //todo : bảo trì sicbo
//        return "{\"code\":0,\"message\":null,\"currentBalanceGame\":0.0,\"data\":\"\"}";
//        logger.debug("4026" + " enroll on 4026");
        AceMoneyService aceMoneyService = new AceMoneyService();
        if (!aceMoneyService.getLodeNew()) {
            return "{\"code\":0,\"message\":null,\"currentBalanceGame\":0.0,\"data\":\"\"}";
        }
        if (userService.checkAccesstoken(nickname, accessToken)) {
            long currentMoney = userService.getCurrentMoneyUserCache(nickname, "vin");
            mMoney = currentMoney;
            if (nickname.isEmpty() || currentMoney <= 0) return aceResponse.toJson();
            if (!PortalUtils.allowDepositAce(nickname)) {
                aceResponse.setErrorCode("nạp rút qua nhanh");

                return aceResponse.toJson();
            }
            //ban truoc 1s
            aceMoneyService.banDepositUser(nickname, 1000);
            logger.debug("4026" + " enroll on login " + mMoney + " " + nickname);
            try {
                UserModel userModel = userService.getUserByNickName(nickname);
                DepositLodeModel depositLodeModel = new DepositLodeModel(userModel.getNickname(), mMoney);
                aceResponse = userService.updateMoneyFromAdmin(nickname, (long) -mMoney, "vin",
                        "Lode", "Chuyển tiền Lode NEW",
                        "Chuyển tiền Lode NEW", 0);
                BroadCastUserMoney.pushBroadCast(nickname);

                if (aceResponse.isSuccess()) {
                    String myres = this.getRequest(depositLodeModel);
                    System.out.println(myres);
                    logger.debug("4026 " + DepositLodeMoiProcessor.class + " call to 3rd " + myres + " " + nickname);
                    LodeResponse res = new ObjectMapper().readValue(myres, LodeResponse.class);
                    logger.debug("4026" + DepositLodeMoiProcessor.class + " call to 3rd " + res.errorCode + " " + nickname);
                    if (res.errorCode != 0) {
                        aceResponse.setSuccess(false);
                        aceResponse.setErrorCode(" chuyển tiền sang bên thứ 3 thất bại lý do: " + res.msg);
                        userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
                                "Lode", "Chuyển tiền Lode NEW thất bại",
                                "Chuyển tiền Lode NEW thất bại", 0);
                        BroadCastUserMoney.pushBroadCast(nickname);
                    } else {
                        aceResponse.setSuccess(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//                    if( e instanceof  IOException){
//                        userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
//                                "Lode", "Rollback tiền Lode",
//                                "Rollback tiền Lode", 0);
//                    }
                aceResponse.setErrorCode("chuyển tiền thất bại " + e.getMessage());
            }

        }
        //   userService.getU
        aceMoneyService.banDepositUser(nickname, 10000);
        return aceResponse.toJson();
    }

    private String getRequest(DepositLodeModel depositRequest) throws IOException {

        String urlRequest = DepositLodeModel.DOMAIN + "deposit?brand=" + DepositLodeModel.GAMENAME + "&hash=" + depositRequest.getMd5Deposit() + "&username=" + depositRequest.userName + "&amount=" + depositRequest.amount;
        OkHttpClient client = HttpCommon2.getInstance().getHttpClient().newBuilder().callTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(urlRequest)
                .build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        if (data.contains("\"errorCode\":0")) {
            return data;
        } else {
            throw new IOException("ERROR");
        }
    }

}
