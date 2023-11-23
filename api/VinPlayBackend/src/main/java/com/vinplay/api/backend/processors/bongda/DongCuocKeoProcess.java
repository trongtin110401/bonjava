package com.vinplay.api.backend.processors.bongda;

import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.dao.impl.KeoBongDaImpl;
import com.vinplay.bongda.entities.KeoBongDa;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;

public class DongCuocKeoProcess implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        try {
            KeoBongDaDao dao = new KeoBongDaImpl();

            String Id = request.getParameter("Id");
            String status = request.getParameter("status");
            int st = (status == null || status.isEmpty()) ? 1 : Integer.parseInt(status);
            //  int status = 4;
            KeoBongDa keoBongDa = dao.findKeoBongDaBySessionAndId(Id);
            keoBongDa.setStatus(st);
            dao.updateKeoBongDa(keoBongDa);
            CacheService cacheService = new CacheServiceImpl();
            cacheService.setValue("Bong_Da_Update_BE", 1);
            return "1";
        } catch (Exception e) {
            return "0";
        }

    }
}
