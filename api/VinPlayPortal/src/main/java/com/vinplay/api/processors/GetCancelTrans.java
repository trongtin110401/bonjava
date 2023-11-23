package com.vinplay.api.processors;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class GetCancelTrans implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String transID = request.getParameter("transID");
            //RechargeDao dao = new RechargeDaoImpl();
            DepositBankModel trans = FindDesploitNH(transID);
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            if (trans == null) {
                return "0";
            }else{
                if(trans.getStatus() == 100 || trans.getStatus() == 2){
                    return "0";
                }else{
                    //updateCodepay(trans.Nickname, true, trans.getDescription(),trans.BankBrandName);
                    //BroadCastUserMoney.pushBroadTime2(trans.Nickname);
//                    boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(transID, DvtConst.STATUS_REJECT, "", "User huy giao dich");
                    updateSttCodePayMomoSun(transID,"User huy giao dich");
                    historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, " Từ Chối", " giao dịch bị hủy");
                    return "1";
                }
            }


        }catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
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

    private void updateSttCodePayMomoSun(String TrainID, String ua) {
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

    public DepositBankModel FindDesploitNH(String trainsid){
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

}
