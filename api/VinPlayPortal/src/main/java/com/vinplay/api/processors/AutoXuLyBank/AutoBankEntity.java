package com.vinplay.api.processors.AutoXuLyBank;

public class AutoBankEntity {
    private String url = "http://charging.go88.bz";
    private int port = 10007;

    private String apiSecret = "6Tj2!^8j*5";

    private String apiKey = "35481e06-8a55-4ffb-884e-d4a871417c41";

    private final String apiRegCharge = "/api/MM/RegCharge";

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
}
