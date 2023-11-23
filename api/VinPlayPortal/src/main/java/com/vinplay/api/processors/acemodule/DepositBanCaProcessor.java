package com.vinplay.api.processors.acemodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.ace.entity.DepositRequestEntity;
import com.vinplay.ace.utils.AceUtils;
import com.vinplay.api.entities.ACEResponse;
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
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DepositBanCaProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");

        // String money=request.getParameter("money");
        Long mMoney = 0l;
        logger.debug("4026" + " enroll on 4026");
        AceMoneyService aceMoneyService = new AceMoneyService();
        if(!aceMoneyService.getOpenAce()) {
        return "{\"code\":0,\"message\":null,\"currentBalanceGame\":0.0,\"data\":\"\"}";
        }
        synchronized (this) {
            if (userService.checkAccesstoken(nickname, accessToken)) {
                long currentMoney = userService.getCurrentMoneyUserCache(nickname, "vin");
                mMoney = currentMoney;
                if (nickname.isEmpty() || currentMoney <= 0) return aceResponse.toJson();
                if (!PortalUtils.allowDepositAce(nickname)){
                    aceResponse.setErrorCode("nạp rút qua nhanh");
                    return aceResponse.toJson();
                }
                logger.debug("4026" + " enroll on login " + mMoney + " " + nickname);
                try {
                    UserModel userModel = userService.getUserByNickName(nickname);
                    String ticket_id = String.valueOf(VinPlayUtils.generateTransId());
                    DepositRequestEntity depositRequest = new DepositRequestEntity(String.valueOf(userModel.getId()), userModel.getUsername()
                            , userModel.getNickname(), accessToken, 0, mMoney, AceUtils.generateKeySc(accessToken, ticket_id, mMoney));
                    depositRequest.setTicket_id(ticket_id);

//                    aceResponse = userService.updateMoneyFromAdmin(nickname, (long) -mMoney, "vin",
//                            "BanCA", "Chuyển tiền Bắn Cá",
//                            "Chuyển tiền Bắn cá", 0);

                 //   if (aceResponse.isSuccess()) { // thành công thì mới vào đây

                    aceResponse = userService.updateMoneyFromAdmin(nickname, (long)- mMoney, "vin",
                            "BanCA", "Chuyển tiền Bắn Cá",
                            "Chuyển tiền Bắn cá", 0);
                         if (aceResponse.isSuccess()) { // call sang ace , nếu thất bại thì cộng lại tiền
                            String myres = this.getRequest(depositRequest);
                            logger.debug("4026" + " call to 3rd " + myres + " " + nickname);
                            ACEResponse res = new ObjectMapper().readValue(myres, ACEResponse.class);
                            logger.debug("4026" + " call to 3rd " + res.isSuccess() + " " + nickname);

                            if(!res.isSuccess()){
                                res.setSuccess(false);
                                aceResponse.setErrorCode(" chuyển tiền sang bên thứ 3 thất bại lý do: " +res.getMsg());
                                // rollback money
//                                userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
//                                        "BanCA", "Chuyển tiền Bắn Cá",
//                                        "Chuyển tiền Bắn cá", 0);
                            }else {

                            }
                         }

                    BroadCastUserMoney.pushBroadCast(nickname);

                } catch (Exception e) {
                    e.printStackTrace();
                    if(e instanceof IOException){
//                        userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
//                                "BanCA", "Rollback tiền Bắn Cá",
//                                "Rollback tiền Bắn cá", 0);
                    }
                    aceResponse.setErrorCode("chuyển tiền thất bại "+e.getMessage());
                }

            }
        }

      //  userService.getU

         // khóa mõm giao dịch nạp rút trong vòng
        aceMoneyService.banDepositUser(nickname, 10000);
        return aceResponse.toJson();
    }

    private String getRequest(DepositRequestEntity depositRequest) throws IOException {
        OkHttpClient client = HttpCommon2.getInstance().getHttpClient().newBuilder().callTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, depositRequest.toJson());
        Request request = new Request.Builder()
                .url("https://bangchiu.bandoluuniem.net/banca/deposit?brand=sunvin")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
