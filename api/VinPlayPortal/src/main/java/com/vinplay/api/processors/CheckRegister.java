package com.vinplay.api.processors;

import bitzero.util.common.business.Debug;
import com.hazelcast.core.IMap;
import com.vinplay.api.utils.PortalUtils;
import com.vinplay.api.utils.SocialUtils;
import com.vinplay.dal.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.StatusGames;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.SocialModel;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.LoginResponse;
import com.vinplay.vbee.common.utils.UserValidaton;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CheckRegister implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String type = request.getParameter("tp");
        String data = request.getParameter("data");
        try {
//            loadChatUsers();
//            if (listChatUsers.contains(data)) {
//                LoginResponse res = new LoginResponse(false, "1010");
//                return res.toJson();
//            }
            if (!UserValidaton.validateNickname((String) data) && !UserValidaton.validateNicknameSpecial((String) data)) {
                LoginResponse res = new LoginResponse(false, "1010");
                return res.toJson();
            }
            UserDaoImpl dao = new UserDaoImpl();
            if (type.equals("1")) {
                //check nickname
                UserModel user = dao.getUserByNickName(data);
                if(user != null) {
                    LoginResponse res = new LoginResponse(false, "1010");
                    return res.toJson();
                }else {
                    LoginResponse res = new LoginResponse(true, "1100");
                    return res.toJson();
                }
            } else {
//            check username
                UserModel user = dao.getUserByUserName(data);
                if(user != null) {
                    LoginResponse res = new LoginResponse(false, "1010");
                    return res.toJson();
                }else {
                    LoginResponse res = new LoginResponse(true, "1111");
                    return res.toJson();
                }
            }
        }catch (Exception e) {
            e.getMessage();
        }
        LoginResponse res = new LoginResponse(false, "1010");
        return res.toJson();
    }
    public static List<String> listChatUsers = new ArrayList<String>();

    public void loadChatUsers() {  // load chat user
        if(listChatUsers.isEmpty() || listChatUsers.size() == 0) {
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
}