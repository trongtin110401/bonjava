package com.vinplay.api.backend.processors;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.backend.models.CallBackModel;
import com.vinplay.api.backend.processors.cashout.NapRutGame;
import com.vinplay.api.backend.processors.cashout.NapRutModel;
import com.vinplay.common.notification.NotificationAdminObj;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.utils.TelegramAlert;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.response.EventResponse;
import com.vinplay.vbee.common.response.RechargeByCardReponse;
import com.vinplay.vbee.common.response.UserEvent;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CallBackProcess implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String chargeId = request.getParameter("chargeId");
        String requestId = request.getParameter("requestId");
        String chargeType = request.getParameter("chargeType");
        String chargeCode = request.getParameter("chargeCode");
        String regAmount = request.getParameter("chargeAmount");
        String status = request.getParameter("status");
        CallBackModel callBackModel = new CallBackModel(chargeId, chargeType, chargeCode, regAmount, status, requestId);

        if ("momo".equals(chargeType)) {
            ApproveDepositMomoProcessor(callBackModel);
        } else if ("momoout".equals(chargeType)) {
            cashOutByMomo(callBackModel);
        } else if ("bank".equals(chargeType)) {
            ApproveDepositBankProcessor(callBackModel);
        } else if ("bankout".equals(chargeType)) {
            cashOutByBank(callBackModel);
        } else if ("usdt".equals(chargeType)) {

        } else if ("card".equals(chargeType)) {
            DepositCardProcessor(callBackModel);
        }
        return "ok";
    }


    public String cashOutByMomo(CallBackModel callBackModel) {
        CashoutDao cashoutDao = new CashoutDaoImpl();
        UserWithdrawMomo userWithdraw = cashoutDao.FindCashoutMomoById(callBackModel.getRequestId());
        if (userWithdraw == null) {
            return "";
        }
        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
        HistoryTransModel historyTransModel = historyTransDao.findTransaction(callBackModel.getRequestId(), userWithdraw.Nickname, "RUT_BANK");
        if (callBackModel.getStatus().equals("success")) {
            cashoutDao.UpdateCashoutMomo(callBackModel.getChargeId(), CashoutUtil.STATUS_SUCCESS, "Auto_Bank");
            historyTransModel.setTrangthai("Thành công");
            historyTransModel.setGhiChu("Thành công");
            userWithdraw.Amount = Integer.parseInt(callBackModel.getRegAmount());
            cashoutDao.UpdateCashoutMomo(callBackModel.getRequestId(), CashoutUtil.STATUS_SUCCESS, "Auto_Bank");
            userWithdraw.Status = CashoutUtil.STATUS_SUCCESS;
            TelegramAlert.SendMessageCashoutMomo(userWithdraw);

        } else {
            UserServiceImpl userService = new UserServiceImpl();
            long fee = userWithdraw.AmountReal - userWithdraw.Amount;
            userService.refundWhenError(userWithdraw.Nickname, userWithdraw.AmountReal, fee);
            cashoutDao.UpdateCashoutMomo(callBackModel.getChargeId(), CashoutUtil.STATUS_ERROR, "Auto_Bank");
            historyTransModel.setTrangthai("Thất bại");
            historyTransModel.setGhiChu("Thất bại");
            userWithdraw.Status = CashoutUtil.STATUS_ERROR;
        }
        try {
            NotificationAdminObj obj = new NotificationAdminObj();
            SendToWS.sendBEExcCashoutbyMomo(userWithdraw);
            obj.setRutMomo(true);
            SendToWS.sendBEExcNotification(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        historyTransDao.updateTransaction(historyTransModel);
        return "true";
    }

    public String cashOutByBank(CallBackModel callBackModel) {
        CashoutDao cashoutDao = new CashoutDaoImpl();
        UserWithdraw userWithdraw = cashoutDao.FindCashoutBankById(callBackModel.getRequestId());
        if (userWithdraw == null) {
            return "";
        }
        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
        HistoryTransModel historyTransModel = historyTransDao.findTransaction(callBackModel.getRequestId(), userWithdraw.Username, "RUT_BANK");
        if (historyTransModel == null) {
            return "";
        }
        if (callBackModel.getStatus().equals("success")) {
            historyTransModel.setTrangthai("Thành công");
            historyTransModel.setGhiChu("Thành công");
            userWithdraw.Amount = Integer.parseInt(callBackModel.getRegAmount());
            userWithdraw.Status = "success";
            TelegramAlert.SendMessageCashout(userWithdraw);
            cashoutDao.UpdateCashoutBank(callBackModel.getRequestId(), CashoutUtil.STATUS_SUCCESS, "Auto_Bank");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            long fee = userWithdraw.AmountReal - userWithdraw.Amount;
            boolean refund = userService.refundWhenError(userWithdraw.Username, userWithdraw.AmountReal, fee);
            cashoutDao.UpdateCashoutBank(callBackModel.getRequestId(), CashoutUtil.STATUS_ERROR, "Auto_Bank");
            historyTransModel.setTrangthai("Thất bại");
            historyTransModel.setGhiChu("Thất bại");
            if (!refund) {
                return "";
            }
        }

        historyTransDao.updateTransaction(historyTransModel);
        NotificationAdminObj obj = new NotificationAdminObj();
        try {
            obj.setRutBank(true);
            SendToWS.sendBEExcNotification(obj);
            SendToWS.sendBEExcCashoutbybank(userWithdraw);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "true";
    }


    public String ApproveDepositMomoProcessor(CallBackModel callBackModel) {
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        try {
            String transId = callBackModel.getChargeId();
            int type;
            if (callBackModel.getStatus().equals("success")) {
                type = 0;
            } else {
                type = 1;
            }
            String userApprove = "AutoBank";
            long tien = Long.parseLong(callBackModel.getRegAmount());


            RechargeDao dao = new RechargeDaoImpl();
            // find transaction in db
            DepositMomoModel trans = dao.FindDepositMomoById(transId);
            if (trans == null) {
                return response.toJson();
            }
            if (trans.Status != DvtConst.STATUS_PENDING) {
                return response.toJson();
            }
            EventResponse eventResponse = checkEventNapTien(trans.Nickname);
            if (eventResponse.isSuccess()) {
                long eventAmount = tien * eventResponse.getRate() / 100;
                tien += eventAmount;
                UserEvent userEvent = new UserEvent();
                userEvent.setEventName(eventResponse.getEventName());
                userEvent.setEventAmount(eventAmount);
                userEvent.setActualAmount(tien);
                userEvent.setId(System.currentTimeMillis());
                userEvent.setEventId(eventResponse.getId());
                userEvent.setNickname(trans.Nickname);
                userEvent.setCreatedDate(VinPlayUtils.getCurrentDateTime());
                OtherService otherService = new OtherServiceImpl();
                otherService.saveUserNapTienEvent(userEvent);
            }

            // update trans in db
            int status = type == 0 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
            boolean resultUpdateTrans = dao.UpdateDepositMomoManualStatusCallBack(transId, status, "", userApprove, String.valueOf(tien));
            historyTransService.update(transId, trans.Nickname, HistoryTransConst.MOMO, this.getTrangthai(status), this.getTrangthaiDes(status));
            if (resultUpdateTrans) {
                EventactionAdminObj model = new EventactionAdminObj();
                model.setId(transId);
                model.setStatus(status);
                model.setType("DEPOSIT_MOMO");
            }
            if (!resultUpdateTrans) {
                return response.toJson();
            }
            HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
            UserServiceImpl service = new UserServiceImpl();
            try {
                if (type == 0) {
                    double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
                    double amount = fee * tien;
                    long totalFee = Math.round(tien - amount);
                    totalFee = totalFee > 0 ? totalFee : 0;
                    response = service.updateMoneyFromAdmin(trans.Nickname, tien, "vin", Consts.RECHARGE_BY_MOMO, "Deposit Momo", "Deposit Momo", totalFee);
                    trans.Amount = tien;
                    historyTransDao.insertTransaction(new HistoryTransModel(transId, "MoMo", "Nạp tiền", String.valueOf(tien), "Thành công", "Nạp Tiền Thành công ", trans.Nickname, HistoryTransConst.MOMO, transId));
                    TelegramAlert.SendMessageDepositMomo(trans);
                    BroadCastUserMoney.pushBroadCast(trans.Nickname);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            NotificationAdminObj obj = new NotificationAdminObj();
            DepositBankModel model = new DepositBankModel(trans.Nickname, trans.Amount, trans.BankBrandName, trans.BankAccountName, trans.BankAccountNumber);
            model.setId(callBackModel.getRequestId());
            model.setTransactionID(callBackModel.getRequestId());
            try {
                model.setStatus(1);
                model.setDescription(trans.Description);
                model.setCreatedAt(VinPlayUtils.getCurrentDateTime());
                model.setUpdatedAt(VinPlayUtils.getCurrentDateTime());
                SendToWS.sendBEExcRechargebyMomosunvin(model);
                obj.setNapBank(true);
                SendToWS.sendBEExcNotification(obj);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return response.toJson();
        } catch (Exception e) {
            return response.toJson();
        }
    }

    public String DepositCardProcessor(CallBackModel callBackModel) {

        BaseResponseModel response = new BaseResponseModel(false, "1001");
        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        try {
            String transId = callBackModel.getRequestId();

            int type;
            if (callBackModel.getStatus().equals("success")) {
                type = 0;
            } else {
                type = 1;
            }
            String userApprove = "AutoBank";
            long tien = Long.parseLong(callBackModel.getRegAmount());
            tien = (long) (tien * 0.8);

            RechargeDao dao = new RechargeDaoImpl();
            RechargeByCardReponse trans = dao.searchRechargeByCard(transId);
            HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
            HistoryTransModel historyTransModel = historyTransDao.findTransactionByTransId(transId);
            if (trans == null) {
                return response.toJson();
            }


            EventResponse eventResponse = checkEventNapTien(trans.nickName);
            if (eventResponse.isSuccess()) {
                long eventAmount = tien * eventResponse.getRate() / 100;
                tien += eventAmount;
                UserEvent userEvent = new UserEvent();
                userEvent.setEventName(eventResponse.getEventName());
                userEvent.setEventAmount(eventAmount);
                userEvent.setActualAmount(tien);
                userEvent.setId(System.currentTimeMillis());
                userEvent.setEventId(eventResponse.getId());
                userEvent.setNickname(trans.nickName);
                userEvent.setCreatedDate(VinPlayUtils.getCurrentDateTime());
                OtherService otherService = new OtherServiceImpl();
                otherService.saveUserNapTienEvent(userEvent);
            }
            int status;
            if (type == 0) {
                status = DvtConst.STATUS_APPROVE;
                historyTransModel.setTrangthai("Thành công");
                historyTransModel.setGhiChu("Thành công");

            } else {
                status = DvtConst.STATUS_REJECT;
                historyTransModel.setTrangthai("Thất bại");
                historyTransModel.setGhiChu("Thất bại");
                historyTransDao.updateTransaction(historyTransModel);
                dao.UpdateDepositCard(transId, status, trans.message, userApprove, Long.parseLong(callBackModel.getRegAmount()));
                return response.toJson();
            }

            historyTransDao.updateTransaction(historyTransModel);
            dao.UpdateDepositCard(transId, status, trans.message, userApprove, Long.parseLong(callBackModel.getRegAmount()));
            //update user money
            UserServiceImpl service = new UserServiceImpl();
            try {
                response = service.updateMoneyFromAdmin(trans.nickName, tien, "vin", Consts.RECHARGE_BY_CARD, Consts.RECHARGE_BY_CARD, "Deposit Cart", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateMoneyCodePayMomoSun2(transId, String.valueOf(tien));
            updateSTTCodePayMomoSun2(transId);
            EventactionAdminObj model = new EventactionAdminObj();
            model.setId(transId);
            model.setStatus(100);
            model.setType("DEPOSIT_BANK");
            try {
                SendToWS.sendBEExcEventaction(model);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (type == 0) {
                trans.amount = (int) tien;
                TelegramAlert.SendMessageDepositCart(tien, trans.nickName);
                BroadCastUserMoney.pushBroadCast(trans.nickName);
                BroadCastUserMoney.pushBroadTime(trans.nickName);
                NapRutGame nrg = new NapRutGame();
                String codedl = nrg.getMaDaily(trans.nickName);
                NapRutModel napgame = new NapRutModel(transId, trans.nickName, codedl, tien, "Cart", trans.timelog);
                nrg.NapRut(napgame);
            }
            response.setErrorCode("200");
            response.setSuccess(true);
            return response.toJson();
        } catch (Exception e) {
            return response.toJson();
        }
    }

    public String ApproveDepositBankProcessor(CallBackModel callBackModel) {

        synchronized (this) {
            BaseResponseModel response = new BaseResponseModel(false, "1001");
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            try {
                String transId = callBackModel.getChargeId();
                int type;
                if (callBackModel.getStatus().equals("success")) {
                    type = 0;
                } else {
                    type = 1;
                }
                String userApprove = "AutoBank";
                long tien = Long.parseLong(callBackModel.getRegAmount());

                RechargeDao dao = new RechargeDaoImpl();

                DepositBankModel trans = dao.FindDepositBankById(transId);
                if (trans == null) {
                    return response.toJson();
                }

                EventResponse eventResponse = checkEventNapTien(trans.Nickname);
                if (eventResponse.isSuccess()) {
                    long eventAmount = tien * eventResponse.getRate() / 100;
                    tien += eventAmount;
                    UserEvent userEvent = new UserEvent();
                    userEvent.setEventName(eventResponse.getEventName());
                    userEvent.setEventAmount(eventAmount);
                    userEvent.setActualAmount(tien);
                    userEvent.setId(System.currentTimeMillis());
                    userEvent.setEventId(eventResponse.getId());
                    userEvent.setNickname(trans.Nickname);
                    userEvent.setCreatedDate(VinPlayUtils.getCurrentDateTime());
                    OtherService otherService = new OtherServiceImpl();
                    otherService.saveUserNapTienEvent(userEvent);
                }
                // update trans in db
                int status = type == 1 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
                trans.Status = status;
                boolean resultUpdateTrans = dao.UpdateDepositBankManualStatusCallBack(transId, status, trans.getDescription(), userApprove, callBackModel.getRegAmount());
                if (!resultUpdateTrans || type == 1) {
                    return response.toJson();
                }

                BroadCastUserMoney.pushBroadTime2(trans.getNickname());
                response.setSuccess(true);
                historyTransService.update(transId, trans.getNickname(), HistoryTransConst.BANK, "Thành công", "Giao dịch thành công");
                updateCodepay(trans.getNickname(), true, trans.getDescription(), trans.getUserSender());

                UserServiceImpl service = new UserServiceImpl();
                try {
                    response = service.updateMoneyFromAdmin(trans.getNickname(), tien, "vin", Consts.RECHARGE_BY_BANK, "Deposit bank", "Deposit bank", 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                historyTransDao.insertTransaction(new HistoryTransModel(transId, "Ngân Hàng", "Nạp tiền", String.valueOf(tien), "Thành công", "Nạp Tiền Thành công ", trans.Nickname, HistoryTransConst.BANK, transId));
                updateMoneyCodePayMomoSun(transId, tien);
                updateMoneyCodePayMomoSun2(transId, String.valueOf(tien));
                updateSttCodePayMomoSun(transId, userApprove);
                updateSTTCodePayMomoSun2(transId);
                EventactionAdminObj model = new EventactionAdminObj();
                model.setId(transId);
                model.setStatus(100);
                model.setType("DEPOSIT_BANK");
                try {
                    SendToWS.sendBEExcRechargebybank(trans);
                    SendToWS.sendBEExcEventaction(model);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                trans.setAmount(tien);
                TelegramAlert.SendMessageDepositBank(trans);
                BroadCastUserMoney.pushBroadCast(trans.getNickname());
                BroadCastUserMoney.pushBroadTime(trans.getNickname());
                updateCodepay(trans.getNickname(), true, trans.getDescription(), trans.getBankBrandName());
                NapRutGame nrg = new NapRutGame();
                String codedl = nrg.getMaDaily(trans.Nickname);
                NapRutModel napgame = new NapRutModel(transId, trans.getNickname(), codedl, tien, "Bank", trans.CreatedAt);
                nrg.NapRut(napgame);
                response.setErrorCode("200");
                response.setSuccess(true);
                return response.toJson();
            } catch (Exception e) {
                return response.toJson();
            }

        }
    }


    private void updateMoneyCodePayMomoSun(String TrainID, long tien) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_bank_manual");
            Document doc = new Document();
            doc.append("Amount", tien);
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateMoneyCodePayMomoSun2(String TrainID, String tien) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("sotien", tien);
            col.updateOne(new Document("transId", TrainID), new Document("$set", doc));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String getTrangthai(int status) {
        switch (status) {
            case 1:
                return "Đang xử lý";
            case 2:
                return "Từ chối";
            case 100:
                return "Thành công";
        }
        return "Đang xử lý";
    }

    String getTrangthaiDes(int status) {
        switch (status) {
            case 1:
                return "Hệ thống đang xử lý";
            case 2:
                return "Giao dịch bị từ chối";
            case 100:
                return "Giao dịch thành công";
        }
        return "Đang xử lý";
    }

    public void updateCodepay(String nickname, boolean use, String codepay, String bankname) {
        try {
            int check = 0;
            if (use == true) {
                check = 1;
            } else {
                check = 0;
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("codepay", codepay);
            doc.append("use", check);
            doc.append("createAt", timeAt);
            doc.append("bankname", bankname);
            col.updateOne((Bson) new Document("nickname", nickname), new Document("$set", doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
    }

    private void updateSttCodePayMomoSun(String TrainID, String ua) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_bank_manual");
            Document doc = new Document();
            doc.append("Status", 100);
            doc.append("UserApprove", ua);
            col.updateOne(new Document("Id", TrainID), new Document("$set", doc));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSTTCodePayMomoSun2(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai", "Thành công");
            col.updateOne(new Document("transId", TrainID), new Document("$set", doc));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public EventResponse checkEventNapTien(String nickname) {
        EventResponse eventResponse = new EventResponse(false,"1001");
        OtherService service = new OtherServiceImpl();
        eventResponse = service.getCurrentEvent();
        if (eventResponse == null) {
            return eventResponse;
        }
        if (!eventResponse.isStatus()) {
            return eventResponse;
        }
        if (service.checkUserNapTienEvent(eventResponse.getId(), nickname)) {
            eventResponse.setSuccess(false);
            return eventResponse;
        }
        return eventResponse;
    }
}
