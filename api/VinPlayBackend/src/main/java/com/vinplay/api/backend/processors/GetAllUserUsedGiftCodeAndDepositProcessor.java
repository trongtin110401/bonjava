package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.FindAllGiftCodeDto;
import com.vinplay.vbee.common.dto.UserUsedGiftCodeAndDepositDto;
import com.vinplay.vbee.common.response.UserUsedCodeAndDepositResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetAllUserUsedGiftCodeAndDepositProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        UserUsedCodeAndDepositResponse response = new UserUsedCodeAndDepositResponse(true, "0");

        HttpServletRequest request = param.get();
        String type = request.getParameter("type");
        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));

        GiftCodeServiceImpl service = new GiftCodeServiceImpl();

        List<UserUsedGiftCodeAndDepositDto> results = service.getAllUserUsedGiftCodeAndDeposit(type, pageIndex, pageSize);
        response.setResults(results);
        return response.toJson();

    }
}

