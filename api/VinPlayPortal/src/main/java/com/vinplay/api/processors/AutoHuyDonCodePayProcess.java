package com.vinplay.api.processors;

import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoHuyDonCodePayProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String trainID = request.getParameter("trainID");
            String accessToken = request.getParameter("at");

            RechargeDao dao = new RechargeDaoImpl();
            DepositBankModel trans = dao.FindDepositBankById(trainID);
            if(trans.getStatus() != 100 && trans.getStatus() != 2){
                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = formatter.parse(trans.getCreatedAt());
                Long time1 = date.getTime();
                Long time2 = new Date().getTime();
                Long time3 = time2 - time1;
                if(time3 <= 300000 && time3 > 0){
                    long time4 = time3/1000;
                    return "{\"time\":\""+time4+"\"}";
                }else{
                    return "{\"time\":\"0\"}";
                }
            }else{
                return "{\"time\":\"error\"}";
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"time\":\"0\"}";

    }





}
