package game.entity.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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


    public UserWithdrawMomo() {
    }

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

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }
}