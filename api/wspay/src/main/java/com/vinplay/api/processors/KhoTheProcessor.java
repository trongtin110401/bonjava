/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.payment.service.impl.PaymentServiceImpl
 *  com.vinplay.payment.utils.PayUtils
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.pay.ExchangeMessage
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import com.vinplay.dichvuthe.response.RechargeResponse;
import com.vinplay.dichvuthe.response.SoftpinResponse;
import com.vinplay.dichvuthe.service.CashOutService;
import com.vinplay.dichvuthe.service.RechargeService;
import com.vinplay.dichvuthe.service.impl.CashOutServiceImpl;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.PhoneCardType;
import com.vinplay.vbee.common.enums.ProviderType;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

public class KhoTheProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"pay");

    public String execute(Param<HttpServletRequest> param) {
        try {
            CashOutService cashOutService = new CashOutServiceImpl();
            SoftpinResponse response = cashOutService.cashOutByCardKhoThe("hello", ProviderType.getProviderById(0), PhoneCardType.getPhoneCardById(3), 2, false);
            return response.getCode() + "";
        } catch (Exception ex) {
             StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
            return "error:" + ex.getMessage() + sStackTrace;
        }
    }    
}

