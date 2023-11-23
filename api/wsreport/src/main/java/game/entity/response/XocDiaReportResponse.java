package game.entity.response;

import game.entity.entitiesxocdia.GamePot;
import game.entity.entitiesxocdia.GamePotReportModel;
import lombok.*;

import java.util.ArrayList;
import java.util.Vector;


public class XocDiaReportResponse {
    String code;
    ArrayList<GamePotReportModel> potList;

    public XocDiaReportResponse() {
    }

    public ArrayList<GamePotReportModel> getPotList() {
        return potList;
    }

    public void setPotList(ArrayList<GamePotReportModel> potList) {
        this.potList = potList;
    }

    public XocDiaReportResponse(String code, ArrayList<GamePotReportModel> potList) {
        this.code = code;
        this.potList = potList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
