package com.vinplay.api.processors.cashout;

import com.vinplay.cashout.BankCBResponse;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class BankCallBackProcess implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        BankCBResponse response = new BankCBResponse(200, "Thành công");
        try {
        HttpServletRequest request = (HttpServletRequest) param.get();

            String errorCode = request.getParameter("errorCode");
            String errorDescription = request.getParameter("errorDescription");
            String tranID = request.getParameter("tranID");
            String accountNumber = request.getParameter("accountNumber");
            String accountName = request.getParameter("accountName");
            String amount = request.getParameter("amount");
            String comment = request.getParameter("comment");
            String tranIDCallback = request.getParameter("tranIDCallback");
            String isBank = request.getParameter("isBank");
            String bankCode = request.getParameter("bankCode");
            String status_x = request.getParameter("status");
            

            CashoutDao cashoutDao = new CashoutDaoImpl();
            if(status_x == null || bankCode == null || tranID == null || errorCode == null
                    || errorDescription == null || accountNumber == null || accountName == null
                    || amount == null || tranIDCallback == null || comment == null || isBank == null
                    || bankCode == null){
                response.setErrorCode(500);
                response.setErrorDescription("Error, value request null");
                return response.toJson();
            }else{
                    String status = status_x;
                if(status.equals("1")){
                    UserWithdraw userWithdraw = cashoutDao.FindCashoutBankById(tranIDCallback);
                    if(userWithdraw == null){
                        response.setErrorCode(500);
                        response.setErrorDescription("TranID CallBack khong ton tai");
                        return response.toJson();
                    }else if(userWithdraw != null & userWithdraw.Status.equalsIgnoreCase("sending")){
                        String uap = userWithdraw.UserProve;
                        if(accountName.equalsIgnoreCase(userWithdraw.BankAccountName) && accountNumber.equalsIgnoreCase(userWithdraw.BankAccountNumber)
                                && amount.equalsIgnoreCase(userWithdraw.Amount+"")){

                            boolean updateTrans = cashoutDao.UpdateCashoutBank(tranIDCallback, "success", uap);
                            if(updateTrans) {
                                this.sendMesToAdmin(tranIDCallback, 100);
                            }
                            return response.toJson();

                        }else{
                            this.refundWhenError(tranIDCallback, userWithdraw, "reject", 0);
                            this.sendMesToAdmin(tranIDCallback, 2);
                            response.setErrorCode(500);
                            response.setErrorDescription("Thong tin rut tien sai");
                            return response.toJson();

                        }

                    }else if(userWithdraw != null & userWithdraw.Status.equalsIgnoreCase("reject")){
                        this.sendMesToAdmin(tranIDCallback, 2);
                        response.setErrorCode(500);
                        response.setErrorDescription("Tu choi giao dich");
                        return response.toJson();
                    }else if(userWithdraw != null & userWithdraw.Status.equalsIgnoreCase("success")){
                        this.sendMesToAdmin(tranIDCallback, 2);
                        response.setErrorCode(500);
                        response.setErrorDescription("TranID callback da rut tien roi");
                        return response.toJson();
                    }else if(userWithdraw != null & userWithdraw.Status.equalsIgnoreCase("pending")){
                        String uap = userWithdraw.UserProve;
                        if(accountName.equalsIgnoreCase(userWithdraw.BankAccountName) && accountNumber.equalsIgnoreCase(userWithdraw.BankAccountNumber)
                                && amount.equalsIgnoreCase(userWithdraw.Amount+"")){

                            boolean updateTrans = cashoutDao.UpdateCashoutBank(tranIDCallback, "success", uap);
                            if(updateTrans) {
                                this.sendMesToAdmin(tranIDCallback, 100);
                            }
                            return response.toJson();

                        }else{
                            this.refundWhenError(tranIDCallback, userWithdraw, "reject", 0);
                            this.sendMesToAdmin(tranIDCallback, 2);
                            response.setErrorCode(500);
                            response.setErrorDescription("Thong tin rut tien sai");
                            return response.toJson();

                        }
                    }
                    else{
                        response.setErrorCode(500);
                        response.setErrorDescription("Error");
                        return response.toJson();
                    }

                }else if(status.equals("2")){
                    UserWithdraw userWithdraw = cashoutDao.FindCashoutBankById(tranIDCallback);
                    if(userWithdraw == null){
                        response.setErrorCode(500);
                        response.setErrorDescription("TranID CallBack khong ton tai");
                        return response.toJson();
                    }else if(userWithdraw != null & userWithdraw.Status.equalsIgnoreCase("sending")){
                        String uap = userWithdraw.UserProve;
                        boolean updateTrans = cashoutDao.UpdateCashoutBank(tranIDCallback, "reject", uap);
                        this.refundWhenError(tranIDCallback, userWithdraw, "reject", 0);
                        if(updateTrans) {
                            this.sendMesToAdmin(tranIDCallback, 2);
                        }
                        response.setErrorCode(500);
                        response.setErrorDescription("Tu choi giao dich");
                        return response.toJson();

                    }else{
                        this.sendMesToAdmin(tranIDCallback, 2);
                        response.setErrorCode(500);
                        response.setErrorDescription("Thong tin sai");
                        return response.toJson();
                    }

                }else if(status.equals("0")){
                    response.setErrorCode(500);
                    response.setErrorDescription("Giao dich dang cho");
                    return response.toJson();
                }else{
                    response.setErrorCode(500);
                    response.setErrorDescription("error");
                    return response.toJson();
                }
            }
        } catch (Exception e) {
            response.setErrorCode(500);
            response.setErrorDescription(e+"");
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

    String refundWhenError(String transid, UserWithdraw userWithdraw, String status, int type) {
        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        historyTransService.update(transid, userWithdraw.Username, HistoryTransConst.RUT_BANK, this.getTrangthai(type), this.getTrangthaiDes(type));
        if (status.equals(CashoutUtil.STATUS_ERROR) || status.equals(CashoutUtil.STATUS_REJECT)) {
            UserServiceImpl userService = new UserServiceImpl();
            long fee = userWithdraw.AmountReal - userWithdraw.Amount;
            boolean refund = userService.refundWhenError(userWithdraw.Username, userWithdraw.AmountReal, fee);
            if (!refund) {
                return "";
            }
        }

        BroadCastUserMoney.pushBroadCast(userWithdraw.Username);
        return "true";
    }



    String getTrangthaiDes(int status) {
        switch (status) {
            case 0:
                return "Sai thông tin ngân hàng";
            case 2:
                return "Ngân hàng đang bảo trì!";
            case 3:
                return "Vui lòng phát sinh cược thêm 100% số tiền đã nạp!";
            case 105:
                return "Giao dịch thành công!";
        }
        return "Giao dịch đang được hệ thống xử lý";
    }

    String getTrangthai(int status) {
        switch (status) {
            case 0:
            case 2:
            case 3:
                return "Từ chối";
            case 105:
                return "Đã duyệt";
        }
        return "Đang xử lý";
    }
}
