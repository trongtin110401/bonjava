package com.vinplay.api.backend.processors.money;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
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
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class ApproveDepositNHProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static LinkedList<BlockByTran> listTranBlock = new LinkedList<BlockByTran>();
    @Override
    public synchronized String execute(Param<HttpServletRequest> param) {
        // type = 0 is approve
        // type = 1 is reject
//        synchronized (this) {
            BaseResponseModel response = new BaseResponseModel(false, "1001");
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            try {
                HttpServletRequest request = (HttpServletRequest) param.get();
                //check transId
                String transId = request.getParameter("transId");
                String typeStr = request.getParameter("type");
                String userApprove = request.getParameter("uad");
                String tienx = request.getParameter("tien");
                long tien = Long.parseLong(tienx);
                long tien_final = 0;
                if (transId.isEmpty() || typeStr.isEmpty()) {
                    return response.toJson();
                }
                int type = Integer.parseInt(typeStr);
                RechargeDao dao = new RechargeDaoImpl();

                // find transaction in db
                DepositBankModel trans = FindDesploitCodepay(transId);
                if (trans == null) {
                    return response.toJson();
                }
                if (trans.Status == DvtConst.STATUS_APPROVE) {
                    return response.toJson();
                }
                if(!checkBlockTran(transId)) {
                    return "Thao tác quá nhanh";
                }
                // update trans in db
                int status = type == 100 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
//                boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(transId, status, trans.Description, userApprove);
//                if (!resultUpdateTrans) {
//                    return response.toJson();
//                }
                if (type == 1) {
                    BroadCastUserMoney.pushBroadTime2(trans.Nickname);
                    response.setSuccess(true);
                    historyTransService.update(transId, trans.Nickname, HistoryTransConst.BANK, "Từ chối", "Giao dịch bị từ chối");
                    EventactionAdminObj model = new EventactionAdminObj();
                    model.setId(transId);
                    model.setStatus(2);
                    model.setType("DEPOSIT_BANK");
                    //updateCodepay(trans.Nickname, true, trans.getDescription(),trans.BankBrandName);
                    HuySttCodePayMomoSun(transId,userApprove);
                    HuySTTCodePayMomoSun2(transId);
                    try {
                        SendToWS.sendBEExcEventaction(model);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response.toJson();
                }
                //update user money
                if (type == 100) {
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double flus = GameCommon.getValueDouble("RATIO_RECHARGE_BANK_TL");
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_BANK");
                        double amount = fee * tien;
                        long totalFee = Math.round(tien - amount);
                        double tien_tmp = tien * flus;
                        tien_final = (long) tien_tmp;
                        totalFee = totalFee > 0 ? totalFee : 0;
                        response = service.updateMoneyFromAdmin(trans.Nickname, tien_final, "vin", Consts.RECHARGE_BY_BANK, "Deposit bank", "Deposit bank", totalFee);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                        historyTransDao.insertTransaction(new HistoryTransModel(trans.BankBrandName + "|" + trans.getDescription(), HistoryTransConst.CHUYEN_KHOAN, "Nạp tiền", tien+"", "Thành công", "Nạp tiền Thành công ", trans.Nickname, HistoryTransConst.BANK, trans.Id));
                    }catch (Exception e) {
                    }
                    updateMoneyCodePayMomoSun(transId,tien_final);
                    updateMoneyCodePayMomoSun2(transId, tien_final+"");
                    updateSttCodePayMomoSun(transId,userApprove);
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
                }
                BroadCastUserMoney.pushBroadCast(trans.Nickname);
                BroadCastUserMoney.pushBroadTime(trans.Nickname);
                //updateCodepay(trans.Nickname, true, trans.getDescription(),trans.BankBrandName);
                updateMoneyCodePayMomoSun(transId,tien);
                updateMoneyCodePayMomoSun2(transId, tien+"");
                NapRutGame nrg = new NapRutGame();
                String codedl = nrg.getMaDaily(trans.Nickname);
                long SoTien = tien;
                if(codedl == null){
                    int xx = 2;
                }else if(codedl != null && codedl.trim().length() == 0){
                    int xx = 2;
                }else if(codedl != null && codedl.trim().equalsIgnoreCase("null") == false){
                        NapRutModel napgame = new NapRutModel(trans.Id, trans.Nickname, codedl, SoTien,"Bank", trans.CreatedAt);
                        nrg.NapRut(napgame);
                }else{
                    int xx =2;
                }

                return response.toJson();
                //send to user

                //

            } catch (Exception e) {
                return response.toJson();
            }

//        }
    }

    public boolean checkBlockTran(String transid) {
        try {
            if (!listTranBlock.isEmpty() && listTranBlock.size() > 300) {
                System.out.println("Đẩy 1 phần tử ra ngoài: " + listTranBlock.pop());
            }
            for(BlockByTran object : listTranBlock) {
                if (object.transid.equals(transid)) {
                    long curentTimeStamp = new Date().getTime();
                    long difftime = curentTimeStamp - object.timeStamp;
                    if (difftime < 20000) {
                        return false;
                    } else {
                        object.timeStamp = new Date().getTime();
                        return true;
                    }
                }
            }
            BlockByTran blockByTran = new BlockByTran();
            blockByTran.transid = transid;
            listTranBlock.add(blockByTran);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private void updateMoneyCodePayMomoSun(String TrainID, long tien) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_nh_manual");
            Document doc = new Document();
            doc.append("Amount",tien);
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateMoneyCodePayMomoSun2(String TrainID, String tien) {
        try {
            InsertELK elk = new InsertELK();
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

    private void updateSttCodePayMomoSun(String TrainID, String ua) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_nh_manual");
            Document doc = new Document();
            doc.append("Status",100);
            doc.append("UserApprove",ua);
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
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));
            HistoryTransModel his = elk.GetHistorybyTransID(TrainID);
            his.setTrangthai("Thành công");
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()),his.getCreateAt());

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateCodepay(String nickname, boolean use, String codepay, String bankname){
        try {
            int check = 0;
            if(use == true){
                check = 1;
            }else{
                check = 0;
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("codepay", codepay);
            doc.append("use", check);
            doc.append("createAt",timeAt);
            doc.append("bankname", bankname);
            col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public DepositBankModel FindDesploitCodepay(String trainsid){
        try {
            ArrayList<DepositBankModel> list_nick = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_nh_manual");
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

    private void HuySttCodePayMomoSun(String TrainID, String ua) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_nh_manual");
            Document doc = new Document();
            doc.append("Status",2);
            doc.append("UserApprove",ua);
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void HuySTTCodePayMomoSun2(String TrainID) {
        try {
            InsertELK elk = new InsertELK();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai","Từ chối");
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));
            HistoryTransModel his = elk.GetHistorybyTransID(TrainID);
            his.setTrangthai("Từ chối");
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()),his.getCreateAt());

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    class BlockByTran {
        public String transid = "";
        public long timeStamp = new Date().getTime();
    }
}

