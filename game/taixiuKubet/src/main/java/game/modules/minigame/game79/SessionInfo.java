package game.modules.minigame.game79;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionInfo {
    @JsonProperty("CurrentState")
    private int currentState;

    @JsonProperty("Result")
    private Result result;

    @JsonProperty("SessionID")
    private int sessionId;

    @JsonProperty("GID")
    private long gid;

    @JsonProperty("Ellapsed")
    private int ellapsed;

    @JsonProperty("TotalBetTai")
    private long totalBetTai;

    @JsonProperty("TotalBetXiu")
    private long totalBetXiu;

    @JsonProperty("TotalBetChan")
    private long totalBetChan;

    @JsonProperty("TotalBetLe")
    private long totalBetLe;

    @JsonProperty("TotalTai")
    private int totalTai;

    @JsonProperty("TotalXiu")
    private int totalXiu;

    @JsonProperty("TotalChan")
    private int totalChan;

    @JsonProperty("TotalLe")
    private int totalLe;

    @JsonProperty("IsStartSession")
    private boolean isStartSession;

    @JsonProperty("IsStartWaiting")
    private boolean isStartWaiting;

    @JsonProperty("StartSessionTime")
    private String startSessionTime;

    // Getters and setters


    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public int getEllapsed() {
        return ellapsed;
    }

    public void setEllapsed(int ellapsed) {
        this.ellapsed = ellapsed;
    }

    public long getTotalBetTai() {
        return totalBetTai;
    }

    public void setTotalBetTai(long totalBetTai) {
        this.totalBetTai = totalBetTai;
    }

    public long getTotalBetXiu() {
        return totalBetXiu;
    }

    public void setTotalBetXiu(long totalBetXiu) {
        this.totalBetXiu = totalBetXiu;
    }

    public long getTotalBetChan() {
        return totalBetChan;
    }

    public void setTotalBetChan(long totalBetChan) {
        this.totalBetChan = totalBetChan;
    }

    public long getTotalBetLe() {
        return totalBetLe;
    }

    public void setTotalBetLe(long totalBetLe) {
        this.totalBetLe = totalBetLe;
    }

    public int getTotalTai() {
        return totalTai;
    }

    public void setTotalTai(int totalTai) {
        this.totalTai = totalTai;
    }

    public int getTotalXiu() {
        return totalXiu;
    }

    public void setTotalXiu(int totalXiu) {
        this.totalXiu = totalXiu;
    }

    public int getTotalChan() {
        return totalChan;
    }

    public void setTotalChan(int totalChan) {
        this.totalChan = totalChan;
    }

    public int getTotalLe() {
        return totalLe;
    }

    public void setTotalLe(int totalLe) {
        this.totalLe = totalLe;
    }

    public boolean isStartSession() {
        return isStartSession;
    }

    public void setStartSession(boolean startSession) {
        isStartSession = startSession;
    }

    public boolean isStartWaiting() {
        return isStartWaiting;
    }

    public void setStartWaiting(boolean startWaiting) {
        isStartWaiting = startWaiting;
    }

    public String getStartSessionTime() {
        return startSessionTime;
    }

    public void setStartSessionTime(String startSessionTime) {
        this.startSessionTime = startSessionTime;
    }
}

