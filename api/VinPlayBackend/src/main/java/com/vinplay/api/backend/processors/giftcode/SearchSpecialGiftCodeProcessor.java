package com.vinplay.api.backend.processors.giftcode;

import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.entities.ResultSpecialGiftCodeResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.SpecialGiftCode;
import com.vinplay.vbee.common.response.BaseResponseModel;
import org.apache.log4j.Logger;
import scala.Int;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class SearchSpecialGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        ResultSpecialGiftCodeResponse response = new ResultSpecialGiftCodeResponse(false, "1001");
        String amount = request.getParameter("am");
        String gift_code = request.getParameter("gc");
        String nick_name = request.getParameter("nn");
        String type = request.getParameter("type");
        String page = request.getParameter("p");
        String page_size = request.getParameter("ps");
        try {
            long iAmount = 0;
            int iType = 0;
            int iPage = 1;
            int iPageSize = 20;
            if (amount != null && !amount.equals(""))
            {
                iAmount = Long.parseLong(amount);
            }
            if (type != null && !type.equals(""))
            {
                iType = Integer.parseInt(type);
            }
            if (page != null && !page.equals(""))
            {
                iPage = Integer.parseInt(page);
            }
            if (page_size != null && !page_size.equals(""))
            {
                iPageSize = Integer.parseInt(page_size);
            }
            GiftCodeDAO dao = new GiftCodeDAOImpl();
            List<SpecialGiftCode> gc = dao.GetSpecialGiftCodesByQuery(iPage,iPageSize,gift_code, iAmount, nick_name, iType);
            response.setGiftCodes(gc);
            response.setErrorCode("0");
            response.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug((Object) e);
        }
        return response.toJson();
    }
}
