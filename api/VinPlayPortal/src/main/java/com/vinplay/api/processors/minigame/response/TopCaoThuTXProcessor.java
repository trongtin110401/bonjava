/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.TaiXiuServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.models.minigame.TopWin
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.processors.minigame.response;

import com.vinplay.api.processors.gamebai.response.TopCaoThuResponse;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.TopCaoThu;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class TopCaoThuTXProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        TopCaoThuResponse response = new TopCaoThuResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String date = request.getParameter("date");
        String moneyType = request.getParameter("mt");
        String numStr = request.getParameter("n");
        if (date != null && numStr != null) {
            try {
                UserServiceImpl service = new UserServiceImpl();
                int num = Integer.parseInt(numStr);
                List<TopCaoThu> userList = service.getTopCaoThu(date, moneyType, num);
                response.setDate(date);
                response.setUserList(userList);
                response.setSuccess(true);
                response.setErrorCode("0");
            }
            catch (Exception e) {
                logger.debug((Object)e);
            }
        }
        return response.toJson();
    }
}

