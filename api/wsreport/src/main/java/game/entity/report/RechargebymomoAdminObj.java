package game.entity.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

@JsonIgnoreProperties
public class RechargebymomoAdminObj implements Serializable {
    public String Id;
    public String Nickname;
    public String CreatedAt;
    public String UpdatedAt;
    public long Amount;
    public int Status;
    public String ReceivedPhoneNumber;
    public String ReceivedName;
    public String SendFromNumber;
    public String Description;
    public String UserApprove;
    public String Comment;

    public RechargebymomoAdminObj() {
    }

    public RechargebymomoAdminObj(String id, String nickname, String createdAt, String updatedAt, long amount, int status, String receivedPhoneNumber, String receivedName, String sendFromNumber, String description, String userApprove, String comment) {
        Id = id;
        Nickname = nickname;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Amount = amount;
        Status = status;
        ReceivedPhoneNumber = receivedPhoneNumber;
        ReceivedName = receivedName;
        SendFromNumber = sendFromNumber;
        Description = description;
        UserApprove = userApprove;
        Comment = comment;
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

    public String getReceivedPhoneNumber() {
        return ReceivedPhoneNumber;
    }

    public void setReceivedPhoneNumber(String receivedPhoneNumber) {
        ReceivedPhoneNumber = receivedPhoneNumber;
    }

    public String getReceivedName() {
        return ReceivedName;
    }

    public void setReceivedName(String receivedName) {
        ReceivedName = receivedName;
    }

    public String getSendFromNumber() {
        return SendFromNumber;
    }

    public void setSendFromNumber(String sendFromNumber) {
        SendFromNumber = sendFromNumber;
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

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
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
