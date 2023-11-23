package com.vinplay.api.processors.momo;

public class CardDuPhong {
    int status;
    String message;
    String request_id;
    int declared_value;
    int value;
    int amount;
    String code;
    String serial;
    String telco;
    int trans_id;
    String callback_sign;

    public CardDuPhong() {
    }

    public CardDuPhong(int status, String message, String request_id, int declared_value, int value, int amount, String code, String serial, String telco, int trans_id, String callback_sign) {
        this.status = status;
        this.message = message;
        this.request_id = request_id;
        this.declared_value = declared_value;
        this.value = value;
        this.amount = amount;
        this.code = code;
        this.serial = serial;
        this.telco = telco;
        this.trans_id = trans_id;
        this.callback_sign = callback_sign;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getRequest_id() {
        return request_id;
    }

    public int getDeclared_value() {
        return declared_value;
    }

    public int getValue() {
        return value;
    }

    public int getAmount() {
        return amount;
    }

    public String getCode() {
        return code;
    }

    public String getSerial() {
        return serial;
    }

    public String getTelco() {
        return telco;
    }

    public int getTrans_id() {
        return trans_id;
    }

    public String getCallback_sign() {
        return callback_sign;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public void setDeclared_value(int declared_value) {
        this.declared_value = declared_value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setTelco(String telco) {
        this.telco = telco;
    }

    public void setTrans_id(int trans_id) {
        this.trans_id = trans_id;
    }

    public void setCallback_sign(String callback_sign) {
        this.callback_sign = callback_sign;
    }

    @Override
    public String toString() {
        return "CardDuPhong{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", request_id='" + request_id + '\'' +
                ", declared_value=" + declared_value +
                ", value=" + value +
                ", amount=" + amount +
                ", code='" + code + '\'' +
                ", serial='" + serial + '\'' +
                ", telco='" + telco + '\'' +
                ", trans_id=" + trans_id +
                ", callback_sign='" + callback_sign + '\'' +
                '}';
    }
}
