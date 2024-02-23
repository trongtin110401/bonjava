package com.vinplay.vbee.common.dto;

public class UserUsedGiftCodeAndDepositDto{

    private String nickName;

    private String code;

    private String dayUsedGiftCode;

    private long money;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDayUsedGiftCode() {
        return dayUsedGiftCode;
    }

    public void setDayUsedGiftCode(String dayUsedGiftCode) {
        this.dayUsedGiftCode = dayUsedGiftCode;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
}
