package com.vinplay.api.processors.minigame.response;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class GameNameResponse extends BaseResponseModel {
    public GameNameResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private List<String> gameNames;

    public List<String> getGameNames() {
        return gameNames;
    }

    public void setGameNames(List<String> gameNames) {
        this.gameNames = gameNames;
    }
}
