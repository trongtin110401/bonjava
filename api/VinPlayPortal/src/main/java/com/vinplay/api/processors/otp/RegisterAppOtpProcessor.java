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

import bitzero.util.common.business.Debug;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.usercore.dao.impl.SecurityDaoImpl;
import com.vinplay.usercore.service.OtpService;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.StatusUser;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.Set2AFResponse;
import com.vinplay.vbee.common.statics.TimeBasedOneTimePasswordUtil;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class RegisterAppOtpProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String username = request.getParameter("un");        
        Set2AFResponse res = new Set2AFResponse(false, "1001");
        try {
            if (username == null) return res.toJson();
            if (username.isEmpty()) return res.toJson();
            int statusGame = GameCommon.getValueInt((String)"STATUS_GAME");
            if (statusGame == StatusGames.MAINTAIN.getId()) {
                res.setErrorCode("1114");
                logger.debug((Object)("Response login: " + res.toJson()));
                return res.toJson();
            }
            UserServiceImpl userService = new UserServiceImpl();
            UserModel userModel = userService.getUserByUserName(username);
            if (userModel == null) {
                res.setErrorCode("1005");
                return res.toJson();
            }
            if (statusGame == StatusGames.SANDBOX.getId() && !userModel.isCanLoginSandbox()) {
                res.setErrorCode("1114");
                logger.debug((Object)("Response login: " + res.toJson()));
                return res.toJson();
            }
            String nick_name = userModel.getNickname();
            if (nick_name != null && !nick_name.trim().isEmpty()) {
                // ok send otp
                OtpService otpService = new OtpServiceImpl();
                int ret = otpService.sendVoiceOtp(nick_name, "",true);
                if (ret != 0) {
                    Debug.trace("Cannot send OTP message!");
                    res.setErrorCode("1011");
                    return res.toJson();
                }
                else
                {
                    res.setErrorCode("0");
                    return res.toJson();
                }
//                String secret = VinPlayUtils.getUserSecretKey((String) userModel.getNickname());
//                if (secret == null || secret.isEmpty()) {
//                    secret = TimeBasedOneTimePasswordUtil.generateBase32Secret();
//                    HazelcastInstance client = HazelcastClientFactory.getInstance();
//                    IMap userMap = client.getMap("users");
//                    if (!userMap.containsKey((Object)nick_name)) {
//                        res.setErrorCode("3002");
//                        return res.toJson();
//                    }
//                    try {
//                        userMap.lock((Object)nick_name);
//                        UserCacheModel user = (UserCacheModel)userMap.get((Object)nick_name);  
//                        if (VinPlayUtils.setUserSecretKey((String) nick_name, (String) secret)) {
//                            res.setSuccess(true);
//                            res.setNickname(nick_name);
//                            res.setErrorCode("0");
//                            res.setSecret(secret);
//                            String string = res.toJson();
//                            return string;
//                        }
//                        res.setErrorCode("3003");
//                        return res.toJson();
//                    }
//                    catch (Exception e) {
//                        res.setErrorCode(e.getMessage());
//                        logger.debug((Object)("activeMobile error: " + e.getMessage()));
//                        return res.toJson();
//                    }
//                    finally {
//                        userMap.unlock((Object)nick_name);
//                    }
//                }
//                else
//                {
//                    res.setSuccess(true);
//                    res.setNickname(nick_name);
//                    res.setErrorCode("0");
//                    res.setSecret(secret);
//                    String string = res.toJson();
//                    return string;
//                }
            }
            res.setErrorCode("2001");
            return res.toJson();
        }
        catch (Exception e2) {
            logger.debug((Object)e2);
        }
        return res.toJson();
    }
}

