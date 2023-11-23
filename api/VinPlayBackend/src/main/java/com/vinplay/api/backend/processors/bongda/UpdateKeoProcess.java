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

public class UpdateKeoProcess implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        try {
            KeoBongDaDao dao = new KeoBongDaImpl();
            String session = request.getParameter("session");
            String Id = request.getParameter("Id");
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

            String UpdatedAt = VinPlayUtils.getCurrentDateTime();
            int status = Integer.parseInt(request.getParameter("status"));


            KeoBongDa keoBongDa = dao.findKeoBongDaBySessionAndId(session, Id);
            keoBongDa.setUrl(url);
            keoBongDa.setUpdatedAt(UpdatedAt);
            keoBongDa.setStatus(status);
            keoBongDa.setBanThangDoiA(banThangDoiA);
            keoBongDa.setBanThangDoiB(banThangDoiB);
            keoBongDa.setThoiGianDa(thoiGianDa);
            keoBongDa.setTileAnDoiACaTran(tileAnDoiACaTran);
            keoBongDa.setTiLeDoiAChapCaTran(tiLeDoiAChapCaTran);
            keoBongDa.setTiLeDoiBChapCaTran(tiLeDoiBChapCaTran);
            keoBongDa.setTileDoiBChapTaiXiu(tileDoiBChapTaiXiu);
            keoBongDa.setTileDoiAChapTaiXiu(tileDoiAChapTaiXiu);
            keoBongDa.setTileDoiAChapHiep1(tileDoiAChapHiep1);
            keoBongDa.setTileDoiBChapHiep1(tileDoiBChapHiep1);
            keoBongDa.setTileDoiAChapHiep2(tileDoiAChapHiep2);
            keoBongDa.setTileDoiBChapHiep2(tileDoiBChapHiep2);
            keoBongDa.setTileAnDoiBCaTran(tileAnDoiBCaTran);
            keoBongDa.setTileAnDoiATaiXiu(tileAnDoiATaiXiu);
            keoBongDa.setTileAnDoiBTaiXiu(tileAnDoiBTaiXiu);
            keoBongDa.setTileAnDoiAHiep1(tileAnDoiAHiep1);
            keoBongDa.setTileAnDoiBHiep1(tileAnDoiBHiep1);
            keoBongDa.setTileAnDoiAHiep2(tileAnDoiAHiep2);
            keoBongDa.setTileAnDoiBHiep2(tileAnDoiBHiep2);
            keoBongDa.setDoiA(doiA);
            keoBongDa.setDoiB(doiB);
            dao.updateKeoBongDa(keoBongDa);

            CacheService cacheService = new CacheServiceImpl();
            cacheService.setValue("Bong_Da_Update_BE", 1);
            return "1";
        } catch (Exception e) {
            return "0";
        }

    }
}
