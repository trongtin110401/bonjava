package com.vinplay.khothe;

import java.util.List;

public class BuyCardResponseObj {
    private int code;
    private String message;
    private BuyCardResponseData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BuyCardResponseData getData() {
        return data;
    }

    public void setData(BuyCardResponseData data) {
        this.data = data;
    }

    public class BuyCardResponseData
    {
        private String partner_reference;
        private String server_reference;
        private String trans_time;
        private List<BuyCardResponseCard> cards;

        public String getPartner_reference() {
            return partner_reference;
        }

        public void setPartner_reference(String partner_reference) {
            this.partner_reference = partner_reference;
        }

        public String getServer_reference() {
            return server_reference;
        }

        public void setServer_reference(String server_reference) {
            this.server_reference = server_reference;
        }

        public String getTrans_time() {
            return trans_time;
        }

        public void setTrans_time(String trans_time) {
            this.trans_time = trans_time;
        }

        public List<BuyCardResponseCard> getCards() {
            return cards;
        }

        public void setCards(List<BuyCardResponseCard> cards) {
            this.cards = cards;
        }
    }

    public class BuyCardResponseCard
    {
        private String telco;
        private int amount;
        private String serial;
        private String pin;

        public String getTelco() {
            return telco;
        }

        public void setTelco(String telco) {
            this.telco = telco;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }
    }
}
