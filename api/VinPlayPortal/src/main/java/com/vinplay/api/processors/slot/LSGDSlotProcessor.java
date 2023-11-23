/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.SlotMachineServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.models.minigame.pokego.LSGDPokeGo
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors.slot;

import com.vinplay.api.processors.minigame.response.LSGDPokeGoResponse;
import com.vinplay.dal.service.impl.SlotMachineServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.models.minigame.pokego.LSGDPokeGo;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class LSGDSlotProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        LSGDPokeGoResponse response = new LSGDPokeGoResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String username = request.getParameter("un");
        int page = Integer.parseInt(request.getParameter("p"));
        String gameName = Games.KHO_BAU.getName();
        if (request.getParameter("gn") != null) {
            gameName = request.getParameter("gn");
        }
        SlotMachineServiceImpl service = new SlotMachineServiceImpl();
        try {
            List results = service.getLSGD(gameName, username, page);
            response.setTotalPages(100);
            response.setResults(results);
            response.setSuccess(true);
            response.setErrorCode("0");
        }
        catch (Exception e) {
            this.logger.error((Object)"LSGD Slot Error: ", (Throwable)e);
            return e.getMessage();
        }
        return response.toJson();
    }
}

