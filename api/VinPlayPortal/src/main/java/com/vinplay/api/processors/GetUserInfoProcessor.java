/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.LogMoneyUserServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.LogMoneyUserResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.api.entities.GetUserDetailResponse;
import com.vinplay.api.entities.GetUserInfoResponse;
import com.vinplay.api.entities.UserTransaction;
import com.vinplay.api.processors.response.LogMoneyResponse;
import com.vinplay.dal.dao.LogMoneyUserDao;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.dal.service.LogMoneyUserService;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.LogMoneyUserServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogMoneyUserResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.LoginResponse;
import static game.modules.gameRoom.entities.GameMoneyInfo.userService;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class GetUserInfoProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {   
        try
        {
            HttpServletRequest request = (HttpServletRequest)param.get();                    
            String userName = request.getParameter("un");      
            String token = request.getParameter("t");      
            GetUserInfoResponse res = new GetUserInfoResponse(500, "error");
            UserServiceImpl userService = new UserServiceImpl();
            if (userName != null && !"".equals(userName))
            {
                try {
                    UserModel userModel = userService.getUserByUserName(userName);
                    if (userModel != null)
                    { 
                        IMap userMap = HazelcastClientFactory.getInstance().getMap("users");
                        if (!userMap.containsKey((Object)userModel.getNickname()))
                        {
                            return res.toJson();
                        }
                        UserCacheModel userCache = (UserCacheModel) userMap.get((Object) userModel.getNickname());    
                        if (userCache != null)
                        {
                            if (userCache.getAccessToken().equals(token)) {                                
                                // thong tin chung
                                res.setNickname(userModel.getNickname());
                                res.setCurrent_balance(userModel.getVin());
                                res.setEmail(userModel.getEmail());
                                res.setIdentification(userModel.getIdentification());
                                res.setMobile(userModel.getMobile());
                                res.setVip_point(userModel.getVippoint());
                                res.setCode(200);
                                res.setMessage("success");
                            }
                            else
                            {
                                res.setCode(400);
                                res.setMessage("bad request");
                            }
                        }
                        else
                        {
                            res.setCode(400);
                            res.setMessage("bad request");
                        }
                    }
                    else
                    {
                        res.setCode(404);
                        res.setMessage("nickname not found");
                    }
                } catch (SQLException ex) {
                    res.setCode(500);
                    res.setMessage("error");
                }
            }
            else
            {
                res.setCode(400);
                res.setMessage("bad request");
            }
            return res.toJson();
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            return ex.getMessage() + "\n" + sStackTrace;
        }
    }
}

