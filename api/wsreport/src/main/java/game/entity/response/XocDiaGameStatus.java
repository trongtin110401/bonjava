package game.entity.response;

import lombok.*;


@Getter
@Setter
public class XocDiaGameStatus {
    String code;
    String timmer;
    String gameState;
    String isBetting;
    String sessionId;

    public XocDiaGameStatus() {
    }

    public XocDiaGameStatus(String code, String timmer, String gameState, String isBetting, String sessionId) {
        this.code = code;
        this.timmer = timmer;
        this.gameState = gameState;
        this.isBetting = isBetting;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTimmer() {
        return timmer;
    }

    public void setTimmer(String timmer) {
        this.timmer = timmer;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public String getIsBetting() {
        return isBetting;
    }

    public void setIsBetting(String isBetting) {
        this.isBetting = isBetting;
    }
}
