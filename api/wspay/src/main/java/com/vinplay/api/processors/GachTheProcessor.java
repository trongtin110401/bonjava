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

import com.vinplay.api.processors.response.TransInfoResponse;
import com.vinplay.dichvuthe.response.RechargeResponse;
import com.vinplay.dichvuthe.service.RechargeService;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.payment.service.impl.PaymentServiceImpl;
import com.vinplay.payment.utils.PayUtils;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.ProviderType;
import com.vinplay.vbee.common.messages.pay.ExchangeMessage;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class GachTheProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"pay");

    public String execute(Param<HttpServletRequest> param) {
        try {
            RechargeService service = new RechargeServiceImpl();
            // get params
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nickname = request.getParameter("nickname");
            String providerType = request.getParameter("provider");
            String serial = request.getParameter("serial");
            String pin = request.getParameter("pin");
            String amount = request.getParameter("amount");
            String platform = request.getParameter("platform");
            logger.info(nickname + "-" + providerType + "-" + serial + "-" + pin + "-" + amount + "-" + platform);
            //RechargeResponse response = service.rechargeByNapTienGa(nickname, ProviderType.getProviderByValue(providerType), serial, pin, amount, platform, 0);            
            RechargeResponse response = service.rechargeByECard(nickname, ProviderType.getProviderByValue(providerType), serial, pin, amount, platform, 0);            
            return response.toJson();
        } catch (Exception ex) {
             StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
            return "error:" + ex.getMessage() + sStackTrace;
        }
    }    
}

