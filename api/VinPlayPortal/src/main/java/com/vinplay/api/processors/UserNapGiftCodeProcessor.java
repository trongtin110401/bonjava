package com.vinplay.api.processors;

import com.vinplay.api.dao.ManageGiftCodeDAO;
import com.vinplay.api.entities.UseCode;
import com.vinplay.api.entities.UserOTP;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.GiftCodeDto;
import com.vinplay.vbee.common.dto.UseGiftCodeDto;
import com.vinplay.vbee.common.response.GiftCodeUpdateResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

import static game.modules.gameRoom.entities.GameMoneyInfo.userService;

public class UserNapGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {


    public String execute(Param<HttpServletRequest> param) {
        GiftCodeUpdateResponse response = new GiftCodeUpdateResponse(false, "1001");
        HttpServletRequest request = param.get();
        String code = request.getParameter("code");
        String nickName = request.getParameter("nickName");
        GiftCodeServiceImpl service = new GiftCodeServiceImpl();

        try {
            ManageGiftCodeDAO dao = new ManageGiftCodeDAO();
            GiftCodeDto giftCodeDto = service.findActiveByCode(code);
            if (giftCodeDto.getCode() == null) {
                return response.toJson();
            }

            if (service.checkUserUseGiftCode(nickName, giftCodeDto.getType())) {
                return response.toJson();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date expirationDate = dateFormat.parse(giftCodeDto.getExpirationTime());
            Date currentDate = new Date();

            if (expirationDate.before(currentDate)) {
                return response.toJson();
            }

            UserOTP usotp = dao.getUserActiveOTP(nickName);
            if (usotp.getActive() != 1) {
                return response.toJson();
            }
            UseGiftCodeDto userGiftCode = new UseGiftCodeDto();

            userGiftCode.setCode(code);
            userGiftCode.setActive(true);
            userGiftCode.setPrice(giftCodeDto.getPrice());
            userGiftCode.setNickname(nickName);
            userGiftCode.setTimelog(VinPlayUtils.getCurrentDateTime());
            userGiftCode.setType(giftCodeDto.getType());
            service.saveUserUseGiftCode(userGiftCode);

            userService.updateMoney(nickName, giftCodeDto.getPrice(), "vin", giftCodeDto.getType(), giftCodeDto.getType(), "M\u00e3: " + code, 0L, null, TransType.NO_VIPPOINT);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        response.setErrorCode("200");
        response.setSuccess(true);
        return response.toJson();
    }
}

