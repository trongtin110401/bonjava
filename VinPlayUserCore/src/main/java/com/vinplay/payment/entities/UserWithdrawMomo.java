package com.vinplay.payment.entities;

import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.utils.VinPlayUtils;

public class UserWithdrawMomo {
    public String Id;
    public String Nickname;
    public int Amount;
    public String PhoneNumber;
    public String CreatedAt;
    public String UpdatedAt;
    public String Status;
    public String UserApprove;
    public int AmountReal;
    public String Description;
    public String phoneName;
    public String accountName;
    //public int Fee;

    public UserWithdrawMomo(String id, String nickname, int amount, String phoneNumber, String createdAt, String updatedAt, String status, String userApprove, String description) {
        Id = id;
        Nickname = nickname;
        Amount = amount;
        PhoneNumber = phoneNumber;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Status = status;
        UserApprove = userApprove;
        Description = description;
    }

    public UserWithdrawMomo(String nickname, int amount, String phoneNumber) {
        this.Id = String.valueOf(VinPlayUtils.generateTransId());
        this.CreatedAt = VinPlayUtils.getCurrentDateTime();
        this.UpdatedAt = VinPlayUtils.getCurrentDateTime();
        Nickname = nickname;
        Amount = amount;
        PhoneNumber = phoneNumber;
        Status = CashoutUtil.STATUS_PENDING;
        UserApprove = "";
        Description = "";
        try{
            double feeWithdraw = GameCommon.getValueDouble("RATIO_CASHOUT_MOMO");
            AmountReal = (int)(feeWithdraw * amount);
            //Fee = amount - AmountReal;
            System.out.println(AmountReal);
        }catch (Exception e){
            AmountReal = 0;
        }
    }

    //this constructor for search
    public UserWithdrawMomo(String id, String nickname, String phoneNumber, String status) {
        Id = id;
        Nickname = nickname;
        PhoneNumber = phoneNumber;
        Status = status;
    }

    public UserWithdrawMomo(String id, String nickname, int amount, String phoneNumber, String createdAt, String updatedAt, String status, String userApprove, int amountReal, String description) {
        Id = id;
        Nickname = nickname;
        Amount = amount;
        PhoneNumber = phoneNumber;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Status = status;
        UserApprove = userApprove;
        AmountReal = amountReal;
        Description = description;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }
}
