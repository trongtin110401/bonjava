package game.models.baucuato2;

import java.util.Map;

public class BauCuaToReportResponse extends BaseResponseModel {

    private Map<Integer, Long> mapReportBet ;
    private Map<Integer, Long> mapBotReportBet ;
    private String remainingTime;
    private boolean isBetting;
    private long referenceId;




    public Map<Integer, Long> getMapReportBet() {
        return mapReportBet;
    }

    public void setMapReportBet(Map<Integer, Long> mapReportBet) {
        this.mapReportBet = mapReportBet;
    }

    public Map<Integer, Long> getMapBotReportBet() {
        return mapBotReportBet;
    }

    public void setMapBotReportBet(Map<Integer, Long> mapBotReportBet) {
        this.mapBotReportBet = mapBotReportBet;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public boolean isBetting() {
        return isBetting;
    }

    public void setBetting(boolean betting) {
        isBetting = betting;
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }

    public BauCuaToReportResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}
