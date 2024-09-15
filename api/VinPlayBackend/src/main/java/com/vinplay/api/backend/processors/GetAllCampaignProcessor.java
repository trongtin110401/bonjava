package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.CampaignNameResponse;

import javax.servlet.http.HttpServletRequest;

public class GetAllCampaignProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        try {
            CampaignNameResponse response = new CampaignNameResponse(true, "0");

            GiftCodeServiceImpl service = new GiftCodeServiceImpl();
            response.setCampaigns(service.getAllCampaign());
            return response.toJson();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

