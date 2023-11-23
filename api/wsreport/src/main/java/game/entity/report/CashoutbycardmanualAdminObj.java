package game.entity.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class CashoutbycardmanualAdminObj implements Serializable {
    public String Id;
    public String Username;
    public int Amount;
    public int AmountReal;
    public String telcoId;
    public String CreatedAt;
    public String UpdatedAt;
    public String Status;
    public String Pin;
    public String Seri;
    public int Version;
    public String UserProve;
    public int quantity;

    public CashoutbycardmanualAdminObj() {
    }

    public CashoutbycardmanualAdminObj(String id, String username, int amount, int amountReal, String telcoId, String createdAt, String updatedAt, String status, String pin, String seri, int version, String userProve, int quantity) {
        Id = id;
        Username = username;
        Amount = amount;
        AmountReal = amountReal;
        this.telcoId = telcoId;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Status = status;
        Pin = pin;
        Seri = seri;
        Version = version;
        UserProve = userProve;
        this.quantity = quantity;
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

    public String getTelcoId() {
        return telcoId;
    }

    public void setTelcoId(String telcoId) {
        this.telcoId = telcoId;
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

    public String getPin() {
        return Pin;
    }

    public void setPin(String pin) {
        Pin = pin;
    }

    public String getSeri() {
        return Seri;
    }

    public void setSeri(String seri) {
        Seri = seri;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
