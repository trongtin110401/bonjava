package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.CampaignNameResponse;

import javax.servlet.http.HttpServletRequest;

public class GetAllCampaignWithoutGiftCodeInfoProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        CampaignNameResponse response = new CampaignNameResponse(true, "0");
        GiftCodeServiceImpl service = new GiftCodeServiceImpl();
        response.setCampaigns(service.getAllCampaignWithoutGiftCodeInfo());
        return response.toJson();
    }
}

