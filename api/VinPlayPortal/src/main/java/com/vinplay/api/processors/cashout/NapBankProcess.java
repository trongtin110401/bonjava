package com.vinplay.api.processors.cashout;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class NapBankProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();

            return null;
        }catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
