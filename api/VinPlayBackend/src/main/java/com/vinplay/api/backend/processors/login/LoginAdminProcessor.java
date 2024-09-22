/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl
 *  com.vinplay.dichvuthe.response.CashoutUserDailyResponse
 *  com.vinplay.usercore.dao.impl.VippointDaoImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.enums.StatusGames
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.UserClientInfo
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.models.vippoint.UserVPEventModel
 *  com.vinplay.vbee.common.response.LoginResponse
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.login;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.response.CashoutUserDailyResponse;
import com.vinplay.usercore.dao.impl.VippointDaoImpl;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserClientInfo;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.models.vippoint.UserVPEventModel;
import com.vinplay.vbee.common.response.LoginResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class LoginAdminProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");

    public String execute(Param<HttpServletRequest> param) {
        System.out.println("=================== Login 1");
        LoginResponse res;
        block17:
        {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String username = request.getParameter("un");
            String password = request.getParameter("pw");
            String otp = request.getParameter("otp");
            //logger.debug((Object)("Request login: username: " + username + ", password: " + password));
            res = new LoginResponse(false, "1001");
            if (username != null && password != null) {
                try {
                    UserServiceImpl userService = new UserServiceImpl();
                    UserModel userModel = userService.getUserByUserName(username);
                    if (userModel != null) {
                        System.out.println("=================== Login 2");
                        if (!userModel.isBanLogin()) {
                            System.out.println("=================== Login 3");
                            if (userModel.getPassword().equals(password)) {
                                System.out.println("=================== Login 4");
                                if (userModel.getNickname() != null && !userModel.getNickname().trim().isEmpty()) {
                                    System.out.println("=================== Login 5");
                                    UserCacheModel userCache;
                                    String accessToken;
                                    block18:
                                    {
                                        //if (userModel.getDaily() == 0 || userModel.getDaily() == 2) break block17;
                                        if (userModel.getDaily() == 0) break block17; // Cho phep dai ly cap 2 login
                                        int statusGame = GameCommon.getValueInt((String) "STATUS_GAME");
                                        if ((userModel.getDaily() == 1 || userModel.getDaily() == 2) && (statusGame == StatusGames.MAINTAIN.getId() || statusGame == StatusGames.SANDBOX.getId() && !userModel.isCanLoginSandbox())) {
//                                        if (userModel.getDaily() == 1 && (statusGame == StatusGames.MAINTAIN.getId() || statusGame == StatusGames.SANDBOX.getId() && !userModel.isCanLoginSandbox())) {
                                            res.setErrorCode("1114");
                                            logger.debug((Object) ("Response login: " + res.toJson()));
                                            System.out.println("=================== Login 6");
                                            return res.toJson();
                                        }
                                        String ip = this.getIpAddress(request);
                                        HazelcastInstance instance = HazelcastClientFactory.getInstance();
                                        IMap userMap = instance.getMap("users");
                                        IMap tokenMap = instance.getMap("cacheToken");
                                        accessToken = "";
                                        userCache = null;

                                        //validate user otp

                                        if (userModel.isHasAppSecurity() && otp != null && !otp.equals("")) {
                                            System.out.println("=================== Login 7");
                                            OtpServiceImpl otpService = new OtpServiceImpl();
                                            int resultCheckOtp = otpService.checkAppOTP(userModel.getNickname(), otp);
                                            if (resultCheckOtp != 0) {
                                                res.setErrorCode("1064");
                                                return res.toJson();
                                            }
                                        }
                                        if (userModel.isHasAppSecurity() && (otp == null || otp.equals(""))) {
                                            System.out.println("=================== Login 8");
                                            res.setErrorCode("10640");
                                            return res.toJson();
                                        }
                                        if (userMap.containsKey((Object) userModel.getNickname())) {
                                            System.out.println("=================== Login 9");
                                            try {
                                                userMap.lock((Object) userModel.getNickname());
                                                userCache = (UserCacheModel) userMap.get((Object) userModel.getNickname());
                                                if (userCache.getAccessToken() == null || VinPlayUtils.sessionTimeout((long) userCache.getLastActive().getTime())) {
                                                    accessToken = VinPlayUtils.genAccessToken((int) userModel.getId());
                                                    userCache.setAccessToken(accessToken);
                                                } else {
                                                    accessToken = userCache.getAccessToken();
                                                }
                                                userCache.setLastActive(new Date());
                                                userCache.setIp(ip);
                                                tokenMap.put((Object) accessToken, (Object) userModel.getNickname(), 180L, TimeUnit.MINUTES);
                                                userMap.put((Object) userModel.getNickname(), (Object) userCache);
                                            } catch (Exception e) {
                                                logger.debug((Object) e);
                                                break block18;
                                            }
                                            try {
                                                userMap.unlock((Object) userModel.getNickname());
                                            } catch (Exception e) {
                                            }
                                        } else {
                                            System.out.println("=================== Login 10");
                                            CashoutDaoImpl csDao = new CashoutDaoImpl();
                                            CashoutUserDailyResponse cs = csDao.getCashoutUserToday(userModel.getNickname());
                                            VippointDaoImpl vpDao = new VippointDaoImpl();
                                            UserVPEventModel vpModel = vpDao.getUserVPByNickName(userModel.getNickname());
                                            userCache = new UserCacheModel(userModel.getId(), userModel.getUsername(), userModel.getNickname(), userModel.getPassword(), userModel.getEmail(), userModel.getFacebookId(), userModel.getGoogleId(), userModel.getMobile(), userModel.getBirthday(), userModel.isGender(), userModel.getAddress(), userModel.getVin(), userModel.getXu(), userModel.getVinTotal(), userModel.getXuTotal(), userModel.getSafe(), userModel.getRechargeMoney(), userModel.getVippoint(), userModel.getDaily(), userModel.getStatus(), userModel.getAvatar(), userModel.getIdentification(), userModel.getVippointSave(), userModel.getCreateTime(), userModel.getMoneyVP(), userModel.getSecurityTime(), userModel.getLoginOtp(), userModel.isBot(), cs.getCashout(), cs.getCashoutTime(), vpModel.getVpEvent(), vpModel.getVpReal(), vpModel.getVpAdd(), vpModel.getVpSub(), vpModel.getNumAdd(), vpModel.getNumSub(), vpModel.getPlace(), vpModel.getPlaceMax());
                                            accessToken = VinPlayUtils.genAccessToken((int) userModel.getId());
                                            userCache.setAccessToken(accessToken);
                                            userCache.setLastMessageId(0L);
                                            userCache.setLastActive(new Date());
                                            userCache.setOnline(0);
                                            userCache.setIp(ip);
                                            tokenMap.put((Object) accessToken, (Object) userModel.getNickname(), 180L, TimeUnit.MINUTES);
                                            userMap.put((Object) userCache.getNickname(), (Object) userCache);
                                        }
                                    }
                                    System.out.println("=================== Login 11");
                                    int mobileSecure = userCache.isHasMobileSecurity() ? 1 : 0;
                                    int appSecure = userCache.isHasAppSecurity() ? 1 : 0;

                                    String birthday = "";
                                    if (userCache.getBirthday() != null && !userCache.getBirthday().isEmpty()) {
                                        birthday = userCache.getBirthday();
                                    }
                                    System.out.println("=================== Login 12");
                                    UserClientInfo userInfo = new UserClientInfo(userCache.getNickname(), userCache.getAvatar(), userCache.getVinTotal(), userCache.getXuTotal(), userCache.getVippoint(), userCache.getVippointSave(), VinPlayUtils.parseDateToString((Date) userCache.getCreateTime()), "", false, 0, userCache.getDaily(), mobileSecure, birthday, appSecure);
                                    String sessionKey = VinPlayUtils.genSessionKey((UserClientInfo) userInfo);
                                    res = new LoginResponse(true, "0", sessionKey, accessToken);
                                    break block17;
                                }
                                res.setErrorCode("2001");
                                break block17;
                            }
                            res.setErrorCode("1007");
                            break block17;
                        }
                        res.setErrorCode("1109");
                        break block17;
                    }
                    res.setErrorCode("1005");
                } catch (Exception e2) {
                    logger.debug((Object) e2);
                }
            }
        }
        logger.debug((Object) ("Response login: " + res.toJson()));
        return res.toJson();
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}

