/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors.otp;

import com.hazelcast.core.IMap;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.statics.TimeBasedOneTimePasswordUtil;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class GetAppOtpByTokenProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        try
        {
            HttpServletRequest request = (HttpServletRequest)param.get();
            long from = System.currentTimeMillis();
            long to = System.currentTimeMillis();        
            int timeStepSeconds = 30;
            long timeStepMillis = timeStepSeconds * 1000;
            String otp = "error";
            String userName = request.getParameter("un");
            String accessToken = request.getParameter("t");
            UserServiceImpl userService = new UserServiceImpl();
            if (userName != null && !"".equals(userName))
            {                
                UserModel userModel = userService.getUserByUserName(userName);
                if (userModel != null)
                {
                    IMap userMap = HazelcastClientFactory.getInstance().getMap("users");
                    if (!userMap.containsKey((Object)userModel.getNickname()))
                        return "invalid_nickname";
                    UserCacheModel userCache = (UserCacheModel) userMap.get((Object) userModel.getNickname());            
                    if (!userCache.isBanLogin()) {
                        if (userCache.getAccessToken().equals(accessToken)) {
                            String base32Secret = VinPlayUtils.getUserSecretKey((String)userCache.getNickname());                    
                            for (long millis = from; millis <= to; millis += timeStepMillis) {
                                otp = TimeBasedOneTimePasswordUtil.generateNumberString(base32Secret, millis, timeStepSeconds);
                                break;
                            }                    
                            long remain = 30 - (System.currentTimeMillis() / 1000) % 30;
                            return "{\"otp\":\""+ otp +"\",\"remain\":" + remain + "}";
                        }                        
                    }                    
                }
            }  
            return "-1";
        }
        catch (Exception ex)
        {
            logger.debug((Object)ex);
            return "-1";
        }
    }
}

