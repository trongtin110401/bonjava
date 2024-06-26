package com.vinplay.api.processors;

import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.UserMomoInfoDto;
import com.vinplay.vbee.common.response.UserMomoInfoResponse;
import com.vinplay.vbee.common.utils.DateTimeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class UserMomoInfoProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        UserMomoInfoResponse response = new UserMomoInfoResponse(false, "1001");

        HttpServletRequest request = param.get();

        String nickName = request.getParameter("nickName");
        String phoneName = request.getParameter("phoneName");
        String phoneNumber = request.getParameter("phoneNumber");
        if (phoneName.isEmpty() || phoneNumber.isEmpty()) {
            response.setSuccess(false);
            response.setErrorCode("Thông tin không hợp lệ");
            return response.toJson();
        }
        UserMomoInfoDto userBankInfoDto = new UserMomoInfoDto(nickName, phoneName, phoneNumber, DateTimeUtils.getCurrentTime((String) "dd-MM-yyyy HH:mm:ss"));

        UserServiceImpl service = new UserServiceImpl();

        List<UserMomoInfoDto> momo = service.getListMomoByNickname(nickName);
        if (momo.size() > 5) {
            response.setSuccess(false);
            response.setErrorCode("Chỉ được tạo tối đa 5 momo");
            return response.toJson();
        }
        if (!momo.isEmpty() && !momo.get(0).getPhoneName().equals(phoneName)) {
            response.setSuccess(false);
            response.setErrorCode("Tên tài khoản không hợp lệ");
            return response.toJson();
        }
        for (UserMomoInfoDto userMomoInfoDto : momo) {
            if (phoneName.equals(userMomoInfoDto.getPhoneNumber())) {
                response.setSuccess(false);
                response.setErrorCode("Tài đã tạo");
                return response.toJson();
            }
        }
        service.saveMomoInfo(userBankInfoDto);
        momo.add(userBankInfoDto);
        response.setMomo(momo);
        response.setSuccess(true);
        response.setErrorCode("0");
        return response.toJson();
    }
}

