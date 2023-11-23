package game.entity.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class CashoutbybankAdminObj implements Serializable {
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

    public CashoutbybankAdminObj() {
    }

    public CashoutbybankAdminObj(String id, String username, int amount, int amountReal, String bankAccountNumber, String bankAccountName, String bankName, String createdAt, String updatedAt, String status, int version, String userProve) {
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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public int getAmountReal() {
        return AmountReal;
    }

    public void setAmountReal(int amountReal) {
        AmountReal = amountReal;
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

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public String getUserProve() {
        return UserProve;
    }

    public void setUserProve(String userProve) {
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
