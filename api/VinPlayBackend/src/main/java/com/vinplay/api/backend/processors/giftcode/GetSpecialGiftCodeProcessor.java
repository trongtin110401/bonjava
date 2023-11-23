package com.vinplay.api.backend.processors.giftcode;

import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.entities.SpecialGiftCodeResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.SpecialGiftCode;
import com.vinplay.vbee.common.response.BaseResponseModel;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetSpecialGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        SpecialGiftCodeResponse response = new SpecialGiftCodeResponse(false, "1001");
        String gift_code = request.getParameter("gc");
        if (gift_code != null && gift_code.equals("")) {
            try {
                GiftCodeDAO dao = new GiftCodeDAOImpl();
                List<SpecialGiftCode> result = dao.GetSpecialGiftCodesByQuery(1,1,gift_code,0,null,0);
                if (result != null && result.size() > 0) {
                    response.setGiftCode(result.get(0));
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
