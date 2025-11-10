package com.vinplay.api.processors.AutoXuLyBank;

public class AutoBankEntity {
    public String url = "https://bankgate.coroach.xyz/bankin/";

    public String urlOrder = url + "Order.ashx";
    public String getBank = url + "info.ashx";
    public int port = 10007;

    public String partnerCode = "snap";
    public String partnerKey = "9a869182e29f47bb42eb28fb07d9ce6b";
    private String apiSecret = "mJkf80kZOp";

    private String apiKey = "ddff2ef1-3a4d-405a-bbe4-9d421414147b";

    private final String apiRegCharge = "/api/MM/RegCharge";

    private final String apiCard = "/api/SIM/RegCharge";

    private String apiGetBankAvailable = "/api/Bank/getBankAvailable";

    private String apiGetLinkBankCode = "/api/Bank/getListBankCode";

    private String apiChargeOut = "/api/Bank/ChargeOut";

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
}
