package com.vinplay.api.processors;

import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;

import javax.servlet.http.HttpServletRequest;

public class GetBillingCodepayProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            return GameCommon.getValueStr((String)"BILLINGCP");
        }
        catch (KeyNotFoundException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
