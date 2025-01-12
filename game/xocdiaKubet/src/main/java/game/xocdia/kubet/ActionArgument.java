package game.xocdia.kubet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionArgument {

    @JsonProperty("Jackpot")
    private long jackpot;

    @JsonProperty("Phrase")
    private int phrase;

    @JsonProperty("SessionID")
    private long sessionID;

    @JsonProperty("Elapsed")
    private int elapsed;

    @JsonProperty("TotalBetOdd")
    private long totalBetOdd;

    @JsonProperty("TotalBetThreeUp")
    private long totalBetThreeUp;

    @JsonProperty("TotalBetThreeDown")
    private long totalBetThreeDown;

    @JsonProperty("TotalBetEven")
    private long totalBetEven;

    @JsonProperty("TotalBetFourUp")
    private long totalBetFourUp;

    @JsonProperty("TotalBetFourDown")
    private long totalBetFourDown;

    @JsonProperty("Result")
    private Result result;

    // Getters and Setters
    public long getJackpot() {
        return jackpot;
    }

    public void setJackpot(long jackpot) {
        this.jackpot = jackpot;
    }

    public int getPhrase() {
        return phrase;
    }

    public void setPhrase(int phrase) {
        this.phrase = phrase;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public int getElapsed() {
        return elapsed;
    }

    public void setElapsed(int elapsed) {
        this.elapsed = elapsed;
    }

    public long getTotalBetOdd() {
        return totalBetOdd;
    }

    public void setTotalBetOdd(long totalBetOdd) {
        this.totalBetOdd = totalBetOdd;
    }

    public long getTotalBetThreeUp() {
        return totalBetThreeUp;
    }

    public void setTotalBetThreeUp(long totalBetThreeUp) {
        this.totalBetThreeUp = totalBetThreeUp;
    }

    public long getTotalBetThreeDown() {
        return totalBetThreeDown;
    }

    public void setTotalBetThreeDown(long totalBetThreeDown) {
        this.totalBetThreeDown = totalBetThreeDown;
    }

    public long getTotalBetEven() {
        return totalBetEven;
    }

    public void setTotalBetEven(long totalBetEven) {
        this.totalBetEven = totalBetEven;
    }

    public long getTotalBetFourUp() {
        return totalBetFourUp;
    }

    public void setTotalBetFourUp(long totalBetFourUp) {
        this.totalBetFourUp = totalBetFourUp;
    }

    public long getTotalBetFourDown() {
        return totalBetFourDown;
    }

    public void setTotalBetFourDown(long totalBetFourDown) {
        this.totalBetFourDown = totalBetFourDown;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

}
