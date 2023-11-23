package com.vinplay.api.backend.processors.giftcode;

import com.google.gson.Gson;
import com.vinplay.api.backend.models.CodeTT;
import com.vinplay.api.backend.models.TanThuDAO;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class GetListCodeTT implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        try {
            TanThuDAO ttdao = new TanThuDAO();
            ArrayList<CodeTT> list_code = ttdao.GetCodeTT();
            Gson gson = new Gson();
            return gson.toJson(list_code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}

