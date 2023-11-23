package com.vinplay.api.backend.processors.taiYou88;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
public class GetListDoimainProcesor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        ListDoimainResponse res = new ListDoimainResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String timeStart = request.getParameter("ts");
        String timeEnd = request.getParameter("te");
        try {
            if (!timeStart.isEmpty() && !timeEnd.isEmpty()) {
                GetELK elkItem = new GetELK();
                res.ListDoimain = elkItem.XulyCound(timeStart, timeEnd);
                res.setErrorCode("0");
                res.setSuccess(true);
            } else {
                res.setErrorCode("1");
                res.setSuccess(false);
            }
        }
        catch (Exception e) {
            logger.debug(e);
        }
        return res.toJson();
    }
}
