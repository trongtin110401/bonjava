/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.List;

public class CampaignNameResponse
        extends BaseResponseModel {
    public CampaignNameResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private List<CampaignName> campaigns;

    public List<CampaignName> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<CampaignName> campaigns) {
        this.campaigns = campaigns;
    }
}


