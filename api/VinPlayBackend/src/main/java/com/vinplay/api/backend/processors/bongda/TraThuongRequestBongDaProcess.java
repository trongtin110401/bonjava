package com.vinplay.api.backend.processors.bongda;

import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.dao.UserBetBongDaDao;
import com.vinplay.bongda.dao.impl.KeoBongDaImpl;
import com.vinplay.bongda.dao.impl.UserBetBongDaimpl;
import com.vinplay.bongda.entities.UserBetBongDa;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.statics.TransType;

import javax.servlet.http.HttpServletRequest;

public class TraThuongRequestBongDaProcess implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {

        HttpServletRequest request = param.get();
        UserBetBongDaDao dao = new UserBetBongDaimpl();
        String session = request.getParameter("session");
        String id = request.getParameter("id");
        String idtran = request.getParameter("idTran");
        long moneyWin = Long.parseLong(request.getParameter("moenyWin"));
        String nickname = request.getParameter("nickname");
        UserBetBongDa userBetBongDa = dao.findUserBetBongDaBySessionAndId(session, id);
        UserService userService = new UserServiceImpl();
//        if (userBetBongDa.getIdTran().equals(idtran) && userBetBongDa.getNickname().equals(nickname.trim())) {
        if (userBetBongDa.getNickname().equals(nickname.trim())) {
            MoneyResponse moneyRes = userService.updateMoney(nickname, moneyWin, "vin", Consts.Bong_Da, Consts.Bong_Da, "Trả thưởng bóng đá", 0, null, TransType.NO_VIPPOINT);
            if (!moneyRes.isSuccess()) {
                return "0";
            }
            dao.updateUserBetBongDa(id, 1, (int)moneyWin);
            return "1";
        }


        return "0";
    }
}
