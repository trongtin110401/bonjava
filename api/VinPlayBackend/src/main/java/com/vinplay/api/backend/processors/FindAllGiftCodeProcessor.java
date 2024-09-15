/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.GiftCodeServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.GiftCodeSearchResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.FindAllGiftCodeDto;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class FindAllGiftCodeProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        FindAllGiftCodeDto response = new FindAllGiftCodeDto(false, "1001");

        HttpServletRequest request = param.get();
        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));

        try {
            String nickName = request.getParameter("nickName");
            String code = request.getParameter("code");
            String price = request.getParameter("price");
            Boolean active = null;
            if (request.getParameter("active") != null && !request.getParameter("active").isEmpty()) {
                active = Boolean.parseBoolean(request.getParameter("active"));
            }
            String type = request.getParameter("type");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            if (pageIndex < 0 || pageSize <= 0) {
                return response.toJson();
            }

            GiftCodeServiceImpl service = new GiftCodeServiceImpl();
            response = service.findAllGiftCode(nickName, code, price, active, type, startTime, endTime, pageIndex, pageSize);

        } catch (Exception e) {
            logger.debug((Object) e);
        }

        return response.toJson();

    }
}

