package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.FindAllGiftCodeDto;

import javax.servlet.http.HttpServletRequest;

public class RecallGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        FindAllGiftCodeDto response = new FindAllGiftCodeDto(true, "0");

        HttpServletRequest request = param.get();
        String type = request.getParameter("type");
        String code = request.getParameter("code");

        GiftCodeServiceImpl service = new GiftCodeServiceImpl();
        service.recallGiftCode(type, code);

        return response.toJson();

    }
}

