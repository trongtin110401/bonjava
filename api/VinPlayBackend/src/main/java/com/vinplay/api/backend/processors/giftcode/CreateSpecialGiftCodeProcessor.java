package com.vinplay.api.backend.processors.giftcode;

import com.vinplay.dal.dao.AgentDAO;
import com.vinplay.dal.dao.impl.AgentDAOImpl;
import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.GiftCodeMessage;
import com.vinplay.vbee.common.models.SpecialGiftCode;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.BaseResponseModel;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class CreateSpecialGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        String amount = request.getParameter("am");
        String gift_code = request.getParameter("gc");
        String use_count = request.getParameter("uc");
        String reuse_count = request.getParameter("rc");
        String nick_name = request.getParameter("nn");
        String type = request.getParameter("type");
        if (amount != null && !amount.equals("") && gift_code != null && !gift_code.equals("") && use_count != null
                && !use_count.equals("") && reuse_count != null && !reuse_count.equals("") && type != null && !type.equals("") ) {
            try {
                GiftCodeDAO dao = new GiftCodeDAOImpl();
                boolean exists = dao.CheckSpecialGiftCodes(gift_code);
                if (!exists) {
                    SpecialGiftCode gc = new SpecialGiftCode();
                    gc.gift_code = gift_code;
                    gc.nick_name = nick_name;
                    gc.status = 0;
                    gc.reuse_count = Integer.parseInt(reuse_count);
                    gc.use_count = Long.parseLong(use_count);
                    gc.type = Integer.parseInt(type);
                    gc.created_time = System.currentTimeMillis();
                    gc.amount = Long.parseLong(amount);
                    dao.InsertSpecialGiftcode(gc);
                    response.setErrorCode("0");
                    response.setSuccess(true);
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
