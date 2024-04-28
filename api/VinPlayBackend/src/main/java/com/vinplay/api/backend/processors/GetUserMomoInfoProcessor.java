package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.UserMomoInfoDto;
import com.vinplay.vbee.common.response.UserMomoInfoResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetUserMomoInfoProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        UserMomoInfoResponse response = new UserMomoInfoResponse(false, "1001");
        HttpServletRequest request = param.get();
        String nickName = request.getParameter("nickName");
        UserServiceImpl service = new UserServiceImpl();
        List<UserMomoInfoDto> momo = service.getListMomoByNickname(nickName);
        response.setMomo(momo);
        response.setSuccess(true);
        response.setErrorCode("0");
        return response.toJson();
    }
}

