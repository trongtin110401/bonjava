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

import com.vinplay.api.entities.RechargeBankResponse;
import com.vinplay.api.entities.RechargeMomoResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GetRechargeInfoProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {   
        try
        {
            HttpServletRequest request = (HttpServletRequest)param.get();      
            String type = request.getParameter("t");      
            if ("momo".equals(type.toLowerCase()))
            {
                RechargeMomoResponse res = new RechargeMomoResponse();
                res.setName("Nguyen Thi Linh");
                res.setPhone("0848026647");
                return res.toJson();
            }
            else if ("bank".equals(type.toLowerCase()))
            {
                RechargeBankResponse res = new RechargeBankResponse();  
                res.setAccount_name("Nguyen Thi Linh");
                res.setAccount_number("109871158224");
                res.setBank_branch("");
                res.setBank_name("Vietinbank");
                res.setMethod("Internet banking");
                return res.toJson();
            }
            else
            {
                return "{\"code\":500,\"message\":\"error\"}";
            }
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            return ex.getMessage() + "\n" + sStackTrace;
        }
    }
}

