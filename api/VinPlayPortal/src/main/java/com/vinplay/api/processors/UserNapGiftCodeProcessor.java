package com.vinplay.api.processors;

import com.vinplay.api.dao.ManageGiftCodeDAO;
import com.vinplay.api.entities.UserOTP;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.utils.TelegramAlert;
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
import java.util.UUID;

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
            OtherService otherService = new OtherServiceImpl();
            if (otherService.getPhoneActiveByNickname(nickName).isEmpty()) {
                response.setSuccess(false);
                response.setErrorCode("Tài khoản chưa liên kêt số điện thoại");
                return response.toJson();
            }

            GiftCodeDto giftCodeDto = service.findActiveByCode(code);
            if (giftCodeDto.getCode() == null) {
                response.setSuccess(false);
                response.setErrorCode("Gift code không hợp lệ");
                return response.toJson();
            }

            if (service.checkUserUseGiftCode(nickName, giftCodeDto.getType())) {
                response.setSuccess(false);
                response.setErrorCode("Đã nhập loại giftcode");
                return response.toJson();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date expirationDate = dateFormat.parse(giftCodeDto.getExpirationTime());
            Date currentDate = new Date();

            if (expirationDate.before(currentDate)) {
                response.setSuccess(false);
                response.setErrorCode("Gift code hết hạn");
                return response.toJson();
            }

            giftCodeDto.setNickName(nickName);
            giftCodeDto.setUsedTime(VinPlayUtils.getCurrentDateTime());
            giftCodeDto.setActive(false);

            UseGiftCodeDto userGiftCode = new UseGiftCodeDto();

            userGiftCode.setCode(code);
            userGiftCode.setActive(true);
            userGiftCode.setPrice(giftCodeDto.getPrice());
            userGiftCode.setNickname(nickName);
            userGiftCode.setTimelog(VinPlayUtils.getCurrentDateTime());
            userGiftCode.setType(giftCodeDto.getType());
            service.saveUserUseGiftCode(userGiftCode);
            service.updateGiftCode(giftCodeDto);
            HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
            historyTransDao.insertTransaction(new HistoryTransModel("Nạp Giftcode", "Hệ Thống",
                    "recharge", String.valueOf(giftCodeDto.getPrice()), "Thành công", code, nickName, "GIFT_CODE", UUID.randomUUID().toString()));
            userService.updateMoney(nickName, giftCodeDto.getPrice(), "vin", "Gift Code", "Gift Code", "Mã: " + code, 0L, null, TransType.NO_VIPPOINT);
            TelegramAlert.SendMessageDepositGiftCode(userGiftCode);
            otherService.updateCodeCallBack(code);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        response.setErrorCode("200");
        response.setSuccess(true);
        return response.toJson();
    }
}

