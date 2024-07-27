package com.vinplay.api.processors.momo;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.entities.BankCallBack;
import com.vinplay.api.entities.BankCallBackResponse;
import com.vinplay.api.processors.Codepayok;
import com.vinplay.api.processors.cashout.GenCommentBank;
import com.vinplay.api.processors.cashout.NapRutGame;
import com.vinplay.api.processors.cashout.NapRutModel;
import com.vinplay.common.HttpCommon;
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
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class NapBankNew implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        BankCallBack bankcallback;
        BankCallBackResponse response = new BankCallBackResponse(200, "Thành công");
        String key = "HKOvdgVOc";
        String comment = request.getParameter("comment");
        String amountx = request.getParameter("amount");
        String signature = request.getParameter("signature");
        String originalInput = key + comment + amountx;
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());


        String nickname = null;

        DepositBankModel codepay = findCodepayByComment(comment);
        logger.info("erorr: xxx3 oke het roi" + comment);
        logger.info("noi dung server gui: "+ comment+ " | "+ amountx+" | "+ signature);
        if (codepay == null) {
            response.setErrorCode(500);
            response.setErrorDescription("nickname khong ton tai !!!");
            return response.toJson();
        }
        nickname = codepay.Nickname;
        if (signature.equals(encodedString) || signature.equalsIgnoreCase("fixkconS1sdd")) {
            long tien = Long.parseLong(amountx);
//                logger.info("erorr: xxx6 Vao day day"+ codepay_new.getNickname());
                UserServiceImpl service = new UserServiceImpl();
                if (codepay.getStatus() != 100) {
                    try {
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_BANK");
                        double amount = fee * tien;
                        long totalFee = Math.round(tien - amount);
                        totalFee = totalFee > 0 ? totalFee : 0;
                        service.updateMoneyFromAdmin(nickname, tien, "vin", Consts.RECHARGE_BY_BANK, "Deposit bank", "Deposit bank", totalFee);
                        logger.info("erorr: xxx7 Vao day day"+ codepay.Nickname);
                        updateMoneyCodePayMomoSun2(codepay.Id, tien);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                        historyTransDao.insertTransaction(new HistoryTransModel(codepay.BankBrandName + "|" + codepay.getDescription(), "CodePay", "recharge", amountx, "Thành công", "Nạp tiền Thành công ", codepay.Nickname, HistoryTransConst.BANK, codepay.Id));
                    }catch (Exception e) {
                        logger.info("loi update history"+ codepay.getDescription());
                    }
                    EventactionAdminObj model = new EventactionAdminObj();
                    model.setId(codepay.getId());
                    model.setStatus(100);
                    model.setType("DEPOSIT_BANK");
                    try {
                        SendToWS.sendBEExcEventaction(model);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BroadCastUserMoney.pushBroadCast(nickname);
                    BroadCastUserMoney.pushBroadTime(nickname);
                    long SoTien = tien;
                    NapRutGame nrg = new NapRutGame();
                    String codedl = nrg.getMaDaily(nickname);
                    if (codedl == null || codedl.trim().length() == 0) {

                    } else if (codedl != null && codedl.trim().equalsIgnoreCase("null") == false) {
                        NapRutModel napgame = new NapRutModel(codepay.Id, nickname, codedl, SoTien, "CodePay", codepay.CreatedAt);
                        nrg.NapRut(napgame);
                    } else {
                    }
                    return response.toJson();
                } else {
                        response.setErrorCode(500);
                        response.setErrorDescription("Sai so tien !!!");
                        return response.toJson();
                }
        } else {
            response.setErrorCode(500);
            response.setErrorDescription("Signature sai !!!");
            return response.toJson();
        }
    }

    private void updateMoneyCodePayMomoSun(String TrainID, long tien) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_codepay_manual");
            Document doc = new Document();
            doc.append("Amount", tien);
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateMoneyCodePayMomoSun2(String TrainID, long tien) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_codepay_manual");
            Document doc = new Document();
            doc.append("Amount", tien);
            doc.append("Status", 100);
            doc.append("UserApprove", "Auto Nap Bank");
            doc.append("Note1", "Thành công");
            doc.append("Note2", "Nạp tiền Thành công");
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSttCodePayMomoSun(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_codepay_manual");
            Document doc = new Document();
            doc.append("Status", 100);
            doc.append("UserApprove", "Auto Nap Bank");
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

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
            doc.append("ghiChu", "Nạp tiền Thành công");
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSttCodePayMomoSunReject(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_codepay_manual");
            Document doc = new Document();
            doc.append("Status", 2);
            doc.append("UserApprove", "Auto Nap Bank");
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSTTCodePayMomoSun2Reject(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai", "Từ chối");
            doc.append("ghiChu", "Nạp tiền Thất Bại không đủ tiền");
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HistoryTransModel GetHistorybyTransID(String TranID) {
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            HistoryTransModel his = null;

            int retry = 3;
            do {
                retry--;
                if (retry < 0) {
                    break;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"transId.keyword\":\"" + TranID + "\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":50,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_search")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }
                JSONObject obj = new JSONObject(data);
                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
                final int n = jsonArray.length();
                for (int i = 0; i < n; ++i) {
                    final JSONObject person = jsonArray.getJSONObject(i);
                    JSONObject test = person.getJSONObject("_source");
                    String nickName = test.getString("nickName");
                    String id = test.getString("id");
                    String giaodich = test.getString("giaodich");
                    String congGiaoDich = test.getString("congGiaoDich");
                    String hinhthuc = test.getString("hinhthuc");
                    String sotien = test.getString("sotien");
                    String trangthai = test.getString("trangthai");
                    String ghiChu = test.getString("ghiChu");
                    String hinhthucTrans = test.getString("hinhthucTrans");
                    String transId = test.getString("transId");
                    String createAt = test.getString("createAt");
                    his = new HistoryTransModel(giaodich, congGiaoDich, hinhthuc, sotien, trangthai, ghiChu, nickName, hinhthucTrans, transId, id, createAt);
                }

            } while (check == false);
            return his;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void InsertHistoryUserTransOK(HistoryTransModel historyTransModel, long idelk, String timeAt) {
        try {
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do {
                retry--;
                if (retry < 0) {
                    break;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.HistoryUserTransaction\",\r\n\"id\": \"" + idelk + "\",\r\n\"giaodich\": \"" + historyTransModel.getGiaodich() + "\",\r\n\"congGiaoDich\": \"" + historyTransModel.congGiaoDich + "\",\r\n\"hinhthuc\": \"" + historyTransModel.hinhthuc + "\",\r\n\"sotien\": \"" + historyTransModel.sotien + "\",\r\n\"trangthai\": \"" + historyTransModel.trangthai + "\",\r\n\"ghiChu\": \"" + historyTransModel.ghiChu + "\",\r\n\"nickName\": \"" + historyTransModel.nickName + "\",\r\n\"hinhthucTrans\": \"" + historyTransModel.hinhthucTrans + "\",\r\n\"transId\": \"" + historyTransModel.transId + "\",\r\n\"createAt\": \"" + timeAt + "\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_doc/" + idelk)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }

            } while (check == false);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Codepayok findCodepay(String codepay) {
        try {
            ArrayList<com.vinplay.api.processors.Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            conditions.put("codepay", codepay);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String nickname = document.getString((Object) "nickname");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    com.vinplay.api.processors.Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if (listcodepay.size() == 0) {
                return null;
            } else {
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
        return null;
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
            col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
    }

    public DepositBankModel FindDesploitCodepay(String trainsid) {

        ArrayList<DepositBankModel> list_nick = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("deposit_codepay_manual");
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


    public DepositBankModel findCodepayByComment(String comment) {

        ArrayList<DepositBankModel> list_nick = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("deposit_codepay_manual");
        conditions.put("Description", comment);
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

}
