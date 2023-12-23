/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.entities.taixiu.TransactionTaiXiu
 *  com.vinplay.dal.service.impl.TaiXiuServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.processors.minigame;

import com.vinplay.api.processors.minigame.response.LichSuGiaoDichTXResponse;
import com.vinplay.dal.service.impl.OverUnderServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuMd5ServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

public class LichSuGiaoDichTXMD5Processor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");
    public String execute(Param<HttpServletRequest> param) {
        LichSuGiaoDichTXResponse response = new LichSuGiaoDichTXResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String username = request.getParameter("un");
        int page = Integer.parseInt(request.getParameter("p"));
        if (page < 0) {
            return response.toJson();
        }
        int moneyType = Integer.parseInt(request.getParameter("mt"));
        logger.debug("vao day"+ moneyType);
        String txType = request.getParameter("txType");
        logger.debug("vao day"+ txType);
        if(txType == null || txType.equals("1")){
            TaiXiuMd5ServiceImpl service = new TaiXiuMd5ServiceImpl();
            logger.debug("vao day ok"+ moneyType);
            try {
                logger.debug("vao day ok kkkk "+ username + " | "+page+ " | " +moneyType);
                List trans = service.getLichSuGiaoDich(username, page, moneyType);
                logger.debug("vao day"+ username + " | "+page+ " | " +moneyType);
                response.setTotalPages(10);
                response.setTransactions(trans);
                response.setSuccess(true);
                response.setErrorCode("0");
            }
            catch (SQLException e) {
                logger.debug("vao error"+ e.getMessage());
                e.printStackTrace();
            }
        }
        else{
            OverUnderServiceImpl service = new OverUnderServiceImpl();
            try {
                List trans = service.getLichSuGiaoDich(username, page, moneyType);
                response.setTotalPages(10);
                response.setTransactions(trans);
                response.setSuccess(true);
                response.setErrorCode("0");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return response.toJson();
    }
}

