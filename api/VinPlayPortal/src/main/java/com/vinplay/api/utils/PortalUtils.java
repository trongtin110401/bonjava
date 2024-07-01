/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.hazelcast.client.HazelcastClientNotActiveException
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl
 *  com.vinplay.dichvuthe.response.CashoutUserDailyResponse
 *  com.vinplay.dichvuthe.service.impl.AlertServiceImpl
 *  com.vinplay.usercore.dao.impl.VippointDaoImpl
 *  com.vinplay.usercore.service.MarketingService
 *  com.vinplay.usercore.service.impl.GameConfigServiceImpl
 *  com.vinplay.usercore.service.impl.LuckyServiceImpl
 *  com.vinplay.usercore.service.impl.MailBoxServiceImpl
 *  com.vinplay.usercore.service.impl.MarketingServiceImpl
 *  com.vinplay.usercore.service.impl.SecurityServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.MarketingModel
 *  com.vinplay.vbee.common.models.UserClientInfo
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.models.cache.UserExtraInfoModel
 *  com.vinplay.vbee.common.models.vippoint.UserVPEventModel
 *  com.vinplay.vbee.common.response.LoginResponse
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 */
package com.vinplay.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hazelcast.client.HazelcastClientNotActiveException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.vinplay.api.processors.GetAppConfigProcesscor;
import com.vinplay.dal.service.impl.AceMoneyService;
import com.vinplay.dal.service.impl.CardBanMoneyService;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.response.CashoutUserDailyResponse;
import com.vinplay.dichvuthe.service.impl.AlertServiceImpl;
import com.vinplay.usercore.dao.impl.VippointDaoImpl;
import com.vinplay.usercore.service.MarketingService;
import com.vinplay.usercore.service.impl.GameConfigServiceImpl;
import com.vinplay.usercore.service.impl.LuckyServiceImpl;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.MarketingServiceImpl;
import com.vinplay.usercore.service.impl.SecurityServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.MarketingModel;
import com.vinplay.vbee.common.models.UserClientInfo;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.models.cache.UserExtraInfoModel;
import com.vinplay.vbee.common.models.vippoint.UserVPEventModel;
import com.vinplay.vbee.common.response.LoginResponse;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.python.parser.ast.Str;

public class PortalUtils {
    private static final Logger logger = Logger.getLogger((String) "api");
    private static MarketingService mktService = new MarketingServiceImpl();

    public static LoginResponse loginSuccess(UserModel userModel, HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException, JsonProcessingException, SQLException {

        // kick user logging in other device
        String pf = request.getParameter("pf");
        if (pf != null && pf.equalsIgnoreCase("FISH")) {
            // do nothing
        } else {
            kickSession(userModel);
        }

        boolean success = true;
        String accessToken = "";
        String sessionKey = "";
        UserServiceImpl ser = new UserServiceImpl();
        int luckyRotate = 0;
        if (userModel.getDaily() == 0) {
            LuckyServiceImpl luckySer = new LuckyServiceImpl();
            luckyRotate = luckySer.receiveRotateDaily(userModel.getId(), userModel.getNickname());
        }
        String ip = PortalUtils.getIpAddress(request);
        String platform = request.getParameter("pf");
        try {
            IMap userExtraMap;
            UserCacheModel userCache;
            block18:
            {
                HazelcastInstance instance = HazelcastClientFactory.getInstance();
                IMap userMap = instance.getMap("users");
                userExtraMap = instance.getMap("cache_user_extra_info");
                IMap tokenMap = instance.getMap("cacheToken");
                userCache = null;
                if (userMap.containsKey((Object) userModel.getNickname())) {
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
                        userCache.setId(userModel.getId());
                        UserCacheModel uc = ser.checkMoneyNegative(userCache);
                        if (uc != null) {
                            userCache = uc;
                            success = false;
                        }
                        tokenMap.put((Object) accessToken, (Object) userModel.getNickname(), 180L, TimeUnit.MINUTES);
                        userMap.put((Object) userModel.getNickname(), (Object) userCache);
                    } catch (Exception e) {
                        logger.debug((Object) e);
                        success = false;
                        break block18;
                    }
                    try {
                        userMap.unlock((Object) userModel.getNickname());
                    } catch (Exception e) {
                    }
                } else {
                    UserCacheModel uc2;
                    CashoutDaoImpl csDao = new CashoutDaoImpl();
                    CashoutUserDailyResponse cs = csDao.getCashoutUserToday(userModel.getNickname());
                    MarketingModel mktModel = mktService.getMKTInfo(userModel.getUsername());
                    VippointDaoImpl vpDao = new VippointDaoImpl();
                    UserVPEventModel vpModel = vpDao.getUserVPByNickName(userModel.getNickname());
                    userCache = new UserCacheModel(userModel.getId(), userModel.getUsername(), userModel.getNickname(), userModel.getPassword(), userModel.getEmail(), userModel.getFacebookId(), userModel.getGoogleId(), userModel.getMobile(), userModel.getBirthday(), userModel.isGender(), userModel.getAddress(), userModel.getVin(), userModel.getXu(), userModel.getVinTotal(), userModel.getXuTotal(), userModel.getSafe(), userModel.getRechargeMoney(), userModel.getVippoint(), userModel.getDaily(), userModel.getStatus(), userModel.getAvatar(), userModel.getIdentification(), userModel.getVippointSave(), userModel.getCreateTime(), userModel.getMoneyVP(), userModel.getSecurityTime(), userModel.getLoginOtp(), userModel.isBot(), cs.getCashout(), cs.getCashoutTime(), vpModel.getVpEvent(), vpModel.getVpReal(), vpModel.getVpAdd(), vpModel.getVpSub(), vpModel.getNumAdd(), vpModel.getNumSub(), vpModel.getPlace(), vpModel.getPlaceMax());
                    userCache.setClient(userModel.getClient());
                    accessToken = VinPlayUtils.genAccessToken((int) userModel.getId());
                    userCache.setAccessToken(accessToken);
                    userCache.setLastMessageId(0L);
                    userCache.setLastActive(new Date());
                    userCache.setOnline(0);
                    userCache.setIp(ip);
                    if (mktModel != null) {
                        userCache.setCampaign(mktModel.campaign);
                        userCache.setMedium(mktModel.medium);
                        userCache.setSource(mktModel.source);
                    }
                    if ((uc2 = ser.checkMoneyNegative(userCache)) != null) {
                        userCache = uc2;
                        success = false;
                    }
                    tokenMap.put((Object) accessToken, (Object) userModel.getNickname(), 180L, TimeUnit.MINUTES);
                    userMap.put((Object) userCache.getNickname(), (Object) userCache);
                }
            }
            int mobileSecure = userCache.isHasMobileSecurity() ? 1 : 0;
            int appSecure = userCache.isHasAppSecurity() ? 1 : 0;
            String birthday = "";
            if (userCache.getBirthday() != null && !userCache.getBirthday().isEmpty()) {
                birthday = userCache.getBirthday();
            }
            UserClientInfo userInfo = new UserClientInfo(userCache.getNickname(), userCache.getAvatar(), userCache.getVin(), userCache.getXu(), userCache.getVippoint(), userCache.getVippointSave(), VinPlayUtils.parseDateToString((Date) userCache.getCreateTime()), ip, false, luckyRotate, userCache.getDaily(), mobileSecure, birthday, appSecure);
            userInfo.setId(userModel.getId());
            sessionKey = VinPlayUtils.genSessionKey((UserClientInfo) userInfo);
            try {
                SecurityServiceImpl sercuSer = new SecurityServiceImpl();
                if (!ip.contains("45.76.178.154")) {
                    sercuSer.saveLoginInfo(userCache.getId(), userCache.getUsername(), userCache.getNickname(), ip, PortalUtils.getUserAgent(request), 1, platform);
                }
                UserExtraInfoModel userExtraModel = new UserExtraInfoModel(userCache.getNickname(), platform);
                userExtraMap.put((Object) userCache.getNickname(), (Object) userExtraModel);
                logger.debug((Object) ("User " + userCache.getNickname() + " login success in platform: " + platform));
            } catch (Exception e2) {
                logger.error((Object) e2);
            }
        } catch (HazelcastClientNotActiveException he) {
            success = false;
        }
        if (success) {
            return new LoginResponse(true, "0", sessionKey, accessToken);
        }
        return new LoginResponse(false, "1001");
    }

    private static void kickSession(UserModel userModel) {
        String nickname = userModel.getNickname();
        HazelcastInstance instance = HazelcastClientFactory.getInstance();

        IMap<String, String> map = instance.getMap("LOGIN_OTHER_DEVICE_MAP");
        map.put(nickname, nickname);

        IQueue queue = instance.getQueue("LOGIN_OTHER_DEVICE_QUEUE");
        if (queue != null) {
            queue.offer(nickname);
        }

        while (map.containsKey(nickname)) {
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static String getUserAgent(HttpServletRequest request) {
        String agent = request.getHeader("USER-AGENT");
        if (agent == null) {
            agent = "";
        }
        return agent;
    }

    public static void loadGameConfig() throws SQLException, JSONException, ParseException {
        GameConfigServiceImpl ser = new GameConfigServiceImpl();
        GetAppConfigProcesscor.configs = ser.getGameConfig();
        GameCommon.init();
    }

    public static synchronized boolean allowDepositAce(String username) {
        long timeUnBan;
        AceMoneyService aceMoneyService = new AceMoneyService();
        boolean allow = true;

        if (aceMoneyService.getBanDepositTime(username) != 0L) {
            allow = false;
        }
        return allow;
    }

    public static boolean allowWithDrawAce(String username) { // vừa nạp vào ko cho rút ra luôn
        long timeUnBan;
        AceMoneyService aceMoneyService = new AceMoneyService();
        boolean allow = true;

        if (aceMoneyService.getBanDepositTime(username) != 0L) {
            allow = false;
        }
        return allow;
    }

    public static List<String> parseStringToList(String urlHelp) {
        ArrayList<String> res = new ArrayList<String>();
        if (urlHelp != null && !urlHelp.isEmpty()) {
            if (urlHelp.contains(",")) {
                String[] arr = urlHelp.trim().split(",");
                for (int i = 0; i < arr.length; ++i) {
                    res.add(arr[i]);
                }
            } else {
                res.add(urlHelp);
            }
        }
        return res;
    }

    public static boolean checkCaptcha(String captcha, String captchaId) {
        IMap captchaCache;
        boolean isCaptcha = false;
        if (!(captcha = captcha.trim().toLowerCase()).isEmpty() && (captchaCache = HazelcastClientFactory.getInstance().getMap("cacheCaptcha")).containsKey((Object) captchaId) && ((String) captchaCache.get((Object) captchaId)).equals(captcha)) {
            captchaCache.remove((Object) captchaId);
            isCaptcha = true;
        }
        return isCaptcha;
    }

    public static void sendMessageKichHoatBaoMatToUser(String nickname) {
        MailBoxServiceImpl mail = new MailBoxServiceImpl();
        mail.sendMailBoxFromByNickNameAdmin(nickname, "K\u00edch ho\u1ea1t b\u1ea3o m\u1eadt \u0111\u1ec3 s\u1eed d\u1ee5ng t\u00ednh n\u0103ng \u0111\u1ed5i th\u01b0\u1edfng", "Nh\u1eb1m m\u1ee5c \u0111\u00edch h\u1ed7 tr\u1ee3 t\u1ed1t nh\u1ea5t khi ng\u01b0\u1eddi ch\u01a1i g\u1eb7p s\u1ef1 c\u1ed1 c\u0169ng nh\u01b0 \u0111\u1ea3m b\u1ea3o \u0111\u1ea7y \u0111\u1ee7 quy\u1ec1n l\u1ee3i cho ng\u01b0\u1eddi ch\u01a1i. VINPLAY y\u00eau c\u1ea7u t\u1ea5t c\u1ea3 c\u00e1c t\u00e0i kho\u1ea3n mu\u1ed1n s\u1eed d\u1ee5ng Giftcode hay d\u00f9ng t\u00ednh n\u0103ng Ti\u00eau Vin \u0111\u1ec1u ph\u1ea3i \u0111\u0103ng k\u00fd b\u1ea3o m\u1eadt S\u0110T. Sau 24h k\u1ec3 t\u1eeb th\u1eddi \u0111i\u1ec3m k\u00edch ho\u1ea1t ng\u01b0\u1eddi ch\u01a1i s\u1ebd s\u1eed d\u1ee5ng \u0111\u01b0\u1ee3c c\u00e1c t\u00ednh n\u0103ng \u0111\u1ed5i th\u01b0\u1edfng. Chi ti\u1ebft xem t\u1ea1i vinplay.net");
    }
}

