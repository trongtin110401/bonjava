package game.modules.minigame.game79;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameHistory {

    @JsonProperty("DiceSum")
    private int diceSum;

    @JsonProperty("SessionId")
    private int sessionId;

    @JsonProperty("GID")
    private String gid;

    @JsonProperty("FirstDice")
    private int firstDice;

    @JsonProperty("SecondDice")
    private int secondDice;

    @JsonProperty("ThirdDice")
    private int thirdDice;

    @JsonProperty("BetSide")
    private String betSide;

    @JsonProperty("CreatedDate")
    private String createdDate;


    public int getDiceSum() {
        return diceSum;
    }

    public void setDiceSum(int diceSum) {
        this.diceSum = diceSum;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getFirstDice() {
        return firstDice;
    }

    public void setFirstDice(int firstDice) {
        this.firstDice = firstDice;
    }

    public int getSecondDice() {
        return secondDice;
    }

    public void setSecondDice(int secondDice) {
        this.secondDice = secondDice;
    }

    public int getThirdDice() {
        return thirdDice;
    }

    public void setThirdDice(int thirdDice) {
        this.thirdDice = thirdDice;
    }

    public String getBetSide() {
        return betSide;
    }

    public void setBetSide(String betSide) {
        this.betSide = betSide;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
