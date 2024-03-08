package com.vinplay.api.backend.processors;

import com.vinplay.api.backend.response.SlotFeeResponse;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.Games;

import javax.servlet.http.HttpServletRequest;

public class GetAllSlotFeeProcessor implements BaseProcessor<HttpServletRequest, String> {

    private final String PERCENT_FEE = "_PERCENT_FEE";

    public String execute(Param<HttpServletRequest> param) {
        SlotFeeResponse response = new SlotFeeResponse(true, "200");
        CacheService cacheService = new CacheServiceImpl();

        setGameValue(response, cacheService, Games.COWBOY);
        setGameValue(response, cacheService, Games.FAST_AND_FURIOUS);
        setGameValue(response, cacheService, Games.LADY_NIGHT);
        setGameValue(response, cacheService, Games.CARIBE);
        setGameValue(response, cacheService, Games.BONG_LAI_CAC);
        setGameValue(response, cacheService, Games.HALLOWEEN);
        setGameValue(response, cacheService, Games.LAS_VEGAS);
        setGameValue(response, cacheService, Games.SEXY_DANCE);
        setGameValue(response, cacheService, Games.LIEN_MINH);

        return response.toJson();
    }

    private void setGameValue(SlotFeeResponse response, CacheService cacheService, Games game) {
        try {
            int value = cacheService.getValueInt(game.getName() + PERCENT_FEE);
            setGameValue(response, game, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGameValue(SlotFeeResponse response, Games game, int value) {
        switch (game) {
            case COWBOY:
                response.setCowboy(value);
                break;
            case FAST_AND_FURIOUS:
                response.setFastAndFurious(value);
                break;
            case LADY_NIGHT:
                response.setLadyNight(value);
                break;
            case CARIBE:
                response.setCaribe(value);
                break;
            case BONG_LAI_CAC:
                response.setBongLaiCac(value);
                break;
            case HALLOWEEN:
                response.setHalloween(value);
                break;
            case LAS_VEGAS:
                response.setLasVegas(value);
                break;
            case SEXY_DANCE:
                response.setSexyDance(value);
                break;
            case LIEN_MINH:
                response.setLienMinh(value);
                break;
            default:
                break;
        }
    }
}
