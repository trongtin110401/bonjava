package com.vinplay.api.processors.AutoXuLyBank;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.InsertELK;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class NganHangProcess implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String transId = null;
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();

            System.out.println("body nap bank sunvin: " + body);
            if (body.contains("\"ResponseCode\":1")) {
                JSONObject obj = new JSONObject(body);
                String contentStr = obj.getString("ResponseContent");
                JSONObject jsonObject = new JSONObject(contentStr);
                transId = jsonObject.getString("RefCode");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String userApprove = "Auto Bank Tool";
        long tien_final = 0;
        RechargeDao dao = new RechargeDaoImpl();
        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        // find transaction in db
        DepositBankModel trans = dao.FindDepositBankById(transId);
        if(trans == null){
            InsertELK elk = new InsertELK();
            HistoryTransModel his = elk.GetHistorybyTransID(transId);
            his.setTrangthai("Từ chối");
            his.setGhiChu("Giao dịch bị từ chối");
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()),his.getCreateAt());
            return "1ok";
        }else{
            if(trans.getStatus() == 100){
                InsertELK elk = new InsertELK();
                MongoDatabase db = MongoDBConnectionFactory.getDB();
                MongoCollection col = db.getCollection("History_User_transaction");
                Document doc = new Document();
                doc.append("trangthai","Thành công");
                doc.append("ghiChu","Duyet lai thanh cong");
                col.updateOne((Bson) new Document("transId", transId), (Bson) new Document("$set", (Object) doc));
                HistoryTransModel his = elk.GetHistorybyTransID(transId);
                his.setTrangthai("Thành công");
                his.setGhiChu("Duyet lai thanh cong");
                elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()),his.getCreateAt());
                return "1ok";
            }else if(trans.getStatus() == 2){
                updateSTT2(transId);
                return "1ok";
            }else{
                BroadCastUserMoney.pushBroadTime2(trans.Nickname);
                historyTransService.update(transId, trans.Nickname, HistoryTransConst.BANK, "Từ chối", "Giao dịch bị từ chối");
                EventactionAdminObj model = new EventactionAdminObj();
                model.setId(transId);
                model.setStatus(2);
                model.setType("DEPOSIT_BANK");
                try {
                    SendToWS.sendBEExcEventaction(model);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BroadCastUserMoney.pushBroadCast(trans.Nickname);
                updateStt(transId, userApprove);
                updateSTT2(transId);
                return "1ok";
            }

        }



    }
    private void updateStt(String TrainID, String userApprove) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_bank_manual");
            Document doc = new Document();
            doc.append("Status",2);
            doc.append("UserApprove",userApprove);
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void updateSTT2(String TrainID) {
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
}

