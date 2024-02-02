/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.GiftCodeServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.GiftCodeMessage
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.GiftCodeDto;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GiftCodeAdminProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        int price;
        int quantity;
        String type = request.getParameter("type");
        int length;
        int expirationDate;
        try {
            expirationDate = Integer.parseInt(request.getParameter("expirationDate"));
            length = Integer.parseInt(request.getParameter("length"));
            price = Integer.parseInt(request.getParameter("price"));
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (Exception e) {
            e.printStackTrace();
            return response.toJson();
        }
        if (price == 0 || quantity == 0) {
            logger.info("price or quantity = 0");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate newDate = currentDate.plusDays(expirationDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expirationTime = newDate.format(formatter);
        String createdDate = currentDate.format(formatter);

        try {
            GiftCodeServiceImpl service = new GiftCodeServiceImpl();
            String giftCode;
            GiftCodeDto giftCodeDto = new GiftCodeDto();
            for (int i = 0; i < quantity; ++i) {
                giftCode = VinPlayUtils.genGiftCode(length);
                giftCodeDto.setType(type);
                giftCodeDto.setPrice(price);
                giftCodeDto.setQuantity(quantity);
                giftCodeDto.setLength(length);
                giftCodeDto.setCreatedDate(createdDate);
                giftCodeDto.setExpirationTime(expirationTime);
                giftCodeDto.setCode(giftCode);
                giftCodeDto.setActive(true);
                giftCodeDto.setExpirationDate(expirationDate);
                service.saveGiftCode(giftCodeDto);
            }
            response.setErrorCode("0");
            response.setSuccess(true);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            response.setErrorCode("1001");
            response.setSuccess(false);
        }
        return response.toJson();
    }
}

