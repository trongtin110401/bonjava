package game.entity.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

@JsonIgnoreProperties
public class RechargebybankAdminObj implements Serializable {
    public String Id;
    public String Nickname;
    public String CreatedAt;
    public String UpdatedAt;
    public long Amount;
    public String Status;
    public String BankBrandName;
    public String BankAccountNumber;
    public String BankAccountName;
    public String Description;
    public String UserApprove;
    public String UserSender;

    public String QRCode;

    public RechargebybankAdminObj() {
    }

    public RechargebybankAdminObj(String id, String nickname, String createdAt, String updatedAt, long amount, String status, String bankBrandName, String bankAccountNumber, String bankAccountName, String description, String userApprove, String userSender, String QRCode) {
        Id = id;
        Nickname = nickname;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Amount = amount;
        Status = status;
        BankBrandName = bankBrandName;
        BankAccountNumber = bankAccountNumber;
        BankAccountName = bankAccountName;
        Description = description;
        UserApprove = userApprove;
        UserSender = userSender;
        this.QRCode = QRCode;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getBankBrandName() {
        return BankBrandName;
    }

    public void setBankBrandName(String bankBrandName) {
        BankBrandName = bankBrandName;
    }

    public String getBankAccountNumber() {
        return BankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        BankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountName() {
        return BankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        BankAccountName = bankAccountName;
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

    public String getUserSender() {
        return UserSender;
    }

    public void setUserSender(String userSender) {
        UserSender = userSender;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
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
