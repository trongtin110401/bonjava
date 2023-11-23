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

import bitzero.util.common.business.Debug;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.api.entities.GetUserDetailResponse;
import com.vinplay.api.entities.GetUserInfoResponse;
import com.vinplay.api.entities.QuotaResponse;
import com.vinplay.api.entities.RechargeBankResponse;
import com.vinplay.api.entities.RechargeMomoResponse;
import com.vinplay.api.entities.UserTransaction;
import com.vinplay.api.processors.response.LogMoneyResponse;
import com.vinplay.dal.dao.LogMoneyUserDao;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.dal.service.LogMoneyUserService;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.LogMoneyUserServiceImpl;
import com.vinplay.usercore.entities.TransferMoneyResponse;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.PartnerConfig;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class TransferMoneyProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {   
        try
        {
            HttpServletRequest request = (HttpServletRequest)param.get();      
            String token = request.getParameter("t");      
            String userName = request.getParameter("un");      
            String receiver = request.getParameter("r");
            String money = request.getParameter("m");
            String desc = request.getParameter("d");
            String otp = request.getParameter("otp");
            logger.debug("TransferMoneyProcessor: " + token + ":" + userName + ":" + receiver + ":" + money + ":" + desc + ":" + otp);
            TransferMoneyResponse res = new TransferMoneyResponse((byte)500,0,0);
            UserServiceImpl userService = new UserServiceImpl();
            if (userName != null && !"".equals(userName))
            {
                try {                    
                    UserModel userModel = userService.getUserByUserName(userName);
                    // check token
                    IMap userMap = HazelcastClientFactory.getInstance().getMap("users");
                    if (!userMap.containsKey((Object) userModel.getNickname())) {
                        return res.toJson();
                    }
                    UserCacheModel userCache = (UserCacheModel) userMap.get((Object) userModel.getNickname());
                    if (userCache != null) {
                        if (!userCache.getAccessToken().equals(token)) {  
                            res.setCode((byte)400);
                            return res.toJson();
                        }
                    }
                    else
                    {
                        return res.toJson();
                    }
                    // neu la dai ly
                    // check user khong ton tai
                    if (userService.getUserByNickName(receiver) == null)
                    {
                        res.setCode((byte)404);
                        return res.toJson();
                    }
                    if (userModel.getNickname().toLowerCase().equals(receiver.toLowerCase()))
                    {
                        res.setCode((byte)404);
                        return res.toJson();
                    }
                    if (userModel.getDaily() == 0)
                    {
                        QuotaResponse checkQuota = CheckQuota(userModel.getNickname(),false);
                        if ("creator01".equals(userName))
                            checkQuota.setCode(0);
                        logger.debug("TransferMoneyProcessor chuyen tien tu user: "+"Quota resultt:" + checkQuota);
                        // kiem tra co phai chuyen cho dai ly khong 
//                        if (receiver != null && !receiver.equals(""))
//                        {
//                            try {
//                                UserModel userReceive = userService.getUserByNickName(receiver);
//                                UserModel userSend = userService.getUserByNickName(userModel.getNickname());
//                                if (userReceive != null)
//                                {
//                                    if (userReceive.getDaily() == 1 && userSend.getVin() < 50000)
//                                    {
//                                        checkQuota.setCode(1);
//                                    }
//                                }
//                            } catch (SQLException ex) {
//
//                            }
//                        }        
//                        // kiem tra user co nhap code va mua the khong
//                        if (checkQuota.getTotal_giftcode_money() > 0 && (checkQuota.getTotal_recharge_card_money() <= 0 || checkQuota.getTotal_agency_receive() <= 0))
//                        {
//                            checkQuota.setCode(1);
//                        }
                        if (checkQuota.getCode() == 0)
                        {       
                            if (otp == null || "".equals(otp))
                            {
                                res = userService.transferMoney(userModel.getNickname(), receiver, Long.parseLong(money), desc, true);
                                return "{\"code\":" + res.getCode() + ",\"message\":\"success\"}";
                            }
                            else
                            {
                                OtpServiceImpl otpSer = new OtpServiceImpl();
                                // check otp 
                                int code = otpSer.checkOtp(otp, userModel.getNickname(), "0", null);
                                if (code == 0) {
                                    res = userService.transferMoney(userModel.getNickname(), receiver, Long.parseLong(money), desc, false);     
                                    if (res.getCode() == 0)
                                    {
                                        // notify
                                        /*try {
                                            HttpClient client = HttpClientBuilder.create().build();
                                            String url = "";
                                            if ("XXENG".equals(PartnerConfig.Client))
                                                url = PartnerConfig.HostBot;
                                            else if ("R68".equals(PartnerConfig.Client))
                                                url = PartnerConfig.HostBot;
                                            else
                                                url = PartnerConfig.HostBot;
                                            HttpPost httpPost = new HttpPost(url + "/rpadmin/ozawasecret1243/3");
                                            httpPost.addHeader("Content-Type", "application/json");
                                            logger.debug("TransferMoneyProcessor chuyen tien tu user: "+" url:" + url);

                                            JSONObject obj = new JSONObject();                 
                                            String sender = userModel.getNickname();
                                            String serverName = "xxeng";
                                            if (userModel.getClient() != null && !userModel.getClient().equals(""))
                                            {                                                
                                                if (userModel.getClient().equals("M"))
                                                {
                                                    serverName = "manVip";
                                                }       
                                                else if (userModel.getClient().equals("R"))
                                                {
                                                    serverName = "r99";
                                                }
                                                else if (userModel.getClient().equals("V")) {
                                                    serverName = "Vip52";
                                                }
                                            }
                                            obj.put("sender_nick_name", sender);                                            
                                            obj.put("receiver_nick_name", receiver);
                                            obj.put("money", Long.parseLong(money));
                                            obj.put("description", desc);
                                            obj.put("serverName", serverName);
                                            long date = System.currentTimeMillis();
                                            int offset = TimeZone.getDefault().getOffset(date);
                                            obj.put("created_time", date + offset);
                                            obj.put("action","USER_TRANSFER_MONEY");
                                            if (Long.parseLong(money) >= 2000000)
                                            {
                                                obj.put("need_check", true);
                                                checkQuota = CheckQuota(userModel.getNickname(),true);
                                                obj.put("total_angecy_receive", checkQuota.getTotal_agency_receive());
                                                obj.put("total_agency_transfer", checkQuota.getTotal_agency_transfer());
                                                obj.put("total_bet_money", checkQuota.getTotal_bet_money());
                                                obj.put("total_giftcode_money", checkQuota.getTotal_giftcode_money());
                                                obj.put("total_recharge_money", checkQuota.getTotal_recharge_card_money());
                                                obj.put("total_user_receive", checkQuota.getTotal_user_receive());
                                                obj.put("total_user_transfer", checkQuota.getTotal_user_transfer());
                                                obj.put("total_win_money", checkQuota.getTotal_win_money());
                                            }
                                            StringEntity requestEntity = new StringEntity(obj.toString(), "UTF-8");
                                            httpPost.setEntity(requestEntity);

                                            // add request header
                                            HttpResponse response = client.execute(httpPost);

                                            BufferedReader rd = new BufferedReader(
                                                    new InputStreamReader(response.getEntity().getContent()));

                                            StringBuffer result = new StringBuffer();
                                            String line = "";
                                            while ((line = rd.readLine()) != null) {
                                                result.append(line);
                                            }            
                                            logger.debug((Object)("LobbyModule bot tele response: " + result));
                                        } catch (Exception ex) {
                                            logger.debug((Object)("LobbyModule error: " + ex));
                                        }*/
                                        // notify cho dai ly
                                        UserModel userReceive = userService.getUserByNickName(receiver);
                                        if (userReceive != null && (userReceive.getDaily() == 1 || userReceive.getDaily() == 2))
                                        {
                                            // notify 
                                            /*try {
                                                HttpClient httpClient = HttpClientBuilder.create().build();
                                                String url = "";
                                                if ("XXENG".equals(PartnerConfig.Client)) {
                                                    url = PartnerConfig.HostBot;
                                                } else if ("R68".equals(PartnerConfig.Client)) {
                                                    url = PartnerConfig.HostBot;
                                                } else {
                                                    url = PartnerConfig.HostBot;
                                                }
                                                HttpPost httpPost = new HttpPost(url + "/rpadmin/ozawasecret1243/10");
                                                httpPost.addHeader("Content-Type", "application/json");

                                                JSONObject objSend = new JSONObject();
                                                String sender = userModel.getNickname();
                                                String serverName = "xxeng";
                                                if (userModel.getClient() != null && !userModel.getClient().equals(""))
                                                {                                                    
                                                    if (userModel.getClient().equals("M"))
                                                    {
                                                        serverName = "manVip";
                                                    }       
                                                    else if (userModel.getClient().equals("R"))
                                                    {
                                                        serverName = "r99";
                                                    }
                                                    else if (userModel.getClient().equals("V"))
                                                    {
                                                        serverName = "Vip52";
                                                    }
                                                }
                                                objSend.put("sender_nick_name", sender);                                                
                                                objSend.put("receiver_nick_name", receiver);
                                                objSend.put("serverName", serverName);
                                                objSend.put("receiver_mobile", userReceive.getMobile());
                                                objSend.put("is_agent", true);
                                                objSend.put("money", Long.parseLong(money));
                                                objSend.put("description", desc);                                                
                                                // get current money receive
                                                long lMoney = Long.parseLong(money);                                                
                                                long currentMoneyReceive = userReceive.getCurrentMoney("vin");
                                                objSend.put("previous_money", currentMoneyReceive - lMoney);
                                                objSend.put("current_money", currentMoneyReceive);
                                                long date = System.currentTimeMillis();
                                                int offset = TimeZone.getDefault().getOffset(date);
                                                objSend.put("created_time", date + offset);

                                                StringEntity requestEntity = new StringEntity(objSend.toString(), "UTF-8");
                                                httpPost.setEntity(requestEntity);

                                                // add request header
                                                HttpResponse response = httpClient.execute(httpPost);

                                                BufferedReader rd = new BufferedReader(
                                                        new InputStreamReader(response.getEntity().getContent()));

                                                StringBuffer result = new StringBuffer();
                                                String line = "";
                                                while ((line = rd.readLine()) != null) {
                                                    result.append(line);
                                                }
                                                logger.info((Object) ("LobbyModule bot tele response: " + result));
                                            } catch (Exception ex) {
                                                logger.info((Object) ("LobbyModule error: " + ex));
                                            }*/
                                        }
                                        return "{\"code\":0,\"message\":\"success\"}";
                                    }
                                    else
                                    {
                                        return "{\"code\":" + res.getCode() + ",\"message\":\"failed\"}";
                                    }
                                }
                                else
                                {
                                    return "{\"code\":222,\"message\":\"invalid otp\"}";
                                }
                            }
                        }
                        else
                        {
                            res = new TransferMoneyResponse((byte)22,0,0);      
                            return res.toJson();
                        }
                    }
                    else if (userModel.getDaily() == 1 || userModel.getDaily() == 2)
                    {
                        logger.debug("TransferMoneyProcessor chuyen tien tu dai ly");
                        // chuyen tien dai ly den user
                        if (otp == null || "".equals(otp))
                        {
                            res = userService.transferMoney(userModel.getNickname(), receiver, Long.parseLong(money), desc, true);                            
                            return "{\"code\":" + res.getCode() + ",\"message\":\"success\"}";
                        } 
                        else
                        {
                            OtpServiceImpl otpSer = new OtpServiceImpl();
                                // check otp 
                                int code = otpSer.checkOtp(otp, userModel.getNickname(), "0", null);
                                if (code == 0) {
                                res = userService.transferMoney(userModel.getNickname(), receiver, Long.parseLong(money), desc, false);
                                if (res.getCode() == 0) {

                                    // notify
                                    /*try {
                                        HttpClient client = HttpClientBuilder.create().build();
                                        String url = "";
                                        if ("XXENG".equals(PartnerConfig.Client)) {
                                            url = PartnerConfig.HostBot;
                                        } else if ("R68".equals(PartnerConfig.Client)) {
                                            url = PartnerConfig.HostBot;
                                        } else {
                                            url = PartnerConfig.HostBot;
                                        }
                                        HttpPost httpPost = new HttpPost(url + "/rpadmin/ozawasecret1243/3");
                                        httpPost.addHeader("Content-Type", "application/json");
                                        logger.debug("TransferMoneyProcessor chuyen tien tu dai ly: "+" url:" + url);

                                        JSONObject obj = new JSONObject();                                        
                                        
                                        String sender = userModel.getNickname();
                                        String serverName = "xxeng";
                                        if (userModel.getClient() != null && !userModel.getClient().equals("")) {                                           
                                            if (userModel.getClient().equals("M"))
                                                {
                                                    serverName = "manVip";
                                                }       
                                                else if (userModel.getClient().equals("R"))
                                                {
                                                    serverName = "r99";
                                                }
                                            else if (userModel.getClient().equals("V"))
                                            {
                                                serverName = "Vip52";
                                            }
                                        }
                                        obj.put("sender_nick_name", sender);                                        
                                        obj.put("receiver_nick_name", receiver);
                                        obj.put("money", Long.parseLong(money));
                                        obj.put("description", desc);
                                        obj.put("serverName", serverName);
                                        long date = System.currentTimeMillis();
                                        int offset = TimeZone.getDefault().getOffset(date);
                                        obj.put("created_time", date + offset);
                                        obj.put("action", "AGENT_TRANSFER_MONEY");                                        
                                        StringEntity requestEntity = new StringEntity(obj.toString(), "UTF-8");
                                        httpPost.setEntity(requestEntity);

                                        // add request header
                                        HttpResponse response = client.execute(httpPost);

                                        BufferedReader rd = new BufferedReader(
                                                new InputStreamReader(response.getEntity().getContent()));

                                        StringBuffer result = new StringBuffer();
                                        String line = "";
                                        while ((line = rd.readLine()) != null) {
                                            result.append(line);
                                        }
                                        logger.debug((Object) ("LobbyModule bot tele response: " + result));
                                    } catch (Exception ex) {
                                        logger.debug((Object) ("LobbyModule error: " + ex));
                                    }                                    
                                    // notify cho dai ly   
                                    try {
                                        HttpClient httpClient = HttpClientBuilder.create().build();
                                        String url = "";
                                        if ("XXENG".equals(PartnerConfig.Client)) {
                                            url = PartnerConfig.HostBot;
                                        } else if ("R68".equals(PartnerConfig.Client)) {
                                            url = PartnerConfig.HostBot;
                                        } else {
                                            url = PartnerConfig.HostBot;
                                        }
                                        HttpPost httpPost = new HttpPost(url + "/rpadmin/ozawasecret1243/11");
                                        httpPost.addHeader("Content-Type", "application/json");

                                        JSONObject objSend = new JSONObject();                                       
                                        String sender = userModel.getNickname();
                                        String serverName = "xxeng";
                                        if (userModel.getClient() != null && !userModel.getClient().equals("")) {                                            
                                            if (userModel.getClient().equals("M"))
                                                {
                                                    serverName = "manVip";
                                                }       
                                                else if (userModel.getClient().equals("R"))
                                                {
                                                    serverName = "r99";
                                                }
                                            else if (userModel.getClient().equals("V")) {
                                                serverName = "Vip52";
                                            }
                                        }
                                        objSend.put("sender_nick_name", sender);                                        
                                        objSend.put("receiver_nick_name", receiver);
                                        objSend.put("sender_mobile", userModel.getMobile());
                                        objSend.put("is_agent", true);
                                        objSend.put("money", Long.parseLong(money));
                                        objSend.put("description", desc);
                                        objSend.put("serverName", serverName);
                                        // get current money receive                                        
                                        long currentMoneyReceive = userModel.getCurrentMoney("vin");
                                        objSend.put("previous_money", currentMoneyReceive);
                                        objSend.put("current_money", currentMoneyReceive - Long.parseLong(money));
                                        long date = System.currentTimeMillis();
                                        int offset = TimeZone.getDefault().getOffset(date);
                                        objSend.put("created_time", date + offset);

                                        StringEntity requestEntity = new StringEntity(objSend.toString(), "UTF-8");
                                        httpPost.setEntity(requestEntity);

                                        // add request header
                                        HttpResponse response = httpClient.execute(httpPost);

                                        BufferedReader rd = new BufferedReader(
                                                new InputStreamReader(response.getEntity().getContent()));

                                        StringBuffer result = new StringBuffer();
                                        String line = "";
                                        while ((line = rd.readLine()) != null) {
                                            result.append(line);
                                        }
                                        logger.info((Object) ("LobbyModule bot tele response: " + result));
                                    } catch (Exception ex) {
                                        logger.info((Object) ("LobbyModule error: " + ex));
                                    }*/
                                    return "{\"code\":0,\"message\":\"success\"}";
                                } else {
                                    return "{\"code\":" + res.getCode() + ",\"message\":\"failed\"}";
                                }
                            } else {
                                return "{\"code\":222,\"message\":\"invalid otp\"}";
                            }
                        }
                    }                        
                }
                catch (Exception ex)
                {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String sStackTrace = sw.toString(); // stack trace as a string
                    logger.info("TransferMoneyProcessor 1:"+ex.getMessage());
                    logger.info(sStackTrace);
                    return "{\"code\":500,\"message\":\"error\"}";
                }
            }            
            return "{\"code\":500,\"message\":\"error\"}";
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            logger.info("TransferMoneyProcessor 2:"+ex.getMessage());
            logger.info(sStackTrace);
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }
    
    private QuotaResponse CheckQuota(String nick_name, boolean seven_days)
    {
        QuotaResponse response = new QuotaResponse();
        try
        {
            UserModel userModel = userService.getUserByNickName(nick_name);
            if (userModel != null) {            
                // nap the                   
                LogMoneyUserDao logService = new LogMoneyUserDaoImpl();
                long total_giftcode_money = 0;
                long total_user_receive = 0;
                long total_agency_receive = 0;
                long total_user_transfer = 0;
                long total_agency_transfer = 0;
                       
                AgentServiceImpl service = new AgentServiceImpl();
                List<AgentResponse> agents = service.listAgent();
                ArrayList<String> agentNames = new ArrayList<String>();
                if (agents != null && agents.size() > 0) {
                    for (AgentResponse agent : agents) {
                        agentNames.add(agent.nickName);
                    }
                }                
                List<LogUserMoneyResponse> resultGiftCode = logService.searchAllLogMoneyUser(nick_name, "GIFTCODE",seven_days);
                if (resultGiftCode != null && resultGiftCode.size() > 0) {                    
                    total_giftcode_money = resultGiftCode.stream().map((trans) -> trans.moneyExchange).reduce(total_giftcode_money, (accumulator, _item) -> accumulator + _item);                
                }
                List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(nick_name, "RECEIVE",seven_days);
                if (resulReceive != null && resulReceive.size() > 0) {                    
                    for (LogUserMoneyResponse trans : resulReceive) {
                        boolean matchAgent = false;
                        for (String s : agentNames) {
                            if (trans.description.contains(s)) {
                                matchAgent = true;
                            }
                        }
                        if (matchAgent) {
                            total_agency_receive += trans.moneyExchange;                            
                        } else {
                            total_user_receive += trans.moneyExchange;                            
                        }
                    }                    
                }
                List<LogUserMoneyResponse> resulTransfer = logService.searchAllLogMoneyUser(nick_name, "TRANSFER",seven_days);
                if (resulTransfer != null && resulTransfer.size() > 0) {                    
                    for (LogUserMoneyResponse trans : resulTransfer) {
                        boolean matchAgent = false;
                        for (String s : agentNames) {
                            if (trans.description.contains(s)) {
                                matchAgent = true;
                            }
                        }
                        if (matchAgent) {
                            total_agency_transfer += trans.moneyExchange;                            
                        } else {
                            total_user_transfer += trans.moneyExchange;                            
                        }
                    }                    
                }
                long total_recharge_card_money = 0;//total_agency_receive - userModel.getRechargeMoney();                
                List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(nick_name, "CARD",seven_days);
                if (resultCard != null && resultCard.size() > 0) {                    
                    total_recharge_card_money = resultCard.stream().map((trans) -> trans.moneyExchange).reduce(total_recharge_card_money, (accumulator, _item) -> accumulator + _item);                
                }
//                // formula
//                long quota = 0;
//                quota += (total_recharge_card_money + total_agency_receive + total_user_receive + total_giftcode_money);
//                quota += total_recharge_card_money; // nap the x3
//                quota += total_agency_receive; // nap dai ly x3;
//                quota += total_user_receive * 3; // nguoi choi khac chuyen x6;
//                quota += total_giftcode_money * 3; // tien gift code * 6;               
//                 // trừ đi số tiền chuyển đi
//                quota -= total_agency_transfer;
//                quota -= total_user_transfer;
//                Debug.trace("Quota:" + quota);
//                long total_bet_money = 0 - logService.getTotalBetWin(nick_name, "BET", null);                
//                long total_win_money = logService.getTotalBetWin(nick_name, "WIN", null);   
//                // get total bet taixiu
//                long total_bet_tx_money = 0 - logService.getTotalBetWin(nick_name, "BET","TaiXiu");
//                //long total_win_tx_money = logService.getTotalBetWin(nick_name, "WIN","TaiXiu");
//                // Ban Ca                
//                long total_bet_banca_money = 0 - logService.getTotalBetWin(nick_name, "BET","HamCaMap");
//                long total_bet_slot_money = 0 - logService.getTotalBetWin(nick_name, "BET","SLOT");
//                long finalQuota = total_bet_money - (total_bet_slot_money * 9) / 10;
                // formula
                long quota = 0;
                //quota += (total_recharge_card_money + total_agency_receive + total_user_receive + total_giftcode_money);
                quota += total_recharge_card_money; // nap the x3
                quota += total_agency_receive; // nap dai ly x3;
                quota += total_user_receive * 4; // nguoi choi khac chuyen x6;
                quota += total_giftcode_money * 4; // tien gift code * 6;                 
                // trừ đi số tiền chuyển đi
                quota += total_agency_transfer;
                quota += total_user_transfer;
                logger.debug("Quota:" + quota);
                long total_bet_money = 0 - logService.getTotalBetWin(nick_name, "BET",null);                
                long total_win_money = logService.getTotalBetWin(nick_name, "WIN",null);
                // get total bet taixiu
                //long total_bet_tx_money = 0 - logService.getTotalBetWin(nick_name, "BET","TaiXiu");
                //long total_win_tx_money = logService.getTotalBetWin(nick_name, "WIN","TaiXiu");
                //long taixiu_bet_win = total_bet_tx_money - total_win_tx_money;
                //if (taixiu_bet_win < 0)
                //    taixiu_bet_win = 0 - taixiu_bet_win;
                // Ban Ca                
                long total_bet_banca_money = 0 - logService.getTotalBetWin(nick_name, "BET","HamCaMap");
                long total_bet_slot_money = 0 - logService.getTotalBetWin(nick_name, "BET","SLOT");
                long finalQuota = total_bet_money - (total_bet_banca_money * 9) / 10 - (total_bet_slot_money * 9) / 10;                
                logger.debug("Final quota :" + finalQuota);                
                long manual_quota = userModel.getManual_quota();
                finalQuota += manual_quota;
                //Debug.trace("Final quota :" + ((0 - total_bet_money) + total_win_money - quota));
                // check quote
                if (finalQuota > quota)
                {
                    response.setCode(0);
                }
                else
                {
                    response.setCode(1);
                }
                response.setTotal_agency_receive(total_agency_receive);
                response.setTotal_agency_transfer(total_agency_transfer);
                response.setTotal_bet_money(total_bet_money);
                response.setTotal_giftcode_money(total_giftcode_money);
                response.setTotal_recharge_card_money(total_recharge_card_money);
                response.setTotal_user_receive(total_user_receive);
                response.setTotal_user_transfer(total_user_transfer);
                response.setTotal_win_money(total_win_money);
            } 
        }
        catch (Exception ex)
        {
            response.setCode(-1);
            logger.debug((Object)("Check quote error : " + ex.getMessage()));
        }        
        return response;
    }
}

