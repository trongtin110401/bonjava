package com.vinplay.api.processors.acemodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.ace.AceRequestEntity;
import com.vinplay.ace.entity.DepositRequestEntity;
import com.vinplay.ace.service.HttpAceService;
import com.vinplay.ace.utils.AceUtils;
import com.vinplay.api.entities.ACEResponse;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.common.HttpCommon;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dal.service.impl.AceMoneyService;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class DepositAceProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");
    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false,"500");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
       // String money = request.getParameter("money");
        Long mMoney = 0l;
        logger.debug("4018" +" enroll on 4018");
        synchronized (this){
            if(userService.checkAccesstoken(nickname,accessToken)){
                long currentMoney = userService.getCurrentMoneyUserCache(nickname,"vin");
                mMoney= currentMoney;
                if(nickname.isEmpty() || currentMoney <=0) return aceResponse.toJson();
                if(!PortalUtils.allowDepositAce(nickname)){
                    aceResponse.setErrorCode("nạp rút qua nhanh");
                    return  aceResponse.toJson();
                }
                logger.debug("4018" +" enroll on login " + mMoney +" " + nickname);
                try {
                    UserModel userModel = userService.getUserByNickName(nickname);
                    String ticket_id = String.valueOf(VinPlayUtils.generateTransId());
                    DepositRequestEntity depositRequest = new DepositRequestEntity(String.valueOf(userModel.getId()),userModel.getUsername()
                            ,userModel.getNickname(),accessToken,0,mMoney, AceUtils.generateKeySc(accessToken,ticket_id,mMoney));
                    depositRequest.setTicket_id(ticket_id);

                    // tru tien
                    aceResponse =userService.updateMoneyFromAdmin(nickname, (long)-mMoney, "vin",
                            "BongDA", "Chuyển tiền bóng đá",
                            "Chuyển tiền bóng đá", 0);
                    BroadCastUserMoney.pushBroadCast(nickname);

                        if(aceResponse.isSuccess()){ // nếu thất bại thì rollback lại tiền

                            String response =   this.getRequest(depositRequest);
                            logger.debug("4018" +" call to 3rd " + response+" " + nickname);
                            ACEResponse res = new ObjectMapper().readValue( response,ACEResponse.class);
                            logger.debug("4018" +" call to 3rd " + res.isSuccess() +" " + nickname);
                        if(!res.isSuccess()){
                            res.setSuccess(false);
                            aceResponse.setErrorCode(" chuyển tiền sang bên thứ 3 thất bại lý do: " +res.getMsg());
                        // todo : rollback money when fail
//                            userService.updateMoneyFromAdmin(nickname, (long)mMoney, "vin",
//                                    "BongDA", "Chuyển tiền bóng đá thất bại",
//                                    "Chuyển tiền bóng đá thất bại", 0);
//                            BroadCastUserMoney.pushBroadCast(nickname);
                         }else {

                        }
                        }


                } catch (Exception e) {
                    e.printStackTrace();
                    if(e instanceof IOException){
//                        userService.updateMoneyFromAdmin(nickname, (long)mMoney, "vin",
//                                "BongDA", "Rollback tiền bóng đá",
//                                "Rollback tiền bóng đá", 0);
                    }
                    aceResponse.setErrorCode("chuyển tiền thất bại "+e.getMessage());
                }

            }
        }

        //userService.getU
        AceMoneyService aceMoneyService = new AceMoneyService();
        aceMoneyService.banDepositUser(nickname,50000);
        return aceResponse.toJson();
    }
    private String getRequest(DepositRequestEntity depositRequest) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, depositRequest.toJson());
        Request request = new Request.Builder()
                .url("https://bandoluuniem.net/sport/deposit?brand=sunvin")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
