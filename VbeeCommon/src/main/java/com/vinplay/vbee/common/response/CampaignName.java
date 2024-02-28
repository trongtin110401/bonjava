package com.vinplay.vbee.common.response;

public class CampaignName {
    private int id;
    private String campaignName;

    private long quantityActiveCode;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public long getQuantityActiveCode() {
        return quantityActiveCode;
    }

    public void setQuantityActiveCode(long quantityActiveCode) {
        this.quantityActiveCode = quantityActiveCode;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
}
