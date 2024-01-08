package com.vinplay.api.processors;

import com.vinplay.api.processors.minigame.response.GameNameResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.Games;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class GetGameNameProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        GameNameResponse response = new GameNameResponse(false, "1001");
        List<String> gameNames = new ArrayList<>();

        try {
            for (Games myEnum : Games.values()) {
                gameNames.add(myEnum.getName());
            }
            response = new GameNameResponse(true, "0");
            response.setGameNames(gameNames);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response.toJson();


    }
}
