package com.vinplay.api.processors;

import bitzero.util.common.business.Debug;
import com.hazelcast.core.IMap;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.api.utils.SocialUtils;
import com.vinplay.usercore.service.CacheService;
import com.vinplay.usercore.service.OtpService;
import com.vinplay.usercore.service.impl.CacheServiceImpl;
import com.vinplay.usercore.service.impl.MarketingServiceImpl;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.UserMakertingUtil;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.UserMarketingMessage;
import com.vinplay.vbee.common.models.SocialModel;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.LoginResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class LoginAdminProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String username = request.getParameter("un");
        String password = request.getParameter("pw");
//         password = new String(Base64.getDecoder().decode(password)); // todo bỏ command khi
//        String realpass = this.getRealPass(password,username);
        //   System.out.println(realpass);
        String social = request.getParameter("s");
        String accessToken = request.getParameter("at");
        String cp = request.getParameter("cp");
        request.getHeader("user-agent");
        logger.debug((Object) ("Request login: username: " + username + ", password: " + password + ", social: " + social + ", accessToken: " + accessToken));
//        try {
//            password = VinPlayUtils.getMD5Hash(realpass);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
        if (username != null && password != null || social != null && (social.equals("fb") || social.equals("gg")) && accessToken != null) {
            LoginResponse res = new LoginResponse(false, "1001");
            try {
                int statusGame = GameCommon.getValueInt((String) "STATUS_GAME");
                if (statusGame == StatusGames.MAINTAIN.getId()) {
                    res.setErrorCode("1114");
                    logger.debug((Object) ("Response login: " + res.toJson()));
                    return res.toJson();
                }
                CacheService cacheService = new CacheServiceImpl();
                cacheService.setValue("login_noti", "true");
                UserServiceImpl userService = new UserServiceImpl();
                if (social != null && (social.equals("fb") || social.equals("gg"))) {
                    String cache = social.equals("fb") ? "cacheFacebook" : "cacheGoogle";
                    IMap socialMap = HazelcastClientFactory.getInstance().getMap(cache);
                    String socialId = SocialUtils.getSocialId((IMap<String, SocialModel>) socialMap, accessToken, social);
                    if (socialId == null) {
                        logger.debug((Object) ("Response login: " + res.toJson()));
                        return res.toJson();
                    }
                    if (socialId.isEmpty()) {
                        res.setErrorCode("1009");
                        logger.debug((Object) ("Response login: " + res.toJson()));
                        return res.toJson();
                    }
                    UserModel userModel = userService.getUserBySocialId(socialId, social);
                    if (userModel == null) {
                        if (statusGame == StatusGames.SANDBOX.getId()) {
                            res.setErrorCode("1114");
                            return res.toJson();
                        }
                        if (userModel.isBot()) {
                            res.setErrorCode("1114");
                            return res.toJson();
                        }
                        if (userService.insertUserBySocial(socialId, social)) {
                            socialMap.put((Object) socialId, (Object) new SocialModel(accessToken, socialId, new Date()));
                            String campaign = request.getParameter("utm_campaign");
                            String medium = request.getParameter("utm_medium");
                            String source = request.getParameter("utm_source");
                            if (campaign != null && medium != null && source != null) {
                                MarketingServiceImpl mktService = new MarketingServiceImpl();
                                UserMarketingMessage message = new UserMarketingMessage(username, "", 0, VinPlayUtils.getCurrentDateMarketing(), campaign, medium, source);
                                mktService.saveUserMarketing(message);
                                UserMakertingUtil.newRegisterUser((String) campaign, (String) medium, (String) source);
                            }
                            res.setErrorCode("2001");
                        }
                    } else {
                        if (statusGame == StatusGames.SANDBOX.getId() && !userModel.isCanLoginSandbox()) {
                            res.setErrorCode("1114");
                            logger.debug((Object) ("Response login: " + res.toJson()));
                            return res.toJson();
                        }
                        if (!userModel.isBanLogin()) {
                            if (userModel.getNickname() != null && !userModel.getNickname().trim().isEmpty()) {
                                if (userModel.isHasLoginSecurity() && userModel.getLoginOtp() >= 0L && userModel.getLoginOtp() <= userModel.getVinTotal()) {
                                    // send otp
                                    OtpService otpService = new OtpServiceImpl();
                                    int ret = otpService.sendVoiceOtp(userModel.getNickname(), "", true);
                                    if (ret != 0) {
                                        Debug.trace("Cannot send OTP message!");
                                        res.setErrorCode("116");
                                        return res.toJson();
                                    }
                                    res.setErrorCode("1012");
                                } else {
                                    SocialUtils.socialSuccess((IMap<String, SocialModel>) socialMap, socialId, accessToken);
                                    res = PortalUtils.loginSuccess(userModel, request);
                                }
                            } else {
                                res.setErrorCode("2001");
                            }
                        } else {
                            res.setErrorCode("1109");
                        }
                    }
                } else {
                    UserModel userModel2 = userService.getUserByUserName(username);
                    if (userModel2 != null) {
                        if (userModel2.isBot()) {
                            res.setErrorCode("1114");
                            //logger.debug((Object)("Response login: " + res.toJson()));
                            return res.toJson();
                        }
                        if (statusGame == StatusGames.SANDBOX.getId() && !userModel2.isCanLoginSandbox()) {
                            res.setErrorCode("1114");
                            logger.debug((Object) ("Response login: " + res.toJson()));
                            return res.toJson();
                        }
                        if (!userModel2.isBanLogin()) {
                            if (userModel2.getPassword().equals(password)) {
                                if (userModel2.getNickname() != null && !userModel2.getNickname().trim().isEmpty()) {
                                    if (userModel2.isHasLoginSecurity() && userModel2.getLoginOtp() >= 0L && userModel2.getLoginOtp() <= userModel2.getVinTotal()) {
                                        // send otp
                                        OtpService otpService = new OtpServiceImpl();
                                        int ret = otpService.sendVoiceOtp(userModel2.getNickname(), "", true);
                                        if (ret != 0) {
                                            Debug.trace("Cannot send OTP message!");
                                            res.setErrorCode("116");
                                            return res.toJson();
                                        }
                                        res.setErrorCode("1012");
                                    } else {
                                        res = PortalUtils.loginSuccess(userModel2, request);
                                    }
                                } else {
                                    res.setErrorCode("2001");
                                }
                            } else {
                                res.setErrorCode("1007");
                            }
                        } else {
                            res.setErrorCode("1109");
                        }
                    } else {
                        res.setErrorCode("1005");
                    }
                }
            } catch (Exception e1) {
                logger.info((Object) e1);
            }
            logger.debug((Object) ("Response login: " + res.toJson()));
            return res.toJson();
        }
        return "MISSING PARAMETTER";
    }
    }