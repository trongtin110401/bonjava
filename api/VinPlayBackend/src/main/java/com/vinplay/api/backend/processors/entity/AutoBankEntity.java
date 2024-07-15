package com.vinplay.api.backend.processors.entity;

public class AutoBankEntity {
    private String url = "http://cao69.vnm.bz";
    private int port = 10007;

    private String apiSecret = "mJkf80kZOp";

    private String apiKey = "ddff2ef1-3a4d-405a-bbe4-9d421414147b";

    private final String apiRegCharge = "/api/MM/RegCharge";

    private final String apiCard = "/api/SIM/RegCharge";

    private String apiGetBankAvailable = "/api/Bank/getBankAvailable";

    private String apiGetLinkBankCode = "/api/Bank/getListBankCode";

    private String apiChargeOut = "/api/Bank/ChargeOut";

    private String apiMomoChargeOut = "/api/MM/ChargeOut";

    private String callBack = "";

    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiRegCharge() {
        return apiRegCharge;
    }

    public String getApiGetBankAvailable() {
        return apiGetBankAvailable;
    }

    public String getApiGetLinkBankCode() {
        return apiGetLinkBankCode;
    }

    public String getApiChargeOut() {
        return apiChargeOut;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getApiCard() {
        return apiCard;
    }

    public String getApiMomoChargeOut() {
        return apiMomoChargeOut;
    }

    public void setApiMomoChargeOut(String apiMomoChargeOut) {
        this.apiMomoChargeOut = apiMomoChargeOut;
    }
}
