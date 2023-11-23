package game.xocdia.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Map;

public class GamePotReportModel implements Serializable {
    public byte id;
    public Map<String, Long> userBetMap;
    public Map<String, Long> betMap;
    public long totalMoneyBotBet;
    public long totalMoneyUserBet;
    public long totalMoney;

    public GamePotReportModel(byte id, Map<String, Long> userBetMap, Map<String, Long> betMap, long totalMoneyBotBet, long totalMoneyUserBet, long totalMoney) {
        this.id = id;
        this.userBetMap = userBetMap;
        this.betMap = betMap;
        this.totalMoneyBotBet = totalMoneyBotBet;
        this.totalMoneyUserBet = totalMoneyUserBet;
        this.totalMoney = totalMoney;
    }

    public GamePotReportModel() {
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public Map<String, Long> getUserBetMap() {
        return userBetMap;
    }

    public void setUserBetMap(Map<String, Long> userBetMap) {
        this.userBetMap = userBetMap;
    }

    public Map<String, Long> getBetMap() {
        return betMap;
    }

    public void setBetMap(Map<String, Long> betMap) {
        this.betMap = betMap;
    }

    public long getTotalMoneyBotBet() {
        return totalMoneyBotBet;
    }

    public void setTotalMoneyBotBet(long totalMoneyBotBet) {
        this.totalMoneyBotBet = totalMoneyBotBet;
    }

    public long getTotalMoneyUserBet() {
        return totalMoneyUserBet;
    }

    public void setTotalMoneyUserBet(long totalMoneyUserBet) {
        this.totalMoneyUserBet = totalMoneyUserBet;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"success\":false,\"errorCode\":\"1001\"}";
        }
    }

}
