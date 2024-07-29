package com.vinplay.vbee.common.response;

public class CampaignName {
    private long id;
    private String campaignName;
    private long quantityActiveCode;
    private long total;
    private long unused;
    private long used;


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getUnused() {
        return unused;
    }

    public void setUnused(long unused) {
        this.unused = unused;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }
}
