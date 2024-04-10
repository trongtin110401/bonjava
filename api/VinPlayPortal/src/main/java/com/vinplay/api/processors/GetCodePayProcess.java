package com.vinplay.api.processors;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.processors.cashout.GenCommentBank;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.utils.TelegramUtil;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class GetCodePayProcess implements BaseProcessor<HttpServletRequest, String> {


    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String bank = request.getParameter("bank");
            String bankAcc = request.getParameter("cardName");
            String bankNum = request.getParameter("cardCode");
            String accessToken = request.getParameter("at");
            String nickName = this.getUserNameByAccessToken(accessToken);
            TelegramUtil telegramUtil = new TelegramUtil();
            telegramUtil.sendMessageNap(nickName + " Thực hiện nạp tiền qua Bank");
            String bankcode = "";
            if (bank.equalsIgnoreCase("techcombank")) {
                bankcode = "10040";
            } else if (bank.equalsIgnoreCase("Vietinbank")) {
                bankcode = "10020";
            } else if (bank.equalsIgnoreCase("Vietcombank")) {
                bankcode = "10010";
            } else if (bank.equalsIgnoreCase("BIDV")) {
                bankcode = "10050";
            } else if (bank.equalsIgnoreCase("ACB")) {
                bankcode = "10060";
            } else if (bank.equalsIgnoreCase("MBBank") || bank.equalsIgnoreCase("MB Bank")) {
                bankcode = "10030";
            } else {
                bank = "";
                bankcode = "10010";
            }
            String TranID = String.valueOf(VinPlayUtils.generateTransId());
            String originalInput = "{\"cardName\":\"" + bankAcc + "\",\"cardCode\":\"" + bankNum + "\"}";
            String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
            Long time_check = new Date().getTime();
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //dit cu viet lai het
            DepositBankModel codepay3 = findCodepayByNickname(nickName);
//                Codepayok codepay3 = gen.findNickname(nickName);
            GenCommentBank gen = new GenCommentBank();
            String commentcode = "LX" + gen.randomMaChuyenTien().toUpperCase();
            if (codepay3 == null) {
                if (bank.isEmpty()) {
                    String resp = "{\"errorCode\":300}";
                    return resp;
                }
                String dataall = "CodePay" + "|" + bank + "|" + bankAcc + "|" + TranID + "|" + commentcode;
                RechargeServiceImpl reg = new RechargeServiceImpl();
                reg.rechargeByBankManual2(nickName, 1, bankNum, dataall);
                return "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"infomationAccount\":\"" + encodedString + "\",\"comment\":\"" + commentcode + "\",\"qrcode\":\"null\",\"type\":\"Bank\",\"bankCode\":\"" + bankcode + "\",\"TrainID\": \"" + TranID + "\",\"timecon\":7200,\"bankname\":\"" + "createnew"+nickName+ "\"}";
            } else {
                Date out = sim.parse(codepay3.getCreatedAt());
                Long timelog = out.getTime();
                Long time_end = time_check - timelog;
                if (time_end <= 7200000) {
                    Long time_con =7200- time_end / 1000;
                    commentcode = codepay3.getDescription();
                    String resp = "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"infomationAccount\":\"" + encodedString + "\",\"comment\":\"" + commentcode + "\",\"qrcode\":\"null\",\"type\":\"Bank\",\"bankCode\":\"" + bankcode + "\",\"TrainID\": \"" + codepay3.Id + "\",\"timecon\":" + time_con + ",\"bankname\":\"" + codepay3.BankBrandName + "\"}";
                    return resp;
                } else {
                    //huy codepay cu
                    RechargeDao dao = new RechargeDaoImpl();
                    HistoryTransService historyTransService = new HistoryTransServiceImpl();
                    updateCodepayAAA(codepay3.Nickname, true, codepay3.getDescription(),codepay3.BankBrandName);
                    BroadCastUserMoney.pushBroadTime2(codepay3.Nickname);
                    boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(codepay3.Id, DvtConst.STATUS_REJECT, "", "User huy giao dich");
                    huyCodepay(codepay3.Id);
                    //historyTransService.update(codepay3.Id, codepay3.Nickname, HistoryTransConst.BANK, " Từ Chối", " giao dịch bị hủy");
                    if (bank.isEmpty()) {
                        String resp = "{\"errorCode\":300}";
                        return resp;
                    }
                    String dataall = "CodePay" + "|" + bank + "|" + bankAcc + "|" + TranID + "|" + commentcode;
                    RechargeServiceImpl reg = new RechargeServiceImpl();
                    reg.rechargeByBankManual2(nickName, 1, bankNum, dataall);
                    return "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"infomationAccount\":\"" + encodedString + "\",\"comment\":\"" + commentcode + "\",\"qrcode\":\"null\",\"type\":\"Bank\",\"bankCode\":\"" + bankcode + "\",\"TrainID\": \"" + TranID + "\",\"timecon\":7200,\"bankname\":\"" + bank + "\"}";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
    public DepositBankModel findCodepayByNickname(String nickname) {

        ArrayList<DepositBankModel> list_nick = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("deposit_codepay_manual");
        conditions.put("Nickname", nickname);
        conditions.put("Status", 1);
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
                desp.setUserSender(UserSender);
                desp.UserApprove = UserApprove;
                list_nick.add(desp);
            }
        });
        if (list_nick.size() == 0) {
            return null;
        } else {
            return list_nick.get(0);
        }
    }

    public void updateCodepayAAA(String nickname, boolean use, String codepay, String bankname) {
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
            col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
    }
    private void huyCodepay(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_codepay_manual");
            Document doc = new Document();
            doc.append("Status", 2);
            doc.append("UserApprove", "Auto Nap Bank");
            doc.append("Note1", "Hủy quá hạn");
            doc.append("Note2", "Hủy do quá hạn");
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
