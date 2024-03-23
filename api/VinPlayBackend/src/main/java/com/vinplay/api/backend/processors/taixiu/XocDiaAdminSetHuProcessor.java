package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class XocDiaAdminSetHuProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String huMin = request.getParameter("huMin");
        String huMax = request.getParameter("huMax");
        String fund = request.getParameter("hu");
        CacheService cacheService = new CacheServiceImpl();

        try {
            long min = Long.parseLong(huMin);
            long max = Long.parseLong(huMax);
            long fundXd = Long.parseLong(fund);
            if (max > min) {
                cacheService.setValue("min_fund_xd_auto", String.valueOf(min));
                cacheService.setValue("max_fund_xd_auto", String.valueOf(max));
                if (fundXd == 99) {
                    cacheService.setValue("fund_xd_auto", 0);
                }
            }
            return "OKE";
        } catch (Exception e) {
            e.getMessage();
        }
        return "ERROR";
    }
}
