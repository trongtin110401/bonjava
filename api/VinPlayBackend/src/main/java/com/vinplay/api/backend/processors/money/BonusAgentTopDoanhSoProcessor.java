/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.money;

import com.vinplay.api.backend.agent.utils.AgentUtils;
import com.vinplay.api.backend.processors.utils.BackendUtils;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class BonusAgentTopDoanhSoProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        String res = "1001";
        HttpServletRequest request = (HttpServletRequest)param.get();
        String otp = request.getParameter("otp");
        String type = request.getParameter("type");
        try {
            if (otp != null && type != null && (type.equals("1") || type.equals("0"))) {
                int code = BackendUtils.checkOTPSuperAdmin(otp, type);
                if (code == 0) {
                    AgentUtils.bonusTopDoanhSo();
                    res = "0";
                } else if (code == 3) {
                    res = "1008";
                } else if (code == 4) {
                    res = "1021";
                }
            }
        }
        catch (Exception e) {
            logger.debug((Object)e);
        }
        return res;
    }
}

