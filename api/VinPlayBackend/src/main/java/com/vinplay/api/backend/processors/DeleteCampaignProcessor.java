
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.CampaignNameResponse;

import javax.servlet.http.HttpServletRequest;

public class DeleteCampaignProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        int id = Integer.parseInt(request.getParameter("id"));
        CampaignNameResponse response = new CampaignNameResponse(true, "0");
        GiftCodeServiceImpl service = new GiftCodeServiceImpl();
        boolean success  = service.deleteCampaign(id);
        response.setSuccess(success);
        response.setCampaigns(service.getAllCampaign());
        return response.toJson();

    }
}

