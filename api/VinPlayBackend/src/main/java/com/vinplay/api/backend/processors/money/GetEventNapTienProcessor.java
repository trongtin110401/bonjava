package com.vinplay.api.backend.processors.money;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.money.EventReChargeMoneyProcess;

import javax.servlet.http.HttpServletRequest;

public class GetEventNapTienProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        String res = "0";
        EventReChargeMoneyProcess response = null;
        try {
            CacheService cacheService = new CacheServiceImpl();
            String cacheNapBank = cacheService.getValueStr("nap_bank");
            String cacheNap1Pay = cacheService.getValueStr("nap_one_pay");
            String cacheNapMomo = cacheService.getValueStr("nap_momo");
            String cacheNapCard = cacheService.getValueStr("nap_card_phone");
            String cacheRutTienBank = cacheService.getValueStr("rut_tien_bank");
            String cacheRutTienCardPhone = cacheService.getValueStr("rut_tien_card_phone");
            String cacheLogin = cacheService.getValueStr("login_noti");
//            int count = cacheService.getValueInt("number");
//      todo : otp
            response = new EventReChargeMoneyProcess(true, "200", cacheNapBank, cacheNap1Pay, cacheNapMomo, cacheNapCard, cacheRutTienBank, cacheRutTienCardPhone, "false", cacheLogin);
//            cacheService.setValue("number", count++);
//            if(count > 10 ) {
                cacheService.setValue("nap_bank", "false");
                cacheService.setValue("nap_one_pay", "false");
                cacheService.setValue("nap_momo", "false");
                cacheService.setValue("nap_card_phone", "false");
                cacheService.setValue("rut_tien_bank", "false");
                cacheService.setValue("rut_tien_card_phone", "false");
                cacheService.setValue("login_noti", "false");
                cacheService.setValue("number", 0);
//            }
            return response.toJson();
        } catch (Exception e) {
            return new EventReChargeMoneyProcess(true, "200", "false", "false", "false", "false", "false", "false", "false", "false").toJson();
        }
    }
}
