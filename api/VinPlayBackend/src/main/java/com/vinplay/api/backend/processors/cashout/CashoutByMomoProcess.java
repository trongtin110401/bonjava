package com.vinplay.api.backend.processors.cashout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vinplay.api.backend.processors.rutbank.CallAutoTransBank;
import com.vinplay.api.backend.processors.rutbank.CallAutoTransMomo;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.entities.CashoutBankResponse;
import com.vinplay.dichvuthe.entities.CashoutMomoResponse;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.ResultCashOutByBankResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CashoutByMomoProcess implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");

    public String execute(Param<HttpServletRequest> param) {
        ResultCashOutByBankResponse response = new ResultCashOutByBankResponse(false, "1001");
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nickName = request.getParameter("nn");
            String phoneNumber = request.getParameter("phone");
            String phoneName = request.getParameter("name");

            String userAprrove = request.getParameter("uad");
            userAprrove = userAprrove == null ? "" : userAprrove;
            String status = request.getParameter("st");

            String timeStart = request.getParameter("ts");
            String timeEnd = request.getParameter("te");
            String pageStr = request.getParameter("p");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);

            String transid = request.getParameter("tid");
            String act = request.getParameter("action");
            act = act == null || act.isEmpty() ? "getList" : act;
            CashoutDao cashoutDao = new CashoutDaoImpl();
            if (act.equals("getList")) {

                UserWithdrawMomo userWithdraw = new UserWithdrawMomo(transid, nickName, phoneNumber, status);
                CashoutMomoResponse res = cashoutDao.GetListCashoutMomo(userWithdraw, page, 50, timeStart,timeEnd);

                return res.toJson();
            } else if (act.equals("get")) {
                if (transid == null || transid.isEmpty()) {
                    return "";
                }
                UserWithdrawMomo userWithdraw = cashoutDao.FindCashoutMomoById(transid);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString((Object) userWithdraw);
            } else if (act.equals("update")) {
                if (transid == null || transid.isEmpty()) {
                    return "";
                }
                if (status == null || status.isEmpty()) {
                    return "";
                }
                //find trans
                UserWithdrawMomo userWithdraw = cashoutDao.FindCashoutMomoById(transid);
                if (userWithdraw == null) {
                    return "";
                }
                if (status.equals(CashoutUtil.STATUS_SENDING)) {
                    this.sendMesToAdmin(transid, 102);
                    CallAutoTransMomo callAutoTransMomo = new CallAutoTransMomo();
                    String output = callAutoTransMomo.CallAPI(userWithdraw); //Product
                    Gson gson = new Gson();
                    if (output.contains("404")){
                        this.sendMesToAdmin(transid, 2);
                    }
                    else {
                        JSONObject jsonObject = new JSONObject(output);
                        if (jsonObject.get("ex_stt").toString().equals("-2.3")) {
                            this.sendMesToAdmin(transid, 3);
                            return "true";
                        }
                    }
                }
                // update trans
                boolean updateTrans = cashoutDao.UpdateCashoutMomo(transid, status, userAprrove);
                if (!updateTrans) {
                    return "";
                }
                // refund
                if (status.equals(CashoutUtil.STATUS_ERROR) || status.equals(CashoutUtil.STATUS_REJECT)) {
                    UserServiceImpl userService = new UserServiceImpl();
                    long fee = userWithdraw.AmountReal - userWithdraw.Amount;
                    boolean refund = userService.refundWhenError(userWithdraw.Nickname, userWithdraw.AmountReal, fee);
                    if (!refund) {
                        return "";
                    }
                }
                return "true";

            }
            return "";

        } catch (Exception e) {
            logger.debug(e.getMessage());
            return response.toJson();
        }

    }

    String sendMesToAdmin(String transID, int status) {
        EventactionAdminObj model = new EventactionAdminObj();
        model.setId(transID);
        model.setStatus(status);
        model.setType("CASH_OUT_BANK");
        try {
            SendToWS.sendBEExcEventaction(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "true";
    }
}
