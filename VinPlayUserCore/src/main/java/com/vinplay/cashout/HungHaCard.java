package com.vinplay.cashout;

import com.vinplay.vbee.common.utils.VinPlayUtils;

public class HungHaCard {
    public String PinCode;
    public String Telco;
    public String Serial;
    public String Amount;
    public String CreatedAt;
    public String Trace;
    public String CardId;
    public String Username;

    public HungHaCard(String pinCode, String telco, String serial, String amount, String trace) {
        PinCode = pinCode;
        Telco = telco;
        Serial = serial;
        Amount = amount;
        Trace = trace;
        CreatedAt = VinPlayUtils.getCurrentDateTime();
        CardId = String.valueOf(VinPlayUtils.generateTransId());
    }
}
