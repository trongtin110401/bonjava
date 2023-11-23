package game.entity.response;

import java.util.ArrayList;

public class StateGameResponse {
    String code;
    ArrayList<String> stateLists;

    public StateGameResponse() {
    }

    public StateGameResponse(String code, ArrayList<String> stateLists) {
        this.code = code;
        this.stateLists = stateLists;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<String> getStateLists() {
        return stateLists;
    }

    public void setStateLists(ArrayList<String> stateLists) {
        this.stateLists = stateLists;
    }
}
