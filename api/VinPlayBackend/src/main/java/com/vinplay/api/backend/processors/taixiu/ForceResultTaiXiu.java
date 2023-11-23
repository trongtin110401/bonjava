package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class ForceResultTaiXiu implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        String res = "0";
        HttpServletRequest request = (HttpServletRequest)param.get();
        String rs = request.getParameter("result");
        if(rs == null){
            return res;
        }
        int result = Integer.parseInt(rs);
        //String referenceId = request.getParameter("referenceId");
        if(result != 0 && result != 1){
            return res;
        }
        CacheService cacheService = new CacheServiceImpl();
        String currentReference = "";

        try{
            currentReference = cacheService.getValueStr("Tai_xiu_current_reference");
            int allowBetting = cacheService.getValueInt("allow_betting_"+currentReference);
            if(allowBetting == 0){
                return "-1";
            }
            cacheService.setValue("force_result_"+currentReference, result);
            return "1";
        }catch (Exception e){
            return res;
        }

    }
}
