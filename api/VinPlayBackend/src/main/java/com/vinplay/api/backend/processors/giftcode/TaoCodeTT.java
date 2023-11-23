package com.vinplay.api.backend.processors.giftcode;

import com.vinplay.api.backend.models.CodeTT;
import com.vinplay.api.backend.models.TanThuDAO;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class TaoCodeTT implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        try {
            String code = request.getParameter("code");
            int money = Integer.parseInt(request.getParameter("money"));
//            logger.info("Code: " + code+" money: " + money);
            String timelog = VinPlayUtils.getCurrentDateTime();
            int stop = 0;
            TanThuDAO ttdao = new TanThuDAO();
            CodeTT tt = new CodeTT(code,money, timelog, stop);
            ttdao.InsertCodeTT(tt);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return "0";
    }
}
