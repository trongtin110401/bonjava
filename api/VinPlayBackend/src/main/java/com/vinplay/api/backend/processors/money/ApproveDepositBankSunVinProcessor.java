package com.vinplay.api.backend.processors.money;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.backend.processors.cashout.NapRutGame;
import com.vinplay.api.backend.processors.cashout.NapRutModel;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.lognaprut.impl.InsertELK;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.statics.Consts;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// todo : approve tiền nạp qua ngân hàng cho user
public class ApproveDepositBankSunVinProcessor implements BaseProcessor<HttpServletRequest, String> {

    @Override
    public String execute(Param<HttpServletRequest> param) {
        // type = 0 is approve
        // type = 1 is reject
        synchronized (this) {
            BaseResponseModel response = new BaseResponseModel(false, "1001");
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            try {
                HttpServletRequest request = (HttpServletRequest) param.get();
                //check transId
                String transId = request.getParameter("transId");
                String typeStr = request.getParameter("type");
                String userApprove = request.getParameter("uad");
                long tien_final = 0;
                if (transId.isEmpty() || typeStr.isEmpty()) {
                    return response.toJson();
                }
                int type = Integer.parseInt(typeStr);
                RechargeDao dao = new RechargeDaoImpl();

                // find transaction in db
                DepositBankModel trans = dao.FindDepositBankById(transId);
                if (trans == null) {
                    return response.toJson();
                }
                if (trans.Status != DvtConst.STATUS_PENDING) {
                    return response.toJson();
                }
                // update trans in db
                int status = type == 0 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
                boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(transId, status, trans.Description, userApprove);
                if (!resultUpdateTrans) {
                    return response.toJson();
                }
                if (type == 1) {
                    response.setSuccess(true);
                    historyTransService.update(transId, trans.Nickname, HistoryTransConst.BANK, "Từ chối", trans.Description);
                    EventactionAdminObj model = new EventactionAdminObj();
                    model.setId(transId);
                    model.setStatus(2);
                    model.setType("DEPOSIT_BANK");
                    try {
                        SendToWS.sendBEExcEventaction(model);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response.toJson();
                }
                //update user money
                if (type == 0) {
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double flus = GameCommon.getValueDouble("RATIO_RECHARGE_BANK_TL");
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
                        double amount = fee * trans.Amount;
                        long totalFee = Math.round(trans.Amount - amount);
                        double tien_tmp = trans.Amount * flus;
                        tien_final = (long) tien_tmp;
                        totalFee = totalFee > 0 ? totalFee : 0;
                        response = service.updateMoneyFromAdmin(trans.Nickname, tien_final, "vin", Consts.RECHARGE_BY_BANK, "Deposit bank", "Deposit bank", totalFee);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                    historyTransDao.insertTransaction(new HistoryTransModel(trans.BankBrandName + "|" + trans.getDescription(), "CodePay", "Nạp tiền", trans.Amount+"", "Thành công", "Nạp tiền Thành công ", trans.Nickname, HistoryTransConst.BANK, trans.Id));
                    updateSttCodePayMomoSun(transId);
                    updateSTTCodePayMomoSun2(transId,trans.Description);
                    EventactionAdminObj model = new EventactionAdminObj();
                    model.setId(transId);
                    model.setStatus(100);
                    model.setType("DEPOSIT_BANK");
                    try {
                        SendToWS.sendBEExcEventaction(model);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateMoneyCodePayMomoSun(transId,tien_final);
                    updateMoneyCodePayMomoSun2(transId, tien_final+"");
                }
                BroadCastUserMoney.pushBroadCast(trans.Nickname);
                updateMoneyCodePayMomoSun(transId,trans.Amount);
                updateMoneyCodePayMomoSun2(transId, trans.Amount+"");
                NapRutGame nrg = new NapRutGame();
                String codedl = nrg.getMaDaily(trans.Nickname);
                long SoTien = trans.Amount;
                if(codedl == null){
                    int xx = 2;
                }else if(codedl != null && codedl.trim().length() == 0){
                    int xx = 2;
                }else if(codedl != null && codedl.trim().equalsIgnoreCase("null") == false){
                    String usend = trans.getUserSender();

                    if(usend.equalsIgnoreCase("CodePay")){
                        NapRutModel napgame = new NapRutModel(trans.Id, trans.Nickname, codedl, SoTien,"CodePay", trans.CreatedAt);
                        nrg.NapRut(napgame);
                    }else if(usend.equalsIgnoreCase("Momo")){
                        NapRutModel napgame = new NapRutModel(trans.Id, trans.Nickname, codedl, SoTien,"MoMo", trans.CreatedAt);
                        nrg.NapRut(napgame);
                    }else{
                        NapRutModel napgame = new NapRutModel(trans.Id, trans.Nickname, codedl, SoTien,"Bank", trans.CreatedAt);
                        nrg.NapRut(napgame);
                    }
                }else{
                    int xx =2;
                }

                return response.toJson();
                //send to user

                //

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
            doc.append("Amount",tien);
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateMoneyCodePayMomoSun2(String TrainID, String tien) {
        InsertELK elk = new InsertELK();
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("sotien",tien);
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));
            HistoryTransModel his = elk.GetHistorybyTransID(TrainID);
            his.setSotien(tien);
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()),his.getCreateAt());

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSttCodePayMomoSun(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_bank_manual");
            Document doc = new Document();
            doc.append("Status",100);
            doc.append("UserApprove","Nap Bank Auto");
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSTTCodePayMomoSun2(String TrainID, String comment) {
        try {
            InsertELK elk = new InsertELK();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai","Thành công");
            doc.append("ghiChu",comment);
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));
            HistoryTransModel his = elk.GetHistorybyTransID(TrainID);
            his.setTrangthai("Thành công");
            his.setGhiChu(comment);
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()),his.getCreateAt());

        }catch (Exception e) {
            e.printStackTrace();
        }

    }


}
