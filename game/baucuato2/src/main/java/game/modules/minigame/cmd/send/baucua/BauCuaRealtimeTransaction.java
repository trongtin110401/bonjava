package game.modules.minigame.cmd.send.baucua;

import org.json.JSONObject;

public class BauCuaRealtimeTransaction {
    String username;
    String betStr;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBetStr() {
        return betStr;
    }

    public BauCuaRealtimeTransaction(String username, String betStr) {
        this.username = username;
        this.betStr = betStr;
    }

    public void setBetStr(String betStr) {
        this.betStr = betStr;
    }
    public JSONObject toJSONObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("nickname", (Object)this.getUsername());
            json.put("betStr", this.getBetStr());
            return json;
        }
        catch (Exception e) {
            return null;
        }
    }
}
