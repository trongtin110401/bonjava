package com.vinplay.api.backend.processors.bongda;

import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.dao.impl.KeoBongDaImpl;
import com.vinplay.bongda.entities.KeoBongDa;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class InsertKeoBongDa implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    @Override
    public String execute(Param<HttpServletRequest> param) {
        KeoBongDaDao dao = new KeoBongDaImpl();
        HttpServletRequest request = (HttpServletRequest) param.get();
        try {
            String session = request.getParameter("session");
            String Id = String.valueOf(VinPlayUtils.generateTransId());
            String doiA = request.getParameter("doiA");
            String doiB = request.getParameter("doiB");
            int banThangDoiA = Integer.parseInt(request.getParameter("banThangDoiA"));
            int banThangDoiB = Integer.parseInt(request.getParameter("banThangDoiB"));

            String thoiGianDa = request.getParameter("thoiGianDa");

            String tiLeDoiAChapCaTran = request.getParameter("tiLeDoiAChapCaTran");
            String tiLeDoiBChapCaTran = request.getParameter("tiLeDoiBChapCaTran");
            String tileDoiAChapTaiXiu = request.getParameter("tileDoiAChapTaiXiu");
            String tileDoiBChapTaiXiu = request.getParameter("tileDoiBChapTaiXiu");
            String tileDoiAChapHiep1 = request.getParameter("tileDoiAChapHiep1");
            String tileDoiBChapHiep1 = request.getParameter("tileDoiBChapHiep1");
            String tileDoiAChapHiep2 = request.getParameter("tileDoiAChapHiep2");
            String tileDoiBChapHiep2 = request.getParameter("tileDoiBChapHiep2");


            double tileAnDoiACaTran = Double.parseDouble(request.getParameter("tileAnDoiACaTran"));
            double tileAnDoiBCaTran = Double.parseDouble(request.getParameter("tileAnDoiBCaTran"));
            double tileAnDoiATaiXiu = Double.parseDouble(request.getParameter("tileAnDoiATaiXiu"));
            double tileAnDoiBTaiXiu = Double.parseDouble(request.getParameter("tileAnDoiBTaiXiu"));
            double tileAnDoiAHiep1 = Double.parseDouble(request.getParameter("tileAnDoiAHiep1"));
            double tileAnDoiBHiep1 = Double.parseDouble(request.getParameter("tileAnDoiBHiep1"));
            double tileAnDoiAHiep2 = Double.parseDouble(request.getParameter("tileAnDoiAHiep2"));
            double tileAnDoiBHiep2 = Double.parseDouble(request.getParameter("tileAnDoiBHiep2"));

            String url = request.getParameter("url");
            String CreatedAt = VinPlayUtils.getCurrentDateTime();

            String UpdatedAt = VinPlayUtils.getCurrentDateTime();
            int status = Integer.parseInt(request.getParameter("status"));
            KeoBongDa keoBongDa = new KeoBongDa(session, Id, doiA, doiB, banThangDoiA, banThangDoiB, thoiGianDa, tiLeDoiAChapCaTran, tiLeDoiBChapCaTran, tileDoiAChapTaiXiu, tileDoiBChapTaiXiu, tileDoiAChapHiep1, tileDoiBChapHiep1, tileDoiAChapHiep2, tileDoiBChapHiep2, tileAnDoiACaTran, tileAnDoiBCaTran, tileAnDoiATaiXiu, tileAnDoiBTaiXiu, tileAnDoiAHiep1, tileAnDoiBHiep1, tileAnDoiAHiep2, tileAnDoiBHiep2, CreatedAt, UpdatedAt, status);
            keoBongDa.setUrl(url);
            boolean result = dao.insertKeoBongDa(keoBongDa);
            CacheService cacheService = new CacheServiceImpl();
            cacheService.setValue("Bong_Da_Update_BE", 1);
        } catch (Exception e) {
            return "0";
        }

        return "1";
    }
}
