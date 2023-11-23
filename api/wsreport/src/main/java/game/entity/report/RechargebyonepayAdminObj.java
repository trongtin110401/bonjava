package game.entity.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class RechargebyonepayAdminObj implements Serializable {
    public String Id;
    public String Nickname;
    public String CreatedAt;
    public String UpdatedAt;
    public long Amount;
    public int Status;
    public String BankBrandName;
    public String BankAccountPassword;
    public String BankAccountName;
    public String OtpNumber;
    public String Description;
    public String UserApprove;
    public int sendingStatus;

    public RechargebyonepayAdminObj() {
    }

    public RechargebyonepayAdminObj(String id, String nickname, String createdAt, String updatedAt, long amount, int status, String bankBrandName, String bankAccountPassword, String bankAccountName, String otpNumber, String description, String userApprove, int sendingStatus) {
        Id = id;
        Nickname = nickname;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Amount = amount;
        Status = status;
        BankBrandName = bankBrandName;
        BankAccountPassword = bankAccountPassword;
        BankAccountName = bankAccountName;
        OtpNumber = otpNumber;
        Description = description;
        UserApprove = userApprove;
        this.sendingStatus = sendingStatus;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public long getAmount() {
        return Amount;
    }

    public void setAmount(long amount) {
        Amount = amount;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getBankBrandName() {
        return BankBrandName;
    }

    public void setBankBrandName(String bankBrandName) {
        BankBrandName = bankBrandName;
    }

    public String getBankAccountPassword() {
        return BankAccountPassword;
    }

    public void setBankAccountPassword(String bankAccountPassword) {
        BankAccountPassword = bankAccountPassword;
    }

    public String getBankAccountName() {
        return BankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        BankAccountName = bankAccountName;
    }

    public String getOtpNumber() {
        return OtpNumber;
    }

    public void setOtpNumber(String otpNumber) {
        OtpNumber = otpNumber;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUserApprove() {
        return UserApprove;
    }

    public void setUserApprove(String userApprove) {
        UserApprove = userApprove;
    }

    public int getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(int sendingStatus) {
        this.sendingStatus = sendingStatus;
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
