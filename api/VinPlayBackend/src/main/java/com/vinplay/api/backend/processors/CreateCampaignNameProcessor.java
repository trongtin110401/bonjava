package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.CampaignNameResponse;
import com.vinplay.vbee.common.response.LinkSocialResponse;

import javax.servlet.http.HttpServletRequest;

public class CreateCampaignNameProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String campaignName = request.getParameter("campaignName");
        CampaignNameResponse response = new CampaignNameResponse(true, "0");
        GiftCodeServiceImpl service = new GiftCodeServiceImpl();
        service.insertCampaignName(campaignName);
        response.setCampaigns(service.getAllCampaign());
        return response.toJson();
    }
}

