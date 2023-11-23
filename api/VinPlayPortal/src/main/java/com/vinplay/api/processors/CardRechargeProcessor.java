/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.LogMoneyUserServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.LogMoneyUserResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import bitzero.util.common.business.Debug;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.api.entities.GetUserDetailResponse;
import com.vinplay.api.entities.GetUserInfoResponse;
import com.vinplay.api.entities.RechargeBankResponse;
import com.vinplay.api.entities.RechargeMomoResponse;
import com.vinplay.api.entities.UserTransaction;
import com.vinplay.api.processors.response.LogMoneyResponse;
import com.vinplay.dal.dao.LogMoneyUserDao;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.dal.service.LogMoneyUserService;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.LogMoneyUserServiceImpl;
import com.vinplay.dichvuthe.response.RechargeResponse;
import com.vinplay.dichvuthe.service.RechargeService;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.usercore.entities.TransferMoneyResponse;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.Platform;
import com.vinplay.vbee.common.enums.ProviderType;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogMoneyUserResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.LoginResponse;
import static game.modules.gameRoom.entities.GameMoneyInfo.userService;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class CardRechargeProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {   
        try
        {
            HttpServletRequest request = (HttpServletRequest)param.get();      
            String token = request.getParameter("t");      
            String userName = request.getParameter("un");      
            String serial = request.getParameter("s");
            String pin = request.getParameter("p");
            String amount = request.getParameter("a");
            String cardType = request.getParameter("ct");            
            UserServiceImpl userService = new UserServiceImpl();
            if (userName != null && !"".equals(userName))
            {               
                UserModel userModel = userService.getUserByUserName(userName);
                if (userModel != null)
                {
                    logger.debug((Object) ("Recharge error: " + PartnerConfig.CongGachThe + ":" + cardType + ":" + serial + ":" + pin + ":" + amount));
                    Platform platform = Platform.find("web");
                    amount = amount.replace(".", "");
                    amount = amount.replace(",", "");
                    RechargeService rechargeService = new RechargeServiceImpl();
                    RechargeResponse res = null;
                    if (PartnerConfig.CongGachThe.trim().equals("GACHTHE")) {
                        res = rechargeService.rechargeByGachThe(userModel.getNickname(), ProviderType.getProviderByValue(cardType.toLowerCase()), serial, pin, amount, platform.getName(), 0);
                    } else if (PartnerConfig.CongGachThe.trim().equals("NAPTIENGA")) {
                        res = rechargeService.rechargeByNapTienGa(userModel.getNickname(), ProviderType.getProviderByValue(cardType.toLowerCase()), serial, pin, amount, platform.getName(), 0);
                    } else if (PartnerConfig.CongGachThe.trim().equals("MUACARD")) {
                        res = rechargeService.rechargeByMuaCard(userModel.getNickname(), ProviderType.getProviderByValue(cardType.toLowerCase()), serial, pin, amount, platform.getName(), 0);
                    } else if (PartnerConfig.CongGachThe.trim().equals("MUACARD24H")) {
                        res = rechargeService.rechargeByMuaCard24h(userModel.getNickname(), ProviderType.getProviderByValue(cardType.toLowerCase()), serial, pin, amount, platform.getName(), 0);
                    } else if (PartnerConfig.CongGachThe.trim().equals("ECARD")) {
                        res = rechargeService.rechargeByECard(userModel.getNickname(), ProviderType.getProviderByValue(cardType.toLowerCase()), serial, pin, amount, platform.getName(), 0);
                    } else if (PartnerConfig.CongGachThe.trim().equals("ZOANCARD")) {
                        res = rechargeService.rechargeByZoan(userModel.getNickname(), ProviderType.getProviderByValue(cardType.toLowerCase()), serial, pin, amount, platform.getName(), 0);
                    } else {
                        res = new RechargeResponse(-1, 0, 0, 0);
                    }
                    return res.toJson();
                }
            }            
            return "{\"code\":500,\"message\":\"error\"}";
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            logger.info(ex.getMessage());
            logger.info(sStackTrace);
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }    
}

