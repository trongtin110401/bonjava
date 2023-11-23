package com.vinplay.api.processors;

import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.dvt.CashoutByCardMessage;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class LogCashoutByCardProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    @Override
    public String execute(Param<HttpServletRequest> param) {
        try{
            HttpServletRequest request = (HttpServletRequest)param.get();
            String accessToken = request.getParameter("token");
            UserServiceImpl userSer = new UserServiceImpl();
            String nickName = request.getParameter("nickname");
            String transTime = request.getParameter("transTime");
            if(accessToken == null || accessToken.isEmpty() || nickName == null || nickName.isEmpty() || transTime == null || transTime.isEmpty()){
                return "";
            }

            if (!userSer.checkAccesstoken(nickName, accessToken)) {
                return "";
            }

            //1. find by trans time and nickname
            CashoutDao cashout = new CashoutDaoImpl();
            CashoutByCardMessage trans = cashout.FindLogByTimeAndNickname(transTime, nickName);
            if(trans != null){

                return trans.getSoftpin();
            }
            return "";
        }catch (Exception e){
            return null;
        }


    }
}
