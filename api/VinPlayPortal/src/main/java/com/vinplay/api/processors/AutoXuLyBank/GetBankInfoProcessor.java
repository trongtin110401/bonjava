package com.vinplay.api.processors.AutoXuLyBank;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class GetBankInfoProcessor implements BaseProcessor<HttpServletRequest, String> {


    @Override
    public String execute(Param<HttpServletRequest> param) {

        HttpServletRequest request = param.get();
        String chargeType = request.getParameter("chargeType");
        String amount = request.getParameter("amount");
        if (chargeType.isEmpty() || amount.isEmpty()) {
            return "{\"error\":400,\"data\":" + "chargeType or amount invalid " + "}";
        }
        AutoBankEntity autoBank = new AutoBankEntity();

        String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiRegCharge()
                + "?apiKey=" + autoBank.getApiKey() + "&chargeType=" + chargeType + "&amount=" + amount + "&requestId=" + UUID.randomUUID();

        return APIProcess.responseGetAPI(url, null);
    }
}
