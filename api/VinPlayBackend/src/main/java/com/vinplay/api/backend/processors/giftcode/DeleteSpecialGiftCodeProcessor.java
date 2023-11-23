package com.vinplay.api.backend.processors.giftcode;

import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.SpecialGiftCode;
import com.vinplay.vbee.common.response.BaseResponseModel;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class DeleteSpecialGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        String gift_code = request.getParameter("gc");
        if (gift_code != null && !gift_code.equals("")) {
            try {
                GiftCodeDAO dao = new GiftCodeDAOImpl();
                boolean result = dao.DeleteSpecialGiftcode(gift_code);
                if (result) {
                    response.setErrorCode("0");
                    response.setSuccess(true);
                }
                else
                {
                    response.setErrorCode("1");
                    response.setSuccess(false);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                logger.debug((Object)e);
            }
            return response.toJson();
        }
        return "MISSING PARAMETTER";
    }
}
