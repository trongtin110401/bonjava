package com.vinplay.api.backend.processors.cashoutCard;

import com.vinplay.api.backend.processors.cashout.NapRutGame;
import com.vinplay.api.backend.processors.cashout.NapRutModel;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.momo.CardClient;
import com.vinplay.momo.RequestWithDrawCardAuto;
import com.vinplay.momo.entities.CardEntity;
import com.vinplay.momo.response.CardResponse;
import com.vinplay.payment.entities.UserWithDrawCard;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.CashOutBankManualResponse;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class ApproveCashoutCardProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");

    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        CashOutBankManualResponse response = new CashOutBankManualResponse(false, "1");
        CardClient cardClient = new CardClient();

        String transid = request.getParameter("tid");
        String status = request.getParameter("st");


        String pin = "";
        String seri = "";


        String userAprrove = request.getParameter("uad");
        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        CashoutDao cashoutDao = new CashoutDaoImpl();
        if (transid == null || transid.isEmpty()) {
            return response.toJson();
        }
        if (status == null || status.isEmpty()) {
            return response.toJson();
        }
        UserWithDrawCard userWithdraw = cashoutDao.FindCashoutCardById(transid);

        // request to server to get serial anh pin of telco

        String telco = this.getTelco(userWithdraw.telcoId);



        // handle after get response

        if (userWithdraw == null) {
            return response.toJson();
        }
        if (!userWithdraw.Status.equals("1")) {
            return response.toJson();
        }

        // refund
        if (status.equals("2") || status.equals("2")) {

            boolean updateTrans = cashoutDao.UpdateCashoutCard(transid, status, userAprrove, seri, pin);

            if (!updateTrans) {
                return response.toJson();
            }
            historyTransService.update(transid, userWithdraw.Username, HistoryTransConst.RUT_Card, "Từ chối", "Giao dịch của bạn bị từ chối");
            EventactionAdminObj model = new EventactionAdminObj();
            model.setId(transid);
            model.setStatus(2);
            model.setType("CASH_OUT_CARD");
            try {
                SendToWS.sendBEExcEventaction(model);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // update vào lịch sử
            UserServiceImpl userService = new UserServiceImpl();
            long fee = userWithdraw.AmountReal - userWithdraw.Amount;
            boolean refund = userService.refundWhenError(userWithdraw.Username, userWithdraw.AmountReal, fee);
            if (!refund) {
                return response.toJson();
            }

        } else {
            // update vào lịch sử
            try {
                CardResponse cardResponse = cardClient.doCharge(new RequestWithDrawCardAuto(telco, userWithdraw.Amount), transid);
                if (cardResponse.getMessage().equals("1")) {
                    response.setErrorCode("3"); // số dư ko đủ
                    return response.toJson();
                } else {

                    List<CardEntity> cardEntityList = cardResponse.getBody();
                    CardEntity cardEntity = cardEntityList.get(0);
                    pin = cardEntity.getCode();
                    seri = cardEntity.getSeri();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return response.toJson();
            }
            // update vào lịch sử
            boolean updateTrans = cashoutDao.UpdateCashoutCard(transid, status, userAprrove, seri, pin);
            if (!updateTrans) {
                return response.toJson();
            }
            historyTransService.update(transid, userWithdraw.Username, HistoryTransConst.RUT_Card, "Đã duyệt", "Mã thẻ của bạn là Seri: " + seri + " Mã pin: " + pin);
            EventactionAdminObj model = new EventactionAdminObj();
            model.setId(transid);
            model.setStatus(100);
            model.setType("CASH_OUT_CARD");
            try {
                SendToWS.sendBEExcEventaction(model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BroadCastUserMoney.pushBroadCast(userWithdraw.Username);
        NapRutGame nrg = new NapRutGame();
        String codedl = nrg.getMaDaily(userWithdraw.Username);
        long SoTien = userWithdraw.AmountReal * (-1);
        if(codedl == null){
            int xx = 2;
        }else if(codedl != null && codedl.trim().length() == 0){
            int xx = 2;
        }else if(codedl != null && codedl.trim().equalsIgnoreCase("null") == false){
            NapRutModel napgame = new NapRutModel(transid, userWithdraw.Username, codedl, SoTien,"Rut Card", userWithdraw.CreatedAt);
            nrg.NapRut(napgame);
        }else{
            int xx =2;
        }

        response.setSuccess(true);
        response.setErrorCode("0");
        return response.toJson();

    }

    String getTelco(String telcoId) {
        switch (telcoId) {
            case "VMS":
                return "Mobi";
            case "VTT":
                return "Viettel";
            case "VNP":
                return "Vina";
            default:
                return "none";
        }
    }
}
