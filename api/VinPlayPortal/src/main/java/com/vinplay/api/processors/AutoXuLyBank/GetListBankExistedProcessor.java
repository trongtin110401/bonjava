package com.vinplay.api.processors.AutoXuLyBank;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class GetListBankExistedProcessor implements BaseProcessor<HttpServletRequest, String> {

    @Override
    public String execute(Param<HttpServletRequest> var1) {
        AutoBankEntity autoBank = new AutoBankEntity();
        String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiGetBankAvailable()
                + "?apiKey=" + autoBank.getApiKey();
        return APIProcess.responseGetAPI(url, null);
    }

}


