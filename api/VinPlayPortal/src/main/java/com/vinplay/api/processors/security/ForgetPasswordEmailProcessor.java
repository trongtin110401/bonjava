/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dichvuthe.service.impl.AlertServiceImpl
 *  com.vinplay.usercore.dao.impl.SecurityDaoImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.enums.StatusGames
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  com.vinplay.vbee.common.utils.StringUtils
 *  com.vinplay.vbee.common.utils.UserValidaton
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors.security;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dichvuthe.service.impl.AlertServiceImpl;
import com.vinplay.usercore.dao.impl.SecurityDaoImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.StringUtils;
import com.vinplay.vbee.common.utils.UserValidaton;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class ForgetPasswordEmailProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        BaseResponseModel res;
        block15 : {
            HttpServletRequest request = (HttpServletRequest)param.get();
            res = new BaseResponseModel(false, "1001");
            String username = request.getParameter("un");
            String email = request.getParameter("email");
            if (username != null && email != null) {
                try {
                    int statusGame = GameCommon.getValueInt((String)"STATUS_GAME");
                    if (statusGame == StatusGames.MAINTAIN.getId()) {
                        res.setErrorCode("1114");
                        logger.debug((Object)("Response login: " + res.toJson()));
                        return res.toJson();
                    }
                    if (UserValidaton.validateEmail((String)email)) {
                        UserServiceImpl userService = new UserServiceImpl();
                        UserModel userModel = userService.getUserByUserName(username);
                        if (userModel == null) break block15;
                        if (statusGame == StatusGames.SANDBOX.getId() && !userModel.isCanLoginSandbox()) {
                            res.setErrorCode("1114");
                            logger.debug((Object)("Response login: " + res.toJson()));
                            return res.toJson();
                        }
                        if (userModel.getEmail() == null || userModel.getEmail().isEmpty() || !userModel.isHasEmailSecurity() || userModel.getNickname() == null || userModel.getNickname().isEmpty()) break block15;
                        if (email.equals(userModel.getEmail())) {
                            String password;
                            block16 : {
                                password = StringUtils.randomString((int)6);
                                SecurityDaoImpl securityDao = new SecurityDaoImpl();
                                if (securityDao.updateUserInfo(userModel.getId(), VinPlayUtils.getMD5Hash((String)password), 2)) {
                                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                                    IMap userMap = client.getMap("users");
                                    if (userMap.containsKey((Object)userModel.getNickname())) {
                                        try {
                                            userMap.lock((Object)userModel.getNickname());
                                            UserCacheModel user = (UserCacheModel)userMap.get((Object)userModel.getNickname());
                                            user.setPassword(VinPlayUtils.getMD5Hash((String)password));
                                            userMap.put((Object)userModel.getNickname(), (Object)user);
                                            res.setErrorCode("0");
                                            res.setSuccess(true);
                                        }
                                        catch (Exception e) {
                                            logger.debug((Object)e);
                                            break block16;
                                        }
                                        try {
                                            userMap.unlock((Object)userModel.getNickname());
                                        }
                                        catch (Exception e) {}
                                    } else {
                                        res.setErrorCode("0");
                                        res.setSuccess(true);
                                    }
                                }
                            }
                            if (res.getErrorCode().equals("0")) {
                                AlertServiceImpl alertSer = new AlertServiceImpl();
                                ArrayList<String> receives = new ArrayList<String>();
                                receives.add(email);
                                String content = " Dear " + userModel.getNickname() + ".<br><br> C\u1ea3m \u01a1n b\u1ea1n \u0111\u00e3 s\u1eed d\u1ee5ng d\u1ecbch v\u1ee5 c\u1ee7a ch\u00fang t\u00f4i. M\u1eadt kh\u1ea9u m\u1edbi c\u1ee7a b\u1ea1n l\u00e0 " + password + ". Vui l\u00f2ng truy c\u1eadp " + GameCommon.getValueStr((String)"WEB") + " \u0111\u1ec3 \u0111\u1ed5i l\u1ea1i m\u1eadt kh\u1ea9u.<br><br>" + GameCommon.getValueStr((String)"SIGN_EMAIL");
                                alertSer.sendEmail("[VinPlay] Th\u00f4ng b\u00e1o m\u1eadt kh\u1ea9u m\u1edbi.", content, receives);
                            }
                            break block15;
                        }
                        res.setErrorCode("1028");
                        break block15;
                    }
                    res.setErrorCode("103");
                }
                catch (Exception e2) {
                    logger.debug((Object)e2);
                }
            }
        }
        return res.toJson();
    }
}

