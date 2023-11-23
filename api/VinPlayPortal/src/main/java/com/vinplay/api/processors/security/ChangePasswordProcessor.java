/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.enums.StatusGames
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors.security;

import bitzero.util.common.business.Debug;
import com.hazelcast.core.IMap;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.dichvuthe.service.impl.AlertServiceImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.OtpService;
import com.vinplay.usercore.service.SecurityService;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.SecurityServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.BaseResponseModel;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class ChangePasswordProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        BaseResponseModel res = new BaseResponseModel(false, "1001");
        String username = request.getParameter("un");
        String token = request.getParameter("t");
        String oldPass = request.getParameter("op");
        String newPass = request.getParameter("np");
        if (username != null && oldPass != null && newPass != null) {
            try {                
                    UserServiceImpl userService = new UserServiceImpl();
                    UserModel userModel = userService.getUserByUserName(username);
                    if (userModel != null) {                       
                        if (userModel.getNickname() != null && !userModel.getNickname().isEmpty()) {
                            IMap userMap = HazelcastClientFactory.getInstance().getMap("users");
                            if (!userMap.containsKey((Object)userModel.getNickname()))
                            {
                                return res.toJson();
                            }
                            UserCacheModel userCache = (UserCacheModel) userMap.get((Object) userModel.getNickname());    
                            if (userCache != null)
                            {
                                if (userCache.getAccessToken().equals(token)) {    
                                    SecurityService securityService = new SecurityServiceImpl();
                                    byte result = securityService.changePassword(userModel.getNickname(), oldPass, newPass, false);
                                    res.setErrorCode(result + "");
                                    if (result == 0)
                                        res.setSuccess(true);
                                }
                            }                                                        
                        } else {
                            res.setErrorCode("2001");
                        }
                    } else {
                        res.setErrorCode("1005");
                    }
            }
            catch (Exception e) {
                logger.debug((Object)e);
            }
        }
        return res.toJson();
    }
}

