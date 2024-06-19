package com.vinplay.api.backend.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.backend.processors.cashout.NapRutGame;
import com.vinplay.api.backend.processors.cashout.NapRutModel;
import com.vinplay.api.backend.processors.rutbank.CallAutoTransBankChung;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.entities.CashoutBankResponse;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.ResultCashOutByBankResponse;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CashOutByBank8Processor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");
    private static final int MAX_ITEM = 15;
    private static final String ACCESS_TOKEN = "";
    private static final String URL_CALL_BACK = "https://lunglinhlalenluons.store/api?c=4009";

    public String execute(Param<HttpServletRequest> param) {
        ResultCashOutByBankResponse response = new ResultCashOutByBankResponse(false, "1001");
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nickName = request.getParameter("nn");
            String bankName = request.getParameter("b");
            String bankAccountNumber = request.getParameter("bankNumber");
            String bankAccountAccountName = request.getParameter("bankAccountAccountName");
            String userAprrove = request.getParameter("uad");
            userAprrove = userAprrove == null ? "" : userAprrove;
            String status = request.getParameter("st");
            String code = request.getParameter("co");
            String timeStart = request.getParameter("ts");
            String timeEnd = request.getParameter("te");
            String pageStr = request.getParameter("p");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            String transid = request.getParameter("tid");
            String typeStr = request.getParameter("type");
            String act = request.getParameter("action");
            String numberMax = request.getParameter("max_item");
            int maxItem = numberMax != null ? Integer.parseInt(numberMax) : MAX_ITEM;
            act = act == null || act.isEmpty() ? "getList" : act;
            CashoutDao cashoutDao = new CashoutDaoImpl();
//            if(typeStr.equalsIgnoreCase("100")){
//                updateHis(transid);
//            }
//            if(typeStr.equalsIgnoreCase("2") || typeStr.equalsIgnoreCase("0") || typeStr.equalsIgnoreCase("3")){
//                updateHis2(transid);
//            }

            if (act.equals("getList")) {

                UserWithdraw userWithdraw = new UserWithdraw(transid, nickName, bankAccountNumber, bankAccountAccountName, bankName, status);
                CashoutBankResponse res = cashoutDao.GetListCashoutBank(userWithdraw, page, maxItem, timeEnd, timeStart);

                return res.toJson();
            } else if (act.equals("get")) {
                if (transid == null || transid.isEmpty()) {
                    return "";
                }
                UserWithdraw userWithdraw = cashoutDao.FindCashoutBankById(transid);
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
                UserWithdraw userWithdraw = cashoutDao.FindCashoutBankById(transid);
                if (userWithdraw == null) {
                    return "";
                }

//                if (!userWithdraw.Status.equals(CashoutUtil.STATUS_PENDING)) {
//                    return "";
//                }

                if (status.equals(CashoutUtil.STATUS_SENDING)) {
                    this.sendMesToAdmin(transid, 102);
                    CallAutoTransBankChung callBank = new CallAutoTransBankChung();
                    String output = callBank.CallAPI(userWithdraw, ACCESS_TOKEN, URL_CALL_BACK); //Product
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
            }

            return "";

        } catch (Exception e) {
            logger.debug("CashOutByBankProcessor error with: " + e.getStackTrace());
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

    String getTrangthaiDes(int status) {
        switch (status) {
            case 0:
                return "Sai thông tin ngân hàng!";
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
    private void updateHis(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai","Thành công");
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void updateHis2(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai","Từ chối");
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}

