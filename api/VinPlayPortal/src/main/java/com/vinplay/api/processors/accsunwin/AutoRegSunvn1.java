package com.vinplay.api.processors.accsunwin;

import com.vinplay.usercore.dao.impl.SecurityDaoImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.MarketingServiceImpl;
import com.vinplay.usercore.service.impl.SecurityServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.UserMakertingUtil;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.messages.UserMarketingMessage;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class AutoRegSunvn1 {
    private static final Logger logger = Logger.getLogger((String)"api");
    public boolean taoAcc(String username, String password, String codeDaiLy, String nickname) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        BaseResponseModel res;
        block10 : {
            String campaign ="";
            String medium ="";
            String source = "";
            String c = "";

            logger.debug((Object)("Request quickRegister: username: " + username + ", password: " + password));
            res = new BaseResponseModel(false, "1001");
            try {
                int statusGame = GameCommon.getValueInt((String)"STATUS_GAME");
                if (statusGame == StatusGames.MAINTAIN.getId() || statusGame == StatusGames.SANDBOX.getId()) {
                    res.setErrorCode("1114");
                    logger.debug((Object)("Response login: " + res.toJson()));
                    return false;
                }
                if (username == null || password == null ) break block10;
                if (true) {
                    if (true) {
                        try {
                            UserServiceImpl userService = new UserServiceImpl();
                            res.setErrorCode(userService.insertUser(username, password));
                            if (!res.getErrorCode().equals("0")) break block10;
                            res.setSuccess(true);
                            UserDaoImpl dao = new UserDaoImpl();
                            int userId = dao.getIdByUsername(username);
                            // todo : update nickname
                            String errorCode = userService.updateNickname(userId, nickname);
                            if( errorCode=="0"){
                                res.setSuccess(true);
                            } else {
                                res.setSuccess(false);
                            }
                            try {
                                if (campaign != null && medium != null && source != null) {
                                    MarketingServiceImpl mktService = new MarketingServiceImpl();
                                    UserMarketingMessage message = new UserMarketingMessage(username, "", 0, VinPlayUtils.getCurrentDateMarketing(), campaign, medium, source);
                                    mktService.saveUserMarketing(message);
                                    UserMakertingUtil.newRegisterUser((String)campaign, (String)medium, (String)source);
                                }

                                SecurityServiceImpl sercuSer = new SecurityServiceImpl();
                                sercuSer.saveLoginInfo(userId, username, "", "127.0.0.1", "all", 0, "web");
                                // Leon:
                                if (codeDaiLy != null && !codeDaiLy.isEmpty()) {
                                    sercuSer.saveUserMapToDailyInfo(userId, username, nickname, codeDaiLy);

                                }
                                // END - Leon:

                                try
                                {
                                    if (c != null)
                                    {
                                        SecurityDaoImpl securDao = new SecurityDaoImpl();
                                        if (c.toLowerCase().equals("m") || c.toLowerCase().equals("man"))
                                        {
                                            securDao.updateClient(userId, "M");
                                        }
                                        else if (c.toLowerCase().equals("r") )
                                        {
                                            securDao.updateClient(userId, "R");
                                        }
                                        else if (c.toLowerCase().equals("v") )
                                        {
                                            securDao.updateClient(userId, "V");
                                        }
                                        else if (c.toLowerCase().equals("k"))
                                        {
                                            securDao.updateClient(userId, "K");
                                        }
                                        else
                                        {
                                            securDao.updateClient(userId, "X");
                                        }
                                    }
                                    else
                                    {
                                        SecurityDaoImpl securDao = new SecurityDaoImpl();
                                        securDao.updateClient(userId, "X");
                                    }
                                }
                                catch (Exception ex)
                                {

                                }
                                break block10;
                            }
                            catch (Exception e) {
                                logger.debug((Object)e);
                            }
                        }
                        catch (SQLException e2) {
                            logger.debug((Object)e2);
                        }
                        break block10;
                    }
                    res.setErrorCode("101");
                    break block10;
                }
                res.setErrorCode("115");
            }
            catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        logger.debug((Object)("Response quickRegister: " + res.toJson()));

        return res.isSuccess();
    }
    public boolean taoTien(String nickname, String tien){
        try {
            UserService userService = new UserServiceImpl();
            long money = Long.parseLong(tien);
            BaseResponseModel baseResponseModel = userService.updateMoneyFromAdmin(nickname, (long)money, "vin",
                    "Auto", "SunWin nap",
                    "SunWin nap", 0);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
