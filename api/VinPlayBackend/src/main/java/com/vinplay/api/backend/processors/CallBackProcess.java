package com.vinplay.api.backend.processors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.backend.models.CallBackModel;
import com.vinplay.api.backend.processors.cashout.NapRutGame;
import com.vinplay.api.backend.processors.cashout.NapRutModel;
import com.vinplay.api.backend.processors.rutbank.CallAutoTransMomo;
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
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.utils.TelegramAlert;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
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
        String chargeType = request.getParameter("chargeType");
        String chargeCode = request.getParameter("chargeCode");
        String regAmount = request.getParameter("chargeAmount");
        String status = request.getParameter("status");
        CallBackModel callBackModel = new CallBackModel(chargeId, chargeType, chargeCode, regAmount, status);

        if ("momo".equals(chargeType)) {
            ApproveDepositMomoProcessor(callBackModel);
        } else if ("momoout".equals(chargeType)) {
            cashOutByMomo(callBackModel);
        } else if ("bank".equals(chargeType)) {
            ApproveDepositBankProcessor(callBackModel);
        } else if ("bankout".equals(chargeType)) {
            cashOutByBank(callBackModel);
        } else if ("usdt".equals(chargeType)) {

        }
        return "ok";
    }


    public String cashOutByMomo(CallBackModel callBackModel) {
        CashoutDao cashoutDao = new CashoutDaoImpl();
        UserWithdrawMomo userWithdraw = cashoutDao.FindCashoutMomoById(callBackModel.getChargeId());
        if (userWithdraw == null) {
            return "";
        }
        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
        HistoryTransModel historyTransModel = historyTransDao.findTransaction(callBackModel.getChargeId(), userWithdraw.Nickname, "RUT_BANK");
        if (callBackModel.getStatus().equals("success")) {
            cashoutDao.UpdateCashoutMomo(callBackModel.getChargeId(), CashoutUtil.STATUS_SUCCESS, "Auto_Bank");
            historyTransModel.setTrangthai("Thành công");
            historyTransModel.setGhiChu("Thành công");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            long fee = userWithdraw.AmountReal - userWithdraw.Amount;
            boolean refund = userService.refundWhenError(userWithdraw.Nickname, userWithdraw.AmountReal, fee);
            cashoutDao.UpdateCashoutMomo(callBackModel.getChargeId(), CashoutUtil.STATUS_ERROR, "Auto_Bank");
            historyTransModel.setTrangthai("Thất bại");
            historyTransModel.setGhiChu("Thất bại");
            if (!refund) {
                return "";
            }
        }

        return "true";
    }

    public String cashOutByBank(CallBackModel callBackModel) {
        CashoutDao cashoutDao = new CashoutDaoImpl();
        UserWithdraw userWithdraw = cashoutDao.FindCashoutBankById(callBackModel.getChargeId());
        if (userWithdraw == null) {
            return "";
        }
        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
        HistoryTransModel historyTransModel = historyTransDao.findTransaction(callBackModel.getChargeId(), userWithdraw.Username, "RUT_BANK");
        if (historyTransModel == null) {
            return "";
        }
        if (callBackModel.getStatus().equals("success")) {
            historyTransModel.setTrangthai("Thành công");
            historyTransModel.setGhiChu("Thành công");
            cashoutDao.UpdateCashoutBank(callBackModel.getChargeId(), CashoutUtil.STATUS_SUCCESS, "Auto_Bank");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            long fee = userWithdraw.AmountReal - userWithdraw.Amount;
            boolean refund = userService.refundWhenError(userWithdraw.Username, userWithdraw.AmountReal, fee);
            cashoutDao.UpdateCashoutMomo(callBackModel.getChargeId(), CashoutUtil.STATUS_ERROR, "Auto_Bank");
            historyTransModel.setTrangthai("Thất bại");
            historyTransModel.setGhiChu("Thất bại");
            if (!refund) {
                return "";
            }
        }

        historyTransDao.updateTransaction(historyTransModel);

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
            // update trans in db
            int status = type == 0 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
            boolean resultUpdateTrans = dao.UpdateDepositMomoManualStatus(transId, status, "", userApprove);
            historyTransService.update(transId, trans.Nickname, HistoryTransConst.MOMO, this.getTrangthai(status), this.getTrangthaiDes(status));
            if (resultUpdateTrans) {
                EventactionAdminObj model = new EventactionAdminObj();
                model.setId(transId);
                model.setStatus(status);
                model.setType("DEPOSIT_MOMO");
                try {
                    SendToWS.sendBEExcEventaction(model);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!resultUpdateTrans) {
                return response.toJson();
            }
            //update user money
            UserServiceImpl service = new UserServiceImpl();
            try {
                double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
                double amount = fee * tien;
                long totalFee = Math.round(tien - amount);
                totalFee = totalFee > 0 ? totalFee : 0;
                response = service.updateMoneyFromAdmin(trans.Nickname, tien, "vin", Consts.RECHARGE_BY_MOMO, "Deposit Momo", "Deposit Momo", totalFee);
                TelegramAlert.SendMessageDepositMomo(trans);
            } catch (Exception e) {
                e.printStackTrace();
            }
            BroadCastUserMoney.pushBroadCast(trans.Nickname);
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

                // update trans in db
                int status = type == 1 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
                boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(transId, status, trans.getDescription(), userApprove);
                if (!resultUpdateTrans) {
                    return response.toJson();
                }
                if (type == 0) {
                    BroadCastUserMoney.pushBroadTime2(trans.getNickname());
                    response.setSuccess(true);
                    historyTransService.update(transId, trans.getNickname(), HistoryTransConst.BANK, "Từ chối", "Giao dịch bị từ chối");
                    EventactionAdminObj model = new EventactionAdminObj();
                    model.setId(transId);
                    model.setStatus(2);
                    model.setType("DEPOSIT_BANK");
                    updateCodepay(trans.getNickname(), true, trans.getDescription(), trans.getUserSender());
                    try {
                        SendToWS.sendBEExcEventaction(model);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    return response.toJson();
                }
                //update user money
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
                    SendToWS.sendBEExcEventaction(model);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
}
