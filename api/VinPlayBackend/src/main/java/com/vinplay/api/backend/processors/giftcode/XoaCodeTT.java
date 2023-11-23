package com.vinplay.api.backend.processors.giftcode;

import com.vinplay.api.backend.models.CodeTT;
import com.vinplay.api.backend.models.TanThuDAO;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;

public class XoaCodeTT implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        try {
            String code = request.getParameter("code");
            TanThuDAO ttdao = new TanThuDAO();
            ttdao.DeleteCodeTT(code);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}
