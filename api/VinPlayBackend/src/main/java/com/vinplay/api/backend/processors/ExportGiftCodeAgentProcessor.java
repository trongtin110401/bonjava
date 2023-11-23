/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.usercore.service.impl.GiftCodeAgentServiceImpl
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.GiftCodeMessage
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.response.GiftCodeAgentResponse
 *  com.vinplay.vbee.common.response.ResultGiftCodeAgentResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.usercore.dao.impl.MoneyInGameDaoImpl;
import com.vinplay.usercore.service.impl.GiftCodeAgentServiceImpl;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.GiftCodeMessage;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.GiftCodeAgentResponse;
import com.vinplay.vbee.common.response.ResultGiftCodeAgentResponse;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

public class ExportGiftCodeAgentProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger((String)"backend");
    
    public String execute(Param<HttpServletRequest> param){
        HttpServletRequest request = (HttpServletRequest)param.get();
        GiftCodeAgentResponse code = new GiftCodeAgentResponse();
        ResultGiftCodeAgentResponse response = new ResultGiftCodeAgentResponse(false, "1001");
        String gia = request.getParameter("gp");
        String soluong = request.getParameter("gq");
        String nguon = request.getParameter("gs");
        String dotphathanh = request.getParameter("gl");
        String moneyType = request.getParameter("mt");
        String type = request.getParameter("type");
        String nickName = request.getParameter("nn");
        String otp = request.getParameter("otp");
        String typeapp = request.getParameter("ta");
        if (!(gia == null || gia.equals("") || soluong == null || soluong.equals("") || nguon == null || nguon.equals("") || dotphathanh == null || dotphathanh.equals(""))) {
            //if (typeapp != null && (typeapp.equals("1") || typeapp.equals("0"))) {
            if (true) { //Minhanh
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap userMap = client.getMap("users");
                if(!userMap.containsKey((Object)nickName)){
                    return response.toJson();
                }
                try{
                    MoneyInGameDaoImpl dao = new MoneyInGameDaoImpl(); //Minhanh
                    UserCacheModel user = dao.getUserByNickName(nickName); //Minhanh
//                    userMap.lock((Object)nickName);
//                    UserCacheModel user = (UserCacheModel)userMap.get((Object)nickName);
//                    if(user.isHasAppSecurity() && (otp == null || otp.equals(""))){
//                        return response.toJson();
//                    }
//                    if(user.isHasAppSecurity() && otp != null && !otp.isEmpty()){
//                        OtpServiceImpl otpService = new OtpServiceImpl();
//                        int resultCheckOtp = otpService.checkAppOTP(nickName, otp);
//                        if(resultCheckOtp != 0){
//                            response.setErrorCode("10004");
//                            return response.toJson();
//                        }
//                    }
                    GiftCodeAgentServiceImpl service = new GiftCodeAgentServiceImpl();
                    if (type.equals("0"))
                        type = "1";
                    GiftCodeMessage msg = new GiftCodeMessage("", gia, Integer.parseInt(soluong), nguon, 1, 1, Integer.parseInt(moneyType), dotphathanh, type, "");


                    long currentMoney = user.getCurrentMoney("vin");
                    code = service.exportGiftCode(msg, currentMoney, nickName);
                    if (code.ErrorCode == 0) {
                        response.setErrorCode("0");
                        response.setSuccess(true);
                        response.setTransactions(code);
                    } else if (code.ErrorCode == 1) {
                        response.setErrorCode("10002");
                    } else if (code.ErrorCode == 2) {
                        response.setErrorCode("10003");
                    }
                    return response.toJson();
                }catch (Exception e){
                    return response.toJson();
                }


            }

            return response.toJson();
        }
        return "MISSING PARAMETTER";
    }
}

