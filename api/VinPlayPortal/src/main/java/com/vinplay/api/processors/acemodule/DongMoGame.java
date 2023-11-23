package com.vinplay.api.processors.acemodule;

import com.vinplay.dal.service.impl.AceMoneyService;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class DongMoGame implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String game = request.getParameter("game");
        String isOpen = request.getParameter("t");
        boolean dong = isOpen.equalsIgnoreCase("1") ? true : false;
        AceMoneyService aceMoneyService = new AceMoneyService();
        if (game.contains("ace")) {
            aceMoneyService.setOpenAce(dong);
            if (!dong) {
                return "Đã đóng game bên ace";
            } else {
                return "Đã mở game bên ace";
            }
        } else if (game.contains("lode")) {
            aceMoneyService.setLodeNew(dong);
            if (!dong) {
                return "Đã đóng game lô đề";
            } else {
                return "Đã mở game lô đề";
            }
        } else if (game.contains("bongda")) {
            aceMoneyService.setBongDa(dong);
            if (!dong) {
                return "Đã đóng game bóng đá";
            } else {
                return "Đã mở game bóng đá";
            }
        } else {
            return "";
        }
    }

}
