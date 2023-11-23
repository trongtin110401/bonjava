package com.vinplay.api.backend.processors.loto;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class PostFlagLotoProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    @Override
    public String execute(Param<HttpServletRequest> var1) {
        return null;
    }
}
