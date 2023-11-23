package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class TaiXiuAdminSetHuProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String huMin = request.getParameter("huMin");
        String huMax = request.getParameter("huMax");
        String hu = request.getParameter("hu");
        CacheService cacheService = new CacheServiceImpl();

        try {
            long min = Long.parseLong(huMin);
            long max = Long.parseLong(huMax);
            long huTx = Long.parseLong(hu);
            if (max > min) {
                cacheService.setValue("min_hu_tx_auto", min + "");
                cacheService.setValue("max_hu_tx_auto", max + "");
                if (huTx == 99) {
                    cacheService.setValue("hu_tx_auto", 0);
                }
            }
            return "OKE";
        } catch (Exception e) {
            e.getMessage();
        }
        return "ERROR";
    }
}
