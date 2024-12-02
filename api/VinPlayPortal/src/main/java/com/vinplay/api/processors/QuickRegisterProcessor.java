/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.dao.impl.UserDaoImpl
 *  com.vinplay.usercore.service.impl.MarketingServiceImpl
 *  com.vinplay.usercore.service.impl.SecurityServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.usercore.utils.UserMakertingUtil
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.enums.StatusGames
 *  com.vinplay.vbee.common.messages.UserMarketingMessage
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  com.vinplay.vbee.common.utils.UserValidaton
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import com.vinplay.api.utils.PortalUtils;
import com.vinplay.marketing.MARKETING_KEYWORD;
import com.vinplay.marketing.entity.UTMTracking;
import com.vinplay.marketing.service.MarketingService;
import com.vinplay.usercore.dao.impl.SecurityDaoImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.impl.SecurityServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.UserValidaton;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class QuickRegisterProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");
    private static Set<String> listIp = new HashSet<>();
    private HashMap<String, Integer> map = new HashMap<>();

    public String execute(Param<HttpServletRequest> param) {
        BaseResponseModel res;
        block10:
        {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String username = request.getParameter("un");
            String password = request.getParameter("pw");
            //   String captcha = request.getParameter("cp");
            //   String captchaId = request.getParameter("cid");
            String nickname = request.getParameter("nn");
            String campaign = request.getParameter("utm_campaign");
            String medium = request.getParameter("utm_medium");
            String source = request.getParameter("utm_source");
            String c = request.getParameter("cl");
            String codeDaiLy = request.getParameter("code_daily");
            logger.debug((Object) ("Request quickRegister: username: " + username + ", password: " + password));
            res = new BaseResponseModel(false, "1001");
            if (username == null || username.length() > 20 || username.length() < 6 || nickname == null || nickname.isEmpty()) {
                res.setErrorCode("tên đăng nhập hoặc tên hiển thị không được để trống.");
                return res.toJson();
            }
            String ip = this.getIpAddress(request);
            try {
                if (map.containsKey(ip)) {
                    Integer data = map.get(ip);
                    if (data > 10) {
                        return res.toJson();
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
            try {
                int statusGame = GameCommon.getValueInt((String) "STATUS_GAME");
                if (statusGame == StatusGames.MAINTAIN.getId() || statusGame == StatusGames.SANDBOX.getId()) {
                    res.setErrorCode("1114");
                    logger.debug((Object) ("Response login: " + res.toJson()));
                    return res.toJson();
                }
                if (username == null || password == null) break block10;
                if (true) {
                    if (UserValidaton.validateUserName((String) username)) {
                        try {
                            UserServiceImpl userService = new UserServiceImpl();
                            res.setErrorCode(userService.insertUser(username, password));
                            if (!res.getErrorCode().equals("0")) break block10;
                            res.setSuccess(true);
                            try {
                                // MARKETING
                                try {
                                    marketing(campaign, source, medium, username);
                                } catch (Exception ignored) {
                                    ignored.printStackTrace();
                                }

                                UserDaoImpl dao = new UserDaoImpl();
                                int userId = dao.getIdByUsername(username);

                                SecurityServiceImpl sercuSer = new SecurityServiceImpl();
                                sercuSer.saveLoginInfo(userId, username, "", PortalUtils.getIpAddress(request), PortalUtils.getUserAgent(request), 0, "web");
                                // Leon:
                                if (codeDaiLy != null && !codeDaiLy.isEmpty()) {
                                    sercuSer.saveUserMapToDailyInfo(userId, username, "", codeDaiLy);
                                }
                                // END - Leon:

                                try {
                                    if (c != null) {
                                        SecurityDaoImpl securDao = new SecurityDaoImpl();
                                        if (c.toLowerCase().equals("m") || c.toLowerCase().equals("man")) {
                                            securDao.updateClient(userId, "M");
                                        } else if (c.toLowerCase().equals("r")) {
                                            securDao.updateClient(userId, "R");
                                        } else if (c.toLowerCase().equals("v")) {
                                            securDao.updateClient(userId, "V");
                                        } else if (c.toLowerCase().equals("k")) {
                                            securDao.updateClient(userId, "K");
                                        } else {
                                            securDao.updateClient(userId, "X");
                                        }
                                    } else {
                                        SecurityDaoImpl securDao = new SecurityDaoImpl();
                                        securDao.updateClient(userId, "X");
                                    }
                                } catch (Exception ex) {

                                }
                                break block10;
                            } catch (Exception e) {
                                logger.debug((Object) e);
                            }
                        } catch (SQLException e2) {
                            logger.debug((Object) e2);
                        }
                        break block10;
                    }
                    res.setErrorCode("101");
                    break block10;
                }
                res.setErrorCode("115");
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        if (res.getErrorCode().equals("0")) {
            //tang hoa add len 1
            try {
                HttpServletRequest request = (HttpServletRequest) param.get();
                String ip = this.getIpAddress(request);
                if (listIp.add(ip)) {
                    if (map.containsKey(ip)) {
                        map.replace(ip, map.get(ip) + 1);
                    } else {
                        map.put(ip, 1);
                    }
                }
                if (listIp.size() > 1000) {
                    String ipPsh = listIp.iterator().next();
                    listIp.remove(ipPsh);
                    map.remove(ip);
                }
            } catch (Exception e) {
                e.getMessage();
            }

        }
        logger.debug((Object) ("Response quickRegister: " + res.toJson()));
        return res.toJson();
    }

    /**
     * @param campaign
     * @param source
     * @param medium
     * @param username
     * @throws Exception
     */
    private static void marketing(String campaign, String source, String medium, String username) throws Exception {
        if (StringUtils.isEmpty(campaign)) campaign = "UNKNOWN";
        if (StringUtils.isEmpty(source)) source = MARKETING_KEYWORD.UTM_SOURCE_NATURAL;
        if (StringUtils.isEmpty(medium)) medium = MARKETING_KEYWORD.UTM_MEDIUM_UNKNOWN;
        int utmId = 1;

        MarketingService marketingService = new MarketingService();
        UTMTracking utmTracking = marketingService.getUTMTrackingByCampaign(campaign.toUpperCase());
        if (utmTracking != null) {
            utmId = utmTracking.getId();
        }
        marketingService.createUser(username, "email", utmId, "", "");
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}

