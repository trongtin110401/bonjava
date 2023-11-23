package com.vinplay.api.processors.acemodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.ace.entity.WithDrawRequestEntity;
import com.vinplay.ace.utils.AceUtils;
import com.vinplay.api.entities.UserInfoAce;
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
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WithDrawLodeAceProcessor  implements BaseProcessor<HttpServletRequest, String> {
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


                if(mMoney<0 || nickname.isEmpty()) return aceResponse.toJson();
                if(!PortalUtils.allowWithDrawAce(nickname)){
                    aceResponse.setErrorCode("nạp rút qua nhanh");

                    return  aceResponse.toJson();
                }
                try {
                    UserInfoAce userInfoAce = this.getUserInfor(accessToken);
                    double realMoney =(userInfoAce.getBalance()*1000.0);
                    mMoney= (long) realMoney;

                    if((long)(mMoney) <=0) return aceResponse.toJson();
                    UserModel userModel = userService.getUserByNickName(nickname);
                    String ticket_id = String.valueOf(VinPlayUtils.generateTransId());
                    WithDrawRequestEntity depositRequest = new WithDrawRequestEntity(String.valueOf(userModel.getId()),accessToken,mMoney, AceUtils.generateKeySc(accessToken,ticket_id,mMoney));
                    depositRequest.setTicket_id(ticket_id);
                    String response = this.getRequest(depositRequest);


                    HashMap<String,Object> res = (HashMap<String, Object>) new ObjectMapper().readValue(response,Object.class);
                    boolean success = (boolean) res.get("success");
                    if(success){
                        aceResponse = userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
                                "Lode", "Nhận tiền từ Lô đề",
                                "Nhận tiền Lô đề", 0);
                        BroadCastUserMoney.pushBroadCast(nickname);

                    } else {
                        aceResponse.setErrorCode(" chuyển tiền sang game thất bại lý do: " +res.get("msg"));

                    }



                } catch (Exception e) {
                    e.printStackTrace();
                    aceResponse.setErrorCode("chuyển tiền thất bại " + e.getMessage());
                }

            }
        }

        //userService.getU
        AceMoneyService aceMoneyService = new AceMoneyService();
        aceMoneyService.banWidrawUser(nickname, 5000);
        return aceResponse.toJson();
    }

    private UserInfoAce getUserInfor(String token) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://bandoluuniem.net/sport/user/getUserByToken?token="+token+"&brand=sunvin")
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        UserInfoAce res = new ObjectMapper().readValue(body, UserInfoAce.class);
        return res;
    }
    private String getRequest(WithDrawRequestEntity depositRequest) throws IOException {
        OkHttpClient client = HttpCommon2.getInstance().getHttpClient().newBuilder().callTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, depositRequest.toJson());
        Request request = new Request.Builder()
                .url("https://lode.bandoluuniem.net/lode/withdraw?brand=sunvin")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
