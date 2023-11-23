package com.vinplay.api.processors.gamebai;

import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetListNguoiChoi implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");


    public String execute(Param<HttpServletRequest> param) {
        try {

            JSONObject response = new JSONObject(GameCommon.getValueStr("tlmn_nguoi"));

            return response.toString();
        } catch (Exception e) {
            logger.debug((Object) e);
            return "";
        }
    }
}
