package com.vinplay.api.processors;

import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.UserBankInfoDto;
import com.vinplay.vbee.common.response.UserBankInfoResponse;
import com.vinplay.vbee.common.utils.DateTimeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class UserBankInfoProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        UserBankInfoResponse response = new UserBankInfoResponse(false, "1001");

        HttpServletRequest request = param.get();

        String nickName = request.getParameter("nickName");
        String bankAccount = request.getParameter("bankAccount");
        String bankName = request.getParameter("bankName");
        String accountName = request.getParameter("accountName");
        if (nickName.isEmpty() || bankAccount.isEmpty() || bankName.isEmpty() || accountName.isEmpty()) {
            response.setSuccess(false);
            response.setErrorCode("Thông tin không hợp lệ");
            return response.toJson();
        }
        UserBankInfoDto userBankInfoDto = new UserBankInfoDto(nickName, bankAccount, bankName, accountName, DateTimeUtils.getCurrentTime((String) "yyyy-MM-dd HH:mm:ss"));

        UserServiceImpl service = new UserServiceImpl();

        List<UserBankInfoDto> banks = service.getListBankByNickname(nickName);
        if (banks.size() > 5) {
            response.setSuccess(false);
            response.setErrorCode("Chỉ được tạo tối đa 5 bank");
            return response.toJson();
        }
        if (!banks.isEmpty() && !banks.get(0).getAccountName().equals(accountName)) {
            response.setSuccess(false);
            response.setErrorCode("Tên tài khoản không hợp lệ");
            return response.toJson();
        }
        for (UserBankInfoDto bankInfoDto : banks) {
            if (bankName.equals(bankInfoDto.getBankName())) {
                response.setSuccess(false);
                response.setErrorCode("Ngân hàng đã được tạo");
                return response.toJson();
            }
        }
        service.saveBankInfo(userBankInfoDto);
        banks.add(userBankInfoDto);
        response.setBanks(banks);
        response.setSuccess(true);
        response.setErrorCode("0");
        return response.toJson();
    }
}

