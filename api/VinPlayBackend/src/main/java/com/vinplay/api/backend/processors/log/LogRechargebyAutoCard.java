package com.vinplay.api.backend.processors.log;

import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositCardResponse;
import com.vinplay.dichvuthe.entities.DepositMobileCardModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.dichvuthe.entities.DepositMomoReponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;
import scala.Int;

import javax.servlet.http.HttpServletRequest;

public class LogRechargebyAutoCard implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");
    private static final int MAX_ITEM = 15;

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        DepositCardResponse res = new DepositCardResponse(false, "1001");
        try {
            String nickname = request.getParameter("nn");
            String sendFrom = request.getParameter("sendFrom");
            String transId = request.getParameter("tid");
            // String ip = request.getParameter("ip");
            //String transNo = request.getParameter("tno");
            String status = request.getParameter("st");
            String startTime = request.getParameter("ts");
            String endTime = request.getParameter("te");
            String pages = request.getParameter("p");
            String numberMax = request.getParameter("max_item");
            int page = Integer.parseInt(pages);
            int maxItem = numberMax != null ? Integer.parseInt(numberMax) : MAX_ITEM;
            RechargeDaoImpl dao = new RechargeDaoImpl();
            DepositMobileCardModel modelSearch = new DepositMobileCardModel(transId, nickname, status, sendFrom);
            res = dao.GetListMobileCardManual(modelSearch, page, maxItem, startTime, endTime);

        } catch (Exception e) {
            logger.debug((Object) e);
        }
        return res.toJson();
    }
}
