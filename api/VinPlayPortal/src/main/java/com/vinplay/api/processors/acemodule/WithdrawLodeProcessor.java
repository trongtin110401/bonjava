package com.vinplay.api.processors.acemodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.StringMap;
import com.vinplay.api.entities.*;
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

public class WithdrawLodeProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public synchronized String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
        Gson gson = new Gson();
        AceMoneyService aceMoneyService = new AceMoneyService();
        Long mMoney = 0l;
        if (userService.checkAccesstoken(nickname, accessToken)) {
            if (mMoney < 0 || nickname.isEmpty()) return aceResponse.toJson();
            if (!PortalUtils.allowWithDrawAce(nickname)) {
                aceResponse.setErrorCode("nạp rút qua nhanh");
                return aceResponse.toJson();
            }
            //ban truoc 1s
            aceMoneyService.banDepositUser(nickname, 1000);
            try {
                UserModel userModel = userService.getUserByNickName(nickname);
                DepositLodeModel depositLodeModel = new DepositLodeModel(userModel.getNickname(), mMoney);
                LodeResponse userInfoAce = this.getUserInfor(depositLodeModel);
                StringMap mapTree = (StringMap) userInfoAce.msg;
                double realMoney = (double) mapTree.get("currentBalanceGame");
                mMoney = (long) realMoney;

                if ((long) mMoney <= 0) return aceResponse.toJson();

                depositLodeModel.amount = mMoney;

                String response = this.getRequest(depositLodeModel);

                HashMap<String, Object> res = (HashMap<String, Object>) new ObjectMapper().readValue(response, Object.class);
                int code = (int) res.get("errorCode");
                if (code == 0) {
                    aceResponse = userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
                            "Lode", "Nhận tiền từ Lode NEW",
                            "Nhận tiền Lode NEW", 0);
                    BroadCastUserMoney.pushBroadCast(nickname);

                } else {
                    aceResponse.setErrorCode(" chuyển tiền sang game thất bại lý do: " + res.get("msg"));

                }

            } catch (Exception e) {
                e.printStackTrace();
                aceResponse.setErrorCode("rút tiền thất bại " + e.getMessage());
            }

        }
        //userService.getU
        aceMoneyService.banWidrawUser(nickname, 5000);
        return aceResponse.toJson();
    }

    private LodeResponse getUserInfor(DepositLodeModel depositRequest) throws IOException {
        Gson gson = new Gson();
        String urlRequest = DepositLodeModel.DOMAIN + "quickplay?brand=" + DepositLodeModel.GAMENAME + "&hash=" + depositRequest.getMd5Deposit() + "&username=" + depositRequest.userName + "&amount=" + depositRequest.amount;
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(urlRequest)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        if (body.contains("\"errorCode\":0")) {
            LodeResponse res = gson.fromJson(body, LodeResponse.class);
            return res;
        } else {
            throw new IOException(body);
        }
    }

    private String getRequest(DepositLodeModel depositRequest) throws IOException {
        String urlRequest = DepositLodeModel.DOMAIN + "withdraw?brand=" + DepositLodeModel.GAMENAME + "&hash=" + depositRequest.getMd5Deposit() + "&username=" + depositRequest.userName + "&amount=" + depositRequest.amount;
        OkHttpClient client = HttpCommon2.getInstance().getHttpClient().newBuilder().callTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(urlRequest)
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        if (body.contains("\"errorCode\":0")) {
            return body;
        } else {
            throw new IOException("ERROR");
        }
    }
}
