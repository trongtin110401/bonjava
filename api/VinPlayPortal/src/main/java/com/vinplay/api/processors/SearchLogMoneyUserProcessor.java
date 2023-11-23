/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinplay.api.processors;

import com.hazelcast.core.IMap;
import com.vinplay.api.processors.response.LogMoneyResponseNew;
import com.vinplay.dal.service.impl.LogMoneyUserServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class SearchLogMoneyUserProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        LogMoneyResponseNew response = new LogMoneyResponseNew(false, "1001");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String token = request.getParameter("t");
        String nickName = request.getParameter("nn");
        String userName = request.getParameter("un");
        String timestart = request.getParameter("ts");
        String timeend = request.getParameter("te");
        String moneyType = request.getParameter("mt");
        String actionName = request.getParameter("ag");
        String serviceName = request.getParameter("sn");
        // vali date input
        try {
            UserServiceImpl userService = new UserServiceImpl();
            UserModel userModel;
            if (userName != null && !"".equals(userName)) {
                userModel = userService.getUserByUserName(userName);
            } else {
                if (nickName != null && !"".equals(nickName)) {
                    userModel = userService.getUserByNickName(nickName);
                }
                else
                    return response.toJson();
            }
            if (userModel == null)
            {
                return response.toJson();
            }
            // check token
            IMap userMap = HazelcastClientFactory.getInstance().getMap("users");
            if (!userMap.containsKey((Object) userModel.getNickname())) {
                return response.toJson();
            }
            UserCacheModel userCache = (UserCacheModel) userMap.get((Object) userModel.getNickname());
            if (userCache != null) {
                if (!userCache.getAccessToken().equals(token)) {
                    response.setErrorCode("400");
                    return response.toJson();
                }
            } else {
                return response.toJson();
            }
            int page = 1;
            int like = 0;
            int totalrecord = 50;
            try {
                if (request.getParameter("p") != null)
                    page = Integer.parseInt(request.getParameter("p"));                
                if (request.getParameter("lk") != null)
                    like = Integer.parseInt(request.getParameter("lk"));
                if (request.getParameter("tr") != null)
                    totalrecord = Integer.parseInt(request.getParameter("tr"));
            } catch (Exception exception) {
                // empty catch block
            }
          
            LogMoneyUserServiceImpl service = new LogMoneyUserServiceImpl();
            try {
                List<LogUserMoneyResponse> trans = service.searchLogMoneyUser(userModel.getNickname(), null, moneyType, serviceName, actionName, timestart, timeend, page, like, totalrecord);
                if (trans != null && trans.size() > 0)
                {
                    trans.forEach((tran) -> {                        
                        try
                        {
                            if (tran.description.toLowerCase().contains("chuyển"))
                            {
                                // Chuyển đi
                                tran.sender_nick_name = tran.nickName;     
                                // sender
                                String name = tran.description.replace("Chuyển tới", "").trim();
                                name = name.substring(11,name.indexOf(":"));
                                tran.receiver_nick_name = name;
                                tran.action = "TRANSFER";
                            }
                            else
                            {
                                // Nhận
                                tran.receiver_nick_name = tran.nickName;     
                                // sender
                                String name = tran.description.replace("Nhận từ", "").trim();
                                name = name.substring(8,name.indexOf(":"));
                                tran.sender_nick_name = name;
                                tran.action = "RECEIVE";                                
                            }
                        }
                        catch (Exception ex)
                        {
                            
                        }
                    });
                }
                int totalPages = service.countsearchLogMoneyUser(userModel.getNickname(), moneyType, serviceName, actionName, timestart, timeend, like);
                response.setTotalPages(totalPages);
                response.setTransactions(trans);
                response.setSuccess(true);
                response.setErrorCode("0");
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug((Object) e);
            }
            return response.toJson();
        } catch (Exception ex) {
            logger.debug((Object) ex);
            return response.toJson();
        }
    }
}
