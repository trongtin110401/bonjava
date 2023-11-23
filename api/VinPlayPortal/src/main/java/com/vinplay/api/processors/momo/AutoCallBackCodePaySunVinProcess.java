package com.vinplay.api.processors.momo;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.entities.BankCallBack;
import com.vinplay.api.entities.BankCallBackResponse;
import com.vinplay.api.processors.cashout.NapRutGame;
import com.vinplay.api.processors.cashout.NapRutModel;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.InsertELK;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class AutoCallBackCodePaySunVinProcess implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public synchronized String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        BankCallBack bankcallback;
        BankCallBackResponse response = new BankCallBackResponse(200, "Thành công");
        String tranID = request.getParameter("tranID");
        String keyID = request.getParameter("keyID");
        String phoneAccount = request.getParameter("phoneAccount");
        String phoneCustomer = request.getParameter("phoneCustomer");
        String nameCustomer = request.getParameter("nameCustomer");
        String comment = request.getParameter("comment");
        String mamount = request.getParameter("amount");
        String type = request.getParameter("type");
        String money = request.getParameter("money");
        String signature = request.getParameter("signature");
        String status = request.getParameter("status");
        RechargeDao dao = new RechargeDaoImpl();
        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        DepositBankModel trans = FindDesploitCodepay(keyID);
        if(status.equalsIgnoreCase("2") == true & trans.getStatus() != 100 & trans.getStatus() != 2){
            //boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(keyID, DvtConst.STATUS_REJECT, "giao dịch bị hủy", "Nap Bank Auto");
            //updateSttCodePayMomoSunThatBai(keyID);
            historyTransService.update(keyID, trans.Nickname, HistoryTransConst.BANK, " Từ Chối", " giao dịch bị hủy");
            updateSttCodePayMomoSunThatBai(trans.Id);
            updateSTTCodePayMomoSun2ThatBai(trans.Id);
            response.setErrorCode(200);
            response.setErrorDescription("Hủy Đơn thành công");
            return response.toJson();
        }

        long tien = Long.parseLong(mamount);
        long tien_final = 0;

        bankcallback = new BankCallBack(tranID,keyID,phoneAccount,phoneCustomer,nameCustomer,comment,mamount,type,money,signature);

        // find transaction in db
        synchronized (this){


            if (trans == null) {
                response.setErrorCode(500);
                response.setErrorDescription("Không tồn tại transaction Id");
                return response.toJson();
            }

            if(trans.getStatus() == 100){
                response.setErrorCode(500);
                response.setErrorDescription("Từ chối callback. Giao dịch đã xử lý trước đó!!!");
                return response.toJson();
            }


            if (verifySignature(bankcallback)){
                // update trạng thái thành công
                if (trans.getStatus() !=100) { // đúng mới cộng tiền
                    //boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(bankcallback.getKeyID(), DvtConst.STATUS_APPROVE, "Comment: "+trans.getDescription()+", Tien: "+tien, "Nap Bank Auto");
                    historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, "Thành công", " giao dịch thành công");
                    updateSttCodePayMomoSun(trans.Id);
                    updateSTTCodePayMomoSun2(trans.Id);
//                    if (!resultUpdateTrans) {
//                        response.setErrorCode(500);
//                        response.setErrorDescription("Cập nhật thất bại");
//                        return response.toJson();
//                    }
                    // cộng tiền
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double flus = GameCommon.getValueDouble("RATIO_RECHARGE_BANK_TL");
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
                        double amount = fee * tien;
                        long totalFee = Math.round(tien - amount);
                        double tien_tmp = tien * flus;
                        tien_final = (long) tien_tmp;
                        totalFee = totalFee > 0 ? totalFee : 0;
                        service.updateMoneyFromAdmin(trans.Nickname, tien_final, "vin",
                                Consts.RECHARGE_BY_BANK, "Nạp Bank",
                                "nạp Bank tự động", totalFee);
                    }catch (Exception e) {
                        response.setErrorCode(500);
                        response.setErrorDescription(""+e);
                        return response.toJson();
                    }
                    updateMoneyCodePayMomoSun(trans.Id,tien_final);
                    updateMoneyCodePayMomoSun2(trans.Id, tien_final+"");
                }


            }else{
                response.setErrorCode(500);
                response.setErrorDescription("Signature sai!");
                return response.toJson();
            }

            BroadCastUserMoney.pushBroadCast(trans.Nickname);
            updateMoneyCodePayMomoSun(trans.Id,tien);
            updateMoneyCodePayMomoSun2(trans.Id, tien+"");
            NapRutGame nrg = new NapRutGame();
            String codedl = nrg.getMaDaily(trans.Nickname);
            long SoTien = tien;
            if(codedl == null){
                int xx = 2;
            }else if(codedl != null && codedl.trim().length() == 0){
                int xx = 2;
            }else if(codedl != null && codedl.trim().equalsIgnoreCase("null") == false){
                String usend = trans.getUserSender();
                    NapRutModel napgame = new NapRutModel(trans.Id, trans.Nickname, codedl, SoTien,"MoMo", trans.CreatedAt);
                    nrg.NapRut(napgame);

            }else{
                int xx =2;
            }
            return response.toJson();

        }
//        return response.toJson();

    }
    private boolean verifySignature(BankCallBack bankcallback) {

        String accessToken = "DL5LdXp6r71Nq0YKccM5jAyMS0xMi0xMiAxNDoxODowNg==";
        String key = "22a7b2091adfac0a0bb4a4b6a2a0028";
        String builderContent = bankcallback.getTranID() + "|" + bankcallback.getAmount() + "|" + bankcallback.getComment() + "|" + key + "|" + accessToken;
        String myHash = "";
        try {
            myHash = VinPlayUtils.getMD5Hash(builderContent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (myHash.equals(bankcallback.getSignature())) {
            return true;
        } else {
            return false;
        }
    }

    private void updateMoneyCodePayMomoSun(String TrainID, long tien) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            Document doc = new Document();
            doc.append("Amount",tien*1.0);
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
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            Document doc = new Document();
            doc.append("Status",100);
            doc.append("UserApprove","Nap Bank Auto");
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSttCodePayMomoSunThatBai(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            Document doc = new Document();
            doc.append("Status",2);
            doc.append("UserApprove","Nap Bank Auto");
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSTTCodePayMomoSun2(String TrainID) {
        try {
            InsertELK elk = new InsertELK();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai","Thành công");
            doc.append("ghiChu","Nạp tiền Thành công");
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));
            HistoryTransModel his = elk.GetHistorybyTransID(TrainID);
            his.setTrangthai("Thành công");
            his.setGhiChu("Nạp tiền Thành công");
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()),his.getCreateAt());

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSTTCodePayMomoSun2ThatBai(String TrainID) {
        try {
            InsertELK elk = new InsertELK();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai","Từ chối");
            doc.append("ghiChu","Giao dịch bị từ chối");
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));
            HistoryTransModel his = elk.GetHistorybyTransID(TrainID);
            his.setTrangthai("Từ chối");
            his.setGhiChu("Giao dịch bị từ chối");
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()),his.getCreateAt());

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DepositBankModel FindDesploitCodepay(String trainsid){
        try {
            ArrayList<DepositBankModel> list_nick = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            conditions.put("Id", trainsid);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String Id = document.getString((Object) "Id");
                    String Nickname = document.getString((Object) "Nickname");
                    String CreatedAt = document.getString((Object) "CreatedAt");
                    String UpdatedAt = document.getString((Object) "UpdatedAt");
                    long Amount = document.getLong((Object) "Amount");
                    int Status = document.getInteger((Object) "Status");
                    String BankBrandName = document.getString((Object) "BankBrandName");
                    String BankAccountNumber = document.getString((Object) "BankAccountNumber");
                    String BankAccountName = document.getString((Object) "BankAccountName");
                    String Description = document.getString((Object) "Description");
                    String UserApprove = document.getString((Object) "UserApprove");
                    String UserSender = document.getString((Object) "UserSender");
                    DepositBankModel desp = new DepositBankModel(Id, Nickname, CreatedAt, UpdatedAt, Amount, Status, BankBrandName, BankAccountNumber, BankAccountName, Description);
                    list_nick.add(desp);
                }
            });
            if(list_nick.size() == 0){
                return null;
            }else{
                return list_nick.get(0);
            }

        }catch (Exception e){
            return null;
        }
    }
}
