/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.IMap
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.enums.StatusGames
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.SocialModel
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.response.LoginResponse
 *  com.vinplay.vbee.common.utils.UserValidaton
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import bitzero.util.common.business.Debug;
import com.hazelcast.core.IMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.vinplay.api.processors.cashout.GenCommentBank;
import com.vinplay.api.processors.momo.ELKAutoBankNew;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.api.utils.SocialUtils;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.SocialModel;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.LoginResponse;
import com.vinplay.vbee.common.utils.UserValidaton;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateNicknameProcesscor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String username = request.getParameter("un");
        String password = request.getParameter("pw");
        String nickname = request.getParameter("nn");
        String social = request.getParameter("s");
        String accessToken = request.getParameter("at");
        logger.debug((Object) ("Request updateNickname: username: " + username + ", password: " + password + ", social: " + social + ", accessToken: " + accessToken + ", nickname: " + nickname));
        loadChatUsers();
        if (listChatUsers.contains(nickname)) {
            LoginResponse res = new LoginResponse(false, "1010");
            return res.toJson();
        }
        if ((username != null && password != null || social != null && (social.equals("fb") || social.equals("gg")) && accessToken != null) && nickname != null) {
            LoginResponse res = new LoginResponse(false, "1001");
            try {
                int statusGame = GameCommon.getValueInt((String) "STATUS_GAME");
                if (statusGame == StatusGames.MAINTAIN.getId()) {
                    res.setErrorCode("1114");
                    logger.debug((Object) ("Response login: " + res.toJson()));
                    return res.toJson();
                }
                if (UserValidaton.validateNickname((String) nickname)) {
                    if (UserValidaton.validateNicknameSpecial((String) nickname)) {
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
                            if (userModel != null) {
                                if (statusGame == StatusGames.SANDBOX.getId() && !userModel.isCanLoginSandbox()) {
                                    res.setErrorCode("1114");
                                    logger.debug((Object) ("Response login: " + res.toJson()));
                                    return res.toJson();
                                }
                                if (!userModel.isBanLogin()) {
                                    if (userModel.getNickname() == null || userModel.getNickname().isEmpty()) {
                                        String errorCode = userService.updateNickname(userModel.getId(), nickname);
                                        if (errorCode == "0") {
                                            SocialUtils.socialSuccess((IMap<String, SocialModel>) socialMap, socialId, accessToken);
                                            userModel.setNickname(nickname);
//                                            userService.updateNickNameUser(userModel.getId(), nickname);
//                                            ELKAutoBankNew elk = new ELKAutoBankNew();
//                                            GenCommentBank gen = new GenCommentBank();
//                                            gen.GenContent2(nickname);
//                                            String commentcode = "SN"+gen.GenContent(nickname);
//                                            String nick = elk.GetNicknameByCode(commentcode);
//                                            if(nick == null){
//                                                elk.InsertCodeUserBankELK(nickname, commentcode);
//                                            }else{
//                                                if(nickname.equalsIgnoreCase(nick) == false){
//                                                    elk.InsertCodeUserBankELK(nickname, commentcode);
//                                                }
//                                            }
                                            res = PortalUtils.loginSuccess(userModel, request);

                                        } else {
                                            res.setErrorCode(errorCode);
                                        }
                                    } else {
                                        res.setErrorCode("1013");
                                    }
                                } else {
                                    res.setErrorCode("1109");
                                }
                            }
                        } else {
                            UserModel userModel2 = userService.getUserByUserName(username);
                            if (userModel2 != null) {
                                if (statusGame == StatusGames.SANDBOX.getId() && !userModel2.isCanLoginSandbox()) {
                                    res.setErrorCode("1114");
                                    logger.debug((Object) ("Response login: " + res.toJson()));
                                    return res.toJson();
                                }
                                if (!userModel2.isBanLogin()) {
                                    if (!userModel2.getUsername().toLowerCase().equals(nickname.toLowerCase())) {
                                        if (userModel2.getPassword().equals(password)) {
                                            if (userModel2.getNickname() == null || userModel2.getNickname().isEmpty()) {
                                                String errorCode2 = userService.updateNickname(userModel2.getId(), nickname);
                                                if (errorCode2 == "0") {
                                                    userModel2.setNickname(nickname);
                                                    userService.updateNickNameUser(userModel2.getId(), nickname);
                                                    res = PortalUtils.loginSuccess(userModel2, request);
                                                } else {
                                                    res.setErrorCode(errorCode2);
                                                }
                                            } else {
                                                res.setErrorCode("1013");
                                            }
                                        } else {
                                            res.setErrorCode("1007");
                                        }
                                    } else {
                                        res.setErrorCode("1011");
                                    }
                                } else {
                                    res.setErrorCode("1109");
                                }
                            } else {
                                res.setErrorCode("1005");
                            }
                        }
                        updateNicknameMongo(username,nickname);
                    } else {
                        res.setErrorCode("116");
                    }
                } else {
                    res.setErrorCode("106");
                }
            } catch (Exception e) {
                logger.debug((Object) e);
            }
            logger.debug((Object) ("Response updateNickname: " + res.toJson()));
            return res.toJson();
        }
        return "MISSING PARAMETTER";
    }

    public static List<String> listChatUsers = new ArrayList<String>();

    public void loadChatUsers() {  // load chat user
        if (listChatUsers.isEmpty() || listChatUsers.size() == 0) {
            try {
                String entry;
                BufferedReader br2 = new BufferedReader(new InputStreamReader((InputStream) new FileInputStream(VBeePath.basePath.concat("config/bots.txt")), "UTF8")); // đọc từ file bots.txt
                while ((entry = br2.readLine()) != null) {
                    this.listChatUsers.add(entry);
                }
                br2.close();
                Debug.trace("BOT CHAT USERS :" + listChatUsers.size());
            } catch (Exception e) {
                // empty catch block
            }
        }
    }

    public static void updateNicknameMongo(String username, String nickName) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> col = db.getCollection("user_map_daily");
        col.updateOne(
                Filters.eq("user_name", username),
                Updates.set("nick_name", nickName)
        );
    }
}

