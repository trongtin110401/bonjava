package com.vinplay.api.processors;

import com.google.gson.Gson;
import com.vinplay.api.processors.cashout.Mo2Enty;
import com.vinplay.api.processors.cashout.MomoEnty;
import com.vinplay.api.processors.cashout.NapSunVinBankMomo;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class GetInfoMomoSunProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();

            String accessToken = request.getParameter("at");
            RechargeServiceImpl rechargeService = new RechargeServiceImpl();
            String nickname = this.getNicknameFromAcesstoken(accessToken);

            DepositBankModel depositBankModel = rechargeService.finMoMoDeposit(nickname);
            SimpleDateFormat sim = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String debug = "";
            if(depositBankModel != null) {
                Date currentDate = new Date();
                Date dateCreate = sim.parse(depositBankModel.getCreatedAt());
//                Long time_end = Math.abs(time_check - timelog);
                Long time_con = (currentDate.getTime() - dateCreate.getTime())/1000;
                if(time_con > 720) {
                    //qua thoi gian
                    rechargeService.cancelMomoById(depositBankModel.getId());
                    String respx = "{ \"errorCode\": 200, \"errorDescription\": \"Thành công.\", \"infomationAccount\": \""+""+"\", \"comment\": \"\", \"qrcode\": \"\", \"type\": \"Momo\", \"bankCode\": null }";
                    return respx;
                }else {
                    long real_con = 720-time_con;
                    //con han
                    String respx = "{ \"comment\": \""+depositBankModel.Description+"\", \"TrainID\": \""+depositBankModel.Id+"\", \"phoneNum\": \""+depositBankModel.BankAccountNumber+"\", \"phoneName\": \""+depositBankModel.BankAccountName+"\", \"timeToExpired\": "+real_con+", \"amount\": 1 }";
                    return respx;
                }
            }else {
                String respx = "{ \"errorCode\": 200, \"errorDescription\": \"Thành công.\", \"infomationAccount\": \""+""+"\", \"comment\": \"\", \"qrcode\": \"\", \"type\": \"Momo\", \"bankCode\": null }";
                return respx;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
    private String getNicknameFromAcesstoken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
