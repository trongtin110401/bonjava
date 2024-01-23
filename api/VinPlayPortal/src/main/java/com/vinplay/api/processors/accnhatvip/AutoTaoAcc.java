package com.vinplay.api.processors.accnhatvip;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.common.HttpCommon;
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
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class AutoTaoAcc {
    private static final Logger logger = Logger.getLogger((String)"api");
    public boolean taoAcc(String username, String password, String codeDaiLy, String nickname) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        BaseResponseModel res;
        block10 : {
            String campaign ="";
            String medium ="";
            String source = "";
            String c = "";
            password = VinPlayUtils.getMD5Hash(password);
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
                                sercuSer.saveLoginInfo(userId, username, "", "10.40.112.3", "all", 0, "web");
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

    private void updateNicknameMapdaily(String username, String nickname) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("user_map_daily");
            Document doc = new Document();
            doc.append("nickName",(Object)nickname);
            col.updateOne((Bson) new Document("user_name", username), (Bson) new Document("$set", (Object) doc));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean taoNickname(String username, String password, String nickname){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://lunglinhlalenluons.store/api?c=5&un="+username+"&pw="+myHash+"&nn="+nickname+"&cp=R&cl=R&pf=web&at=")
                    .method("GET", null)
                    .addHeader("Host", "lunglinhlalenluons.store")
                    .addHeader("Sec-Ch-Ua", "\"Chromium\";v=\"97\", \" Not;A Brand\";v=\"99\"")
                    .addHeader("Sec-Ch-Ua-Mobile", "?0")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36")
                    .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
                    .addHeader("Accept", "*/*")
                    .addHeader("Sec-Fetch-Site", "same-origin")
                    .addHeader("Sec-Fetch-Mode", "cors")
                    .addHeader("Sec-Fetch-Dest", "empty")
                    .addHeader("Referer", "https://lunglinhlalenluons.store/")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "en-US,en;q=0.9")
                    .build();
            Response response = client.newCall(request).execute();
            String content = response.body().string();
            if(content.contains("\"success\":true") == true){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean taoTien(String nickname, String tien){
        try {
            UserService userService = new UserServiceImpl();
            long money = Long.parseLong(tien);
            BaseResponseModel baseResponseModel = userService.updateMoneyFromAdmin(nickname, (long)money, "vin",
                    "Auto", "Nhatvip nap",
                    "Nhatvip nap", 0);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
