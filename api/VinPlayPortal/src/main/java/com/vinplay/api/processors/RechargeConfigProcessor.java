/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.LogMoneyUserServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.LogMoneyUserResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import com.vinplay.api.entities.RechargeConfigResponse;
import com.vinplay.api.entities.Provider;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class RechargeConfigProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {   
        try
        {
            HttpServletRequest request = (HttpServletRequest)param.get();      
            RechargeConfigResponse response = new RechargeConfigResponse();
            response.code = 0;
            response.providers = new ArrayList<Provider>();
            response.providers.add(new Provider("VTT", "Viettel"));
            response.providers.add(new Provider("VNP", "Vinaphone"));
            response.providers.add(new Provider("VMS", "Mobifone"));
            response.amounts = new int[] {10,20,30,50,100,200,300,500};
            response.rate = 1;            
            return response.toJson();
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            logger.info(ex.getMessage());
            logger.info(sStackTrace);
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }    
}

