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

import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.statics.TimeBasedOneTimePasswordUtil;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class GetAppOtpProcessor
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
            String otp = "-1";
            String username = request.getParameter("un");
            String secret = request.getParameter("s");
            UserServiceImpl userService = new UserServiceImpl();
            UserModel userModel = userService.getUserByUserName(username);
            if (userModel != null)
            {
                String base32Secret = VinPlayUtils.getUserSecretKey((String)userModel.getNickname());
                if (secret.equals(base32Secret))
                {
                    for (long millis = from; millis <= to; millis += timeStepMillis) {
                        otp = TimeBasedOneTimePasswordUtil.generateNumberString(base32Secret, millis, timeStepSeconds);
                        break;
                    }
                }
            }
            return otp + "";
        }
        catch (Exception ex)
        {            
            logger.debug((Object)ex);
            return "error";
        }
    }
}

