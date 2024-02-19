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

public class FindAllGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        FindAllGiftCodeDto response = new FindAllGiftCodeDto(false, "1001");

        HttpServletRequest request = param.get();
        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));

        try {
            String nickName = request.getParameter("nickName");
            String code = request.getParameter("code");
            int price = 0;
            if ((request.getParameter("price") != null)){
                price = Integer.parseInt(request.getParameter("price"));
            }
            boolean active = true;
            if (request.getParameter("active") != null){
                active = Boolean.parseBoolean(request.getParameter("active"));
            }
            String type = request.getParameter("type");
            String createdTime = request.getParameter("createdTime");
            if (pageIndex < 0 || pageSize <= 0) {
                return response.toJson();
            }

            GiftCodeServiceImpl service = new GiftCodeServiceImpl();
            response = service.findAllGiftCode(nickName, code, price,active, type, createdTime, pageIndex, pageSize);

        } catch (Exception e) {
            logger.debug((Object) e);
        }

        return response.toJson();

    }
}

