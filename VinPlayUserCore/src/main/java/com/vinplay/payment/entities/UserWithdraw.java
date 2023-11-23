package com.vinplay.payment.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.utils.VinPlayUtils;

public class UserWithdraw {
    public String Id;
    public String Username;
    public int Amount;
    public int AmountReal;
    public String BankAccountNumber;
    public String BankAccountName;
    public String BankName;
    public String CreatedAt;
    public String UpdatedAt;
    public String Status;
    public int Version;
    public String UserProve;

    public UserWithdraw(String id, String username, int amount, String bankAccountNumber, String bankAccountName, String bankName, String createdAt, String updatedAt, String status, int version) {
        Id = id;
        Username = username;
        Amount = amount;
        BankAccountNumber = bankAccountNumber;
        BankAccountName = bankAccountName;
        BankName = bankName;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Status = status;
        Version = version;

    }

    public UserWithdraw(String username, int amount, String bankAccountNumber, String bankAccountName, String bankName) {
        this.Id = String.valueOf(VinPlayUtils.generateTransId());
        Username = username;
        Amount = amount;
        BankAccountNumber = bankAccountNumber;
        BankAccountName = bankAccountName;
        BankName = bankName;
        CreatedAt = VinPlayUtils.getCurrentDateTime();
        UpdatedAt = VinPlayUtils.getCurrentDateTime();
        this.Status = CashoutUtil.STATUS_PENDING;
        this.UserProve = "";
        this.Version = 0;
        try{
            double feeWithdraw = GameCommon.getValueDouble("RATIO_CASHOUT_BANK");
            this.AmountReal = (int)(feeWithdraw * amount);
        }catch (Exception e){
            this.AmountReal = 0;
        }

    }
    //this constructor for search
    public UserWithdraw(String Id, String username, String bankAccountNumber, String bankAccountName, String bankName, String Status) {
        this.Id = Id;
        Username = username;

        BankAccountNumber = bankAccountNumber;
        BankAccountName = bankAccountName;
        BankName = bankName;
        CreatedAt = VinPlayUtils.getCurrentDateTime();
        UpdatedAt = VinPlayUtils.getCurrentDateTime();
        this.Status = Status;



    }

    public UserWithdraw(String id, String username, int amount, int amountReal, String bankAccountNumber, String bankAccountName, String bankName, String createdAt, String updatedAt, String status, int version, String userProve) {
        Id = id;
        Username = username;
        Amount = amount;
        AmountReal = amountReal;
        BankAccountNumber = bankAccountNumber;
        BankAccountName = bankAccountName;
        BankName = bankName;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Status = status;
        Version = version;
        UserProve = userProve;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }
}
