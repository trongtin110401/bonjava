package game.modules.minigame.entities;

import org.json.JSONObject;
import org.python.parser.ast.Str;

public class WinUser {
    public String username;
    public long totalWinMoney;
    public long currentMoney;

    public WinUser(String userName, long totalWinMoney, long currentMoney) {
        this.username = userName;
        this.totalWinMoney = totalWinMoney;
        this.currentMoney = currentMoney;
    }

    public long getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(long currentMoney) {
        this.currentMoney = currentMoney;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTotalWinMoney() {
        return totalWinMoney;
    }

    public void setTotalWinMoney(long totalWinMoney) {
        this.totalWinMoney = totalWinMoney;
    }

    @Override
    public String toString() {
        return "WinUser{" +
                "username='" + username + '\'' +
                ", totalWinMoney=" + totalWinMoney +
                ", currentMoney=" + currentMoney +
                '}';
    }

    public JSONObject toJSONObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("username", (Object)this.getUsername());
            json.put("totalWinMoney", this.getTotalWinMoney());
            json.put("currentMoney", this.getCurrentMoney());
            return json;
        }
        catch (Exception e) {
            return null;
        }
    }
}
