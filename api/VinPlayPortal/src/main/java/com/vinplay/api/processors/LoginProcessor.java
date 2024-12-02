/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.IMap
 *  com.vinplay.usercore.service.impl.MarketingServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.usercore.utils.UserMakertingUtil
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.enums.StatusGames
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.UserMarketingMessage
 *  com.vinplay.vbee.common.models.SocialModel
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.response.LoginResponse
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import bitzero.util.common.business.Debug;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.marketing.entity.MarketingUser;
import com.vinplay.marketing.service.MarketingService;
import com.vinplay.usercore.service.CacheService;
import com.vinplay.usercore.service.OtpService;
import com.vinplay.usercore.service.impl.CacheServiceImpl;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.messages.marketing.UserAccessLogMessage;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.LoginResponse;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class LoginProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String username = request.getParameter("un");
        String password = request.getParameter("pw");
        String platform = request.getParameter("pf");
        String realpass = "";
        if (password.length() != 32) {
            try {
                password = new String(Base64.getDecoder().decode(password)); // todo bỏ command khi
                realpass = this.getRealPass(password, username);
                try {
                    password = VinPlayUtils.getMD5Hash(realpass);
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } catch (Exception ignored) {
            }
        }
        String social = request.getParameter("s");
        String accessToken = request.getParameter("at");
        String cp = request.getParameter("cp");
        request.getHeader("user-agent");
        logger.debug((Object) ("Request login: username: " + username + ", password: " + password + ", social: " + social + ", accessToken: " + accessToken));
        if (username != null && password != null || social != null && (social.equals("fb") || social.equals("gg")) && accessToken != null) {
            LoginResponse res = new LoginResponse(false, "1009");
            if (username == null || username.length() > 20 || username.length() < 6) {
                return res.toJson();
            }
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

                UserModel userModel2 = userService.getUserByUserName(username);
                if (userModel2 != null) {
                    if (userModel2.isBot()) {
                        res.setErrorCode("1114");
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
                                    // marketing
                                    marketing(res, userModel2.getNickname());
                                }
                            } else {
                                res.setErrorCode("2001");
                            }
                        } else {
                            res.setErrorCode("1007");
                        }
                    } else {
                        res.setErrorCode("Tài khoản đã bị khóa.");
                    }
                } else {
                    res.setErrorCode("1007");
                }
//                }
            } catch (Exception e1) {
                logger.info((Object) e1);
            }
            logger.debug((Object) ("Response login: " + res.toJson()));
            return res.toJson();
        }
        return "MISSING PARAMETTER";
    }

    private static void marketing(LoginResponse res, String nickname) {
        System.out.println("====> login 1");
        if (res.isSuccess()) {
            System.out.println("====> login 2");
            try {
                MarketingService marketingService = new MarketingService();
                MarketingUser marketingUser = marketingService.getUsersByName(nickname);
                System.out.println("====> login 3");
                if (marketingUser != null) {
                    System.out.println("====> login 4");
                    UserAccessLogMessage userAccessLogMessage = new UserAccessLogMessage();
                    userAccessLogMessage.setUserId(marketingUser.getId());
                    userAccessLogMessage.setAccessTime(System.currentTimeMillis());
                    RMQApi.publishMessage("queue_marketing", userAccessLogMessage, 100);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public String getRealPass(String encryptPass, String username) {
        String realpass = "";
        try {
            realpass = decrypt(encryptPass, "12345");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return realpass;
    }

    public String decrypt(String strToDecrypt, String secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] cipherData = Base64.getDecoder().decode(strToDecrypt);
        byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
        SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
        IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

        byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
        Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedData = aesCBC.doFinal(encrypted);
        String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
        return decryptedText;
    }

    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

            return result;

        } catch (DigestException e) {
            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte) 0);
        }
    }
}

