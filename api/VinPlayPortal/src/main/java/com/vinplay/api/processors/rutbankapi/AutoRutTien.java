package com.vinplay.api.processors.rutbankapi;

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

import java.io.IOException;

public class AutoRutTien {
    private static final String URL_CALL_BACK = "https://lunglinhlalenluons.store/api?c=4009";
    public String autoRut(String status, String transid, String typeStr){
        try {
            String userAprrove = "Auto Rút Tiền";

            HistoryTransService historyTransService = new HistoryTransServiceImpl();

            CashoutDao cashoutDao = new CashoutDaoImpl();

            if (transid == null || transid.isEmpty()) {
                return "";
            }
            if (status == null || status.isEmpty()) {
                return "";
            }
            //find trans
            UserWithdraw userWithdraw = cashoutDao.FindCashoutBankById(transid);
            if (userWithdraw == null) {
                return "";
            }

            if (status.equals(CashoutUtil.STATUS_SENDING)) {
                this.sendMesToAdmin(transid, 102);
                CallAutoTransBankRut callBank = new CallAutoTransBankRut();
                String output = callBank.CallAPI(userWithdraw, URL_CALL_BACK); //Product
                String check_money_now = "Số dư tài khoản không đủ để thực hiện";
                if(output.contains(check_money_now)){
                    this.sendMesToAdmin(transid, 3); // Số dư tài khoản không đủ để thực hiện
                }
            }

            // update trans
            boolean updateTrans = cashoutDao.UpdateCashoutBank(transid, status, userAprrove);

            if (!updateTrans) {
                return "";
            }
            // refund

            int type = Integer.parseInt(typeStr);
            historyTransService.update(transid, userWithdraw.Username, HistoryTransConst.RUT_BANK, this.getTrangthai(type), this.getTrangthaiDes(type));

            if (status.equals(CashoutUtil.STATUS_ERROR) || status.equals(CashoutUtil.STATUS_REJECT)) {
                this.sendMesToAdmin(transid, 2); // gửi mes từ chối
                UserServiceImpl userService = new UserServiceImpl();
                long fee = userWithdraw.AmountReal - userWithdraw.Amount;
                boolean refund = userService.refundWhenError(userWithdraw.Username, userWithdraw.AmountReal, fee);
                if (!refund) {
                    return "";
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
                NapRutModel napgame = new NapRutModel(transid, userWithdraw.Username, codedl, SoTien,"Rut Bank", userWithdraw.CreatedAt);
                if(nrg.getTransID(transid) == false){
                    nrg.NapRut(napgame);
                }
            }else{
                int xx =2;
            }

            return "true";



        } catch (Exception e) {
            e.printStackTrace();
            return "";
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

    String getTrangthaiDes(int status) {
        switch (status) {
            case 0:
                return "Sai thông tin ngân hàng";
            case 2:
                return "Ngân hàng đang bảo trì!";
            case 3:
                return "Vui lòng cược thêm!";
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
