package com.vinplay.api.processors.autobankOK88;

import com.google.gson.Gson;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;

import javax.servlet.http.HttpServletRequest;

public class GetBankConfigProcessor99 implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            String jsonall  = GameCommon.getValueStr((String)"OK99B");
            Gson gson = new Gson();
            bankok88Entity ba = gson.fromJson(jsonall, bankok88Entity.class);
            return gson.toJson(ba.getList_bank());
        }
        catch (KeyNotFoundException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}

