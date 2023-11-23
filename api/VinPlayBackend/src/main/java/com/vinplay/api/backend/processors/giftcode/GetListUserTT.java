package com.vinplay.api.backend.processors.giftcode;

import com.google.gson.Gson;
import com.vinplay.api.backend.models.FullUserCode;
import com.vinplay.api.backend.models.TanThuDAO;
import com.vinplay.api.backend.models.UserCode;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class GetListUserTT implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        try {
            String code = request.getParameter("code");
            TanThuDAO ttdao = new TanThuDAO();
            ArrayList<UserCode> list_user = ttdao.GetUserCodeTT(code);
//            ArrayList<FullUserCode> list_full_usercode = ttdao.GetFullUserCode(code);
            Gson gson = new Gson();
            return gson.toJson(list_user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}
