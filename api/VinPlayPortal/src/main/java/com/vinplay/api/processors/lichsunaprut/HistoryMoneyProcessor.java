package com.vinplay.api.processors.lichsunaprut;

import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransResponse;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class    HistoryMoneyProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    @Override
    public String execute(Param<HttpServletRequest> param) {
        HistoryTransResponse res = new HistoryTransResponse(false, "1");
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String accessToken = request.getParameter("token");
            UserServiceImpl userSer = new UserServiceImpl();
            String nickName = request.getParameter("nickname");
            String pageStr = request.getParameter("p");
            String ver = request.getParameter("ver");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);

            if (accessToken == null || accessToken.isEmpty() || nickName == null || nickName.isEmpty()) {
                return res.toJson();
            }

            if (!userSer.checkAccesstoken(nickName, accessToken)) {
                return res.toJson();
            }
            if(ver == null || ver.equalsIgnoreCase("")){

                //1. find by trans time and nickname
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                res = historyTransDao.getListTransByName(nickName, page, 5);
                if (res != null) {
                    res.setSuccess(true);
                    res.setErrorCode("0");
                    return res.toJson();
                }
                return res.toJson();
            }else if(ver.equalsIgnoreCase("1")){
                //get nap
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                res = historyTransDao.getListTransNapByName(nickName, page, 5);
                if (res != null) {
                    res.setSuccess(true);
                    res.setErrorCode("0");
                    return res.toJson();
                }
                return res.toJson();
            }else if(ver.equalsIgnoreCase("2")){
                //get rut the
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                res = historyTransDao.getListTransRutTheByName(nickName, page, 5);
                if (res != null) {
                    res.setSuccess(true);
                    res.setErrorCode("0");
                    return res.toJson();
                }
                return res.toJson();
            }else if(ver.equalsIgnoreCase("3")){
                //get rut bank
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                res = historyTransDao.getListTransRutBankByName(nickName, page, 5);
                if (res != null) {
                    res.setSuccess(true);
                    res.setErrorCode("0");
                    return res.toJson();
                }
                return res.toJson();
            }else{
                //1. find by trans time and nickname
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                res = historyTransDao.getListTransByName(nickName, page, 5);
                if (res != null) {
                    res.setSuccess(true);
                    res.setErrorCode("0");
                    return res.toJson();
                }
                return res.toJson();
            }


        } catch (Exception e) {
            return res.toJson();
        }


    }
}
