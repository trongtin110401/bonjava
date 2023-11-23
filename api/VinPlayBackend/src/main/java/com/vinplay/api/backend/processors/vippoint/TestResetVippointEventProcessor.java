/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.usercore.service.impl.VippointServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors.vippoint;

import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.VippointServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import javax.servlet.http.HttpServletRequest;

public class TestResetVippointEventProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        String res = "1001";
        HttpServletRequest request = (HttpServletRequest)param.get();        
        String password = request.getParameter("pw");
        if (password != null && password.equals("FQmMFjF9AKUrpuen")) {            
            try {
                OtpServiceImpl otpService = new OtpServiceImpl();               
                VippointServiceImpl service = new VippointServiceImpl();
                if (service.resetEvent()) {
                    res = "0";
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}

