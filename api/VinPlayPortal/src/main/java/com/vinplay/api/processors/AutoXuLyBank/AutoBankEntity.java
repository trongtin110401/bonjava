package com.vinplay.api.processors.AutoXuLyBank;

public class AutoBankEntity {
    private String url = "http://sv.caheoxanh.com";
    private int port = 10007;

    private String apiSecret = "l%uuGMibQX&s";

    private String apiKey = "6f52a63f-9eb0-4c8c-86ab-178488fdd488";

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
