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
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogMoneyUserResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.LoginResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class GetUserDetailProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {   
        try
        {
            HttpServletRequest request = (HttpServletRequest)param.get();        
            UserServiceImpl userSer = new UserServiceImpl();
            String nickName = request.getParameter("nn");        
            GetUserDetailResponse res = new GetUserDetailResponse(500, "error");
            UserServiceImpl userService = new UserServiceImpl();
            if (nickName != null && nickName != "")
            {
                try {
                    UserModel userModel = userService.getUserByNickName(nickName);
                    if (userModel != null)
                    { 
                        HazelcastInstance client = HazelcastClientFactory.getInstance();
                        IMap userMap = client.getMap("users");
                        // thong tin chung
                        res.setNickname(nickName);
                        res.setCurrent_balance(userModel.getVin());
                        res.setRegister_time(userModel.getCreateTime().getTime());
                        if (userMap.containsKey((Object)nickName)) {                    
                            UserCacheModel userCacheModel = (UserCacheModel)userMap.get((Object)nickName);
                            if (userCacheModel != null)
                            {
                                if (userCacheModel.getLastActive() != null)
                                    res.setLast_login_time(userCacheModel.getLastActive().getTime());
                            }
                        }
                        // nap the                        
                        AgentServiceImpl service = new AgentServiceImpl();
                        List<AgentResponse> agents = service.listAgent();
                        ArrayList<String> agentNames = new ArrayList<String>();
                        if (agents != null && agents.size() > 0)
                        {
                            for (AgentResponse agent : agents)
                            {
                                agentNames.add(agent.nickName);
                            }
                        }
                        LogMoneyUserDao logService = new LogMoneyUserDaoImpl();
                        List<LogUserMoneyResponse> resultGiftCode = logService.searchAllLogMoneyUser(nickName,"GIFTCODE",false);
                        if (resultGiftCode != null && resultGiftCode.size() > 0)
                        {
                            long total_money_giftcode = 0;
                            total_money_giftcode = resultGiftCode.stream().map((trans) -> trans.moneyExchange).reduce(total_money_giftcode, (accumulator, _item) -> accumulator + _item);
                            res.setTotal_gift_code_money(total_money_giftcode);
                        }                       
                        List<LogUserMoneyResponse> resultTransfer = logService.searchAllLogMoneyUser(nickName,"TRANSFER",false);
                        boolean last_agent_transfer = true;
                        if (resultTransfer != null && resultTransfer.size() > 0)
                        {
                            LogUserMoneyResponse last_agency_transaction = null;
                            LogUserMoneyResponse last_user_transfer_transaction = null;                            
                            long total_user_transfer = 0;             
                            long total_agency_transfer = 0;
                            for (LogUserMoneyResponse trans : resultTransfer)
                            {
                                boolean matchAgent = false;
                                for (String s : agentNames)
                                {
                                    if (trans.description.contains(s))
                                    {
                                        matchAgent = true;
                                    }
                                }
                                if (matchAgent)
                                {
                                    total_agency_transfer += trans.moneyExchange;
                                    if (last_agency_transaction == null)
                                    {
                                        last_agency_transaction = trans;                                        
                                    }
                                }
                                else
                                {
                                    total_user_transfer += trans.moneyExchange;
                                    if (last_user_transfer_transaction == null) {
                                        last_user_transfer_transaction = trans;
                                    }
                                }
                            }                            
                            res.setTotal_transfer_money(0 - total_user_transfer);
                            res.setTotal_agency_transfer_money(0 - total_agency_transfer);
                            if (last_agency_transaction != null)
                            {
                                UserTransaction trans = new UserTransaction();                                
                                trans.setMoney(0 - last_agency_transaction.moneyExchange);
                                trans.setType("TRANSFER");                                
                                trans.setTime(last_agency_transaction.createdTime);
                                String name = last_agency_transaction.description.trim();
                                name = name.substring(11,name.indexOf(":"));
                                trans.setName(name);                            
                                res.setLast_agency_transaction(trans);
                            }
                            if (last_user_transfer_transaction != null)
                            {
                                UserTransaction trans = new UserTransaction();
                                trans.setMoney(0 - last_user_transfer_transaction.moneyExchange);
                                trans.setTime(last_user_transfer_transaction.createdTime);
                                String name = last_user_transfer_transaction.description.trim();
                                name = name.substring(11,name.indexOf(":"));
                                trans.setName(name);
                                trans.setType("TRANSFER");
                                res.setLast_transfer_transaction(trans);
                            }                                                  
                        }
                        List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(nickName,"RECEIVE",false);
                        if (resulReceive != null && resulReceive.size() > 0)
                        {
                            LogUserMoneyResponse last_agency_transaction = null;
                            LogUserMoneyResponse last_user_receive_transaction = null;                            
                            long total_user_receive = 0;   
                            long total_agency_receive = 0;
                            for (LogUserMoneyResponse trans : resulReceive)
                            {
                                boolean matchAgent = false;
                                for (String s : agentNames)
                                {
                                    if (trans.description.contains(s))
                                    {
                                        matchAgent = true;
                                    }
                                }
                                if (matchAgent)
                                {
                                    total_agency_receive += trans.moneyExchange;
                                    if (last_agency_transaction == null)
                                    {
                                        last_agency_transaction = trans;
                                        last_agent_transfer = false;
                                    }
                                    else
                                    {
                                        if (trans.createdTime > last_agency_transaction.createdTime)
                                        {
                                            last_agency_transaction = trans;
                                            last_agent_transfer = false;
                                        }
                                    }
                                }
                                else
                                {
                                    total_user_receive += trans.moneyExchange;
                                    if (last_user_receive_transaction == null) {
                                        last_user_receive_transaction = trans;
                                    }
                                }
                            }                            
                            res.setTotal_receive_money(total_user_receive);      
                            res.setTotal_agency_receive_money(total_agency_receive);
                            if (last_agency_transaction != null)
                            {
                                UserTransaction trans = new UserTransaction();
                                trans.setMoney(last_agency_transaction.moneyExchange);
                                trans.setTime(last_agency_transaction.createdTime);
                                if (last_agent_transfer)
                                {
                                    String name = last_user_receive_transaction.description.trim();
                                    name = name.substring(8,name.indexOf(":"));
                                    trans.setName(name);    
                                    trans.setType("RECEIVE");     
                                }
                                else
                                {
                                    String name = last_agency_transaction.description.trim();
                                    name = name.substring(8,name.indexOf(":"));
                                    trans.setName(name);
                                    trans.setType("RECEIVE");
                                }
                                res.setLast_agency_transaction(trans);
                            }
                            if (last_user_receive_transaction != null)
                            {
                                UserTransaction trans = new UserTransaction();
                                trans.setMoney(last_user_receive_transaction.moneyExchange);
                                trans.setTime(last_user_receive_transaction.createdTime);
                                String name = last_user_receive_transaction.description.trim();
                                name = name.substring(8,name.indexOf(":"));
                                trans.setName(name);
                                trans.setType("RECEIVE");
                                res.setLast_receive_transaction(trans);
                            }                                                  
                        }                               
                        
                        //res.setTotal_card_charge_money(userModel.getRechargeMoney());
                        long total_recharge_card_money = 0;//total_agency_receive - userModel.getRechargeMoney();                
                        List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(userModel.getNickname(), "CARD", false);
                        if (resultCard != null && resultCard.size() > 0) {                    
                            total_recharge_card_money = resultCard.stream().map((trans) -> trans.moneyExchange).reduce(total_recharge_card_money, (accumulator, _item) -> accumulator + _item);                
                        }
                        res.setTotal_card_charge_money(total_recharge_card_money);
                        res.setTotal_card_exchange_money(0);
                        res.setTotal_play_money(0 - logService.getTotalBetWin(nickName, "BET",null));
                        long total_money_recharge = total_recharge_card_money + res.getTotal_agency_receive_money() + res.getTotal_receive_money() + res.getTotal_gift_code_money();
                        res.setTotal_win_money(logService.getTotalBetWin(nickName, "WIN",null));                        
//                        // calculate quote
//                        long quota = 0;
//                        quota += (res.getTotal_card_charge_money() + res.getTotal_agency_receive_money() + res.getTotal_receive_money() + res.getTotal_gift_code_money());
//                        quota += res.getTotal_card_charge_money(); // nap the x3
//                        quota += res.getTotal_agency_receive_money(); // nap dai ly x3;
//                        quota += res.getTotal_receive_money() * 3; // nguoi choi khac chuyen x6;
//                        quota += res.getTotal_gift_code_money() * 3; // tien gift code * 6;                 
//                         // trừ đi số tiền chuyển đi
//                        quota -= res.getTotal_transfer_money();                        
//                        quota -= res.getTotal_agency_transfer_money();   
//                        Debug.trace("Quota:" + quota);                         
//                        //Debug.trace("Final quota :" + ((0 - res.getTotal_play_money()) + res.getTotal_win_money() - quota));
//                        // get total bet taixiu
//                        long total_bet_tx_money = 0 - logService.getTotalBetWin(nickName, "BET","TaiXiu");
//                        //long total_win_tx_money = logService.getTotalBetWin(nick_name, "WIN","TaiXiu");
//            2111            // Ban Ca
//                        long total_bet_banca_money = 0 - logService.getTotalBetWin(nickName, "BET","HamCaMap");
//                        long total_bet_slot_money = 0 - logService.getTotalBetWin(nickName, "BET","SLOT");
//                        logger.debug(res.getTotal_play_money() + ":" + total_bet_tx_money + ":" + total_bet_banca_money + ":" + total_bet_slot_money);
//                        //long finalQuota = res.getTotal_play_money() - total_bet_tx_money / 2 - total_bet_banca_money - (total_bet_slot_money * 19) / 20;
//                        long finalQuota = res.getTotal_play_money() - total_bet_banca_money - (total_bet_slot_money * 9) / 10;
// formula
                        long quota = 0;
                        //quota += (total_recharge_card_money + total_agency_receive + total_user_receive + total_giftcode_money);
                        quota += total_recharge_card_money; // nap the x3
                        quota += res.getTotal_agency_receive_money(); // nap dai ly x3;
                        quota += res.getTotal_receive_money() * 4; // nguoi choi khac chuyen x6;
                        quota += res.getTotal_gift_code_money() * 4; // tien gift code * 6;                 
                        // trừ đi số tiền chuyển đi
                        quota += res.getTotal_transfer_money();
                        quota += res.getTotal_agency_transfer_money();
                        Debug.trace("Quota:" + quota);
                        long total_bet_money = 0 - logService.getTotalBetWin(nickName, "BET", null);
                        //long total_win_money = logService.getTotalBetWin(nickName, "WIN", null);
                        // get total bet taixiu
                        //long total_bet_tx_money = 0 - logService.getTotalBetWin(nickName, "BET", "TaiXiu");
                        //long total_win_tx_money = logService.getTotalBetWin(nickName, "WIN", "TaiXiu");
                        //long taixiu_bet_win = total_bet_tx_money - total_win_tx_money;
                        //if (taixiu_bet_win < 0) {
                            //taixiu_bet_win = 0 - taixiu_bet_win;
                        //}
                        // Ban Ca                
                        long total_bet_banca_money = 0 - logService.getTotalBetWin(nickName, "BET", Games.HAM_CA_MAP.getName());
                        long total_bet_slot_money = 0 - logService.getTotalBetWin(nickName, "BET", "SLOT");
                        long finalQuota = total_bet_money - (total_bet_banca_money * 9) / 10 - (total_bet_slot_money * 9) / 10;
                        //long total_win_banca_money = logService.getTotalBetWin(nick_name, "WIN","HamCaMap");
                        //Debug.trace("Final quota :" + ((0 - total_bet_money) - quota));
                        Debug.trace("Final quota :" + finalQuota);
                        quota = finalQuota - quota;
                        res.setQuote(quota);
                        res.setCode(200);
                        res.setMessage("success");
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

