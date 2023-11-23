package com.vinplay.api.backend.processors.lichsunaprut;

import com.hazelcast.core.IMap;
import com.vinplay.api.backend.processors.daily.APIelkHistoryUserTrans;
import com.vinplay.api.backend.processors.daily.BongDaELK;
import com.vinplay.api.backend.processors.daily.UserVinEntity;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.entities.HistoryTransResponse;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class HistoryMoneyByDayProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");
    private static UserService userService = new UserServiceImpl();
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HistoryTransResponse res = new HistoryTransResponse(false, "1");
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nickName = request.getParameter("nn");
            String timeStart = request.getParameter("ts");
            String timeEnd = request.getParameter("te");
            String typeSearch = request.getParameter("type");
            long currentMoney = 0L;
            currentMoney = userService.getCurrentMoneyUserCache(nickName, "vin");
            //1. find by trans time
            if(typeSearch.equals("mongo")) {
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                res = historyTransDao.getListTransByDay(nickName, timeStart, timeEnd);
            }
            if(typeSearch.equals("elk")) {
                APIelkHistoryUserTrans trainsELK = new APIelkHistoryUserTrans();
                BongDaELK bongdaELK = new BongDaELK();
                if (nickName != null && !nickName.equals("")) {
                    res.setListTrans(trainsELK.GetHistory(nickName, timeStart, timeEnd));
                    res.setListTransSend(trainsELK.GetLogByUserSend(nickName, timeStart, timeEnd));
                    res.setListTransReceive(trainsELK.GetLogByUserReceive(nickName, timeStart, timeEnd));
                    res.setListChuyenTienBongDa(bongdaELK.getListBongDaChuyenTienByNickName(timeStart, timeEnd, nickName));
                    res.setListNhanTienBongDa(bongdaELK.getListBongDaNhanTienByNickName(timeStart, timeEnd, nickName));
                } else {
                    res.setListTrans(trainsELK.GetHistoryTotal(timeStart, timeEnd));
                    res.setListTransSend(trainsELK.GetLogByUserByStatus(1, timeStart, timeEnd));
                    res.setListTransReceive(trainsELK.GetLogByUserByStatus(3, timeStart, timeEnd));
                    res.setListChuyenTienBongDa(bongdaELK.getListBongDaChuyenTien(timeStart, timeEnd));
                    res.setListNhanTienBongDa(bongdaELK.getListBongDaNhanTien(timeStart, timeEnd));
                }
            }

            res.setSuccess(true);
            res.setErrorCode("0");
            res.setCurrentMoney(currentMoney);
            res.setNickName(nickName);
            return res.toJson();
        } catch (Exception e) {
            logger.error("HistoryMoneyByDayProcessor error with: " + e.getMessage());
            return res.toJson();
        }


    }
}
