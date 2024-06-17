package game.entity.response;

import game.entity.entitiesxocdia.GamePotReportModel;
import game.entity.entitiesxocdia.UserBetModel;

import java.util.ArrayList;
import java.util.List;


public class XocDiaReportResponse {
    String code;
    ArrayList<GamePotReportModel> potList;
    public List<UserBetModel> users;

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

    public List<UserBetModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserBetModel> users) {
        this.users = users;
    }
}
