package com.vinplay.api.processors;

import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.UserBankInfoDto;
import com.vinplay.vbee.common.response.UserBankInfoResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetUserBankInfoProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        UserBankInfoResponse response = new UserBankInfoResponse(false, "1001");
        HttpServletRequest request = param.get();
        String nickName = request.getParameter("nickName");
        UserServiceImpl service = new UserServiceImpl();
        List<UserBankInfoDto> banks = service.getListBankByNickname(nickName);
        response.setBanks(banks);
        response.setSuccess(true);
        response.setErrorCode("0");
        return response.toJson();
    }
}

