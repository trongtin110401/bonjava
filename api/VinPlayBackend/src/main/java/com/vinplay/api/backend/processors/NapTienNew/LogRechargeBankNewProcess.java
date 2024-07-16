package com.vinplay.api.backend.processors.NapTienNew;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositBankReponse;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class LogRechargeBankNewProcess implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");
    private static final int MAX_ITEM = 15;

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        DepositBankReponse res = new DepositBankReponse(false, "1001");
        try {
            String nickname = request.getParameter("nn");
            String bank = request.getParameter("b");
            String transId = request.getParameter("tid");
            String status = request.getParameter("st");
            String startTime = request.getParameter("ts");
            String endTime = request.getParameter("te");
            String pages = request.getParameter("p");
            String numberMax = request.getParameter("max_item");
            String checkcheck = request.getParameter("checkbank");
            int page = Integer.parseInt(pages);
            int maxItem = numberMax != null ? Integer.parseInt(numberMax) : MAX_ITEM;
            RechargeDaoImpl dao = new RechargeDaoImpl();
            DepositBankModel modelSearch = new DepositBankModel(transId, nickname, status, bank);
            if (checkcheck == null) {
                res = dao.GetListDepositBank(modelSearch, page, maxItem, startTime, endTime);
            } else if (checkcheck.equalsIgnoreCase("codepay")) {
                res = dao.GetListCodepayDes(modelSearch, page, maxItem, startTime, endTime);
            } else if (checkcheck.equalsIgnoreCase("momo")) {
//                res = dao.GetListMomoDes(modelSearch, page, maxItem, startTime, endTime);
                res = GetListMomoDes(modelSearch, page, maxItem, startTime, endTime);
            } else if (checkcheck.equalsIgnoreCase("nh")) {
                res = dao.GetListNHDes(modelSearch, page, maxItem, startTime, endTime);
            } else {
                res = dao.GetListDepositBank(modelSearch, page, maxItem, startTime, endTime);
            }

        } catch (Exception e) {
            logger.debug((Object) e);
        }
        return res.toJson();
    }

    public DepositBankReponse GetListMomoDes(DepositBankModel depositBankModel, int page, int maxItem, String fromTime, String endTime) {
        try {
            final ArrayList<DepositBankModel> records = new ArrayList<DepositBankModel>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (depositBankModel.Nickname != null && !depositBankModel.Nickname.isEmpty()) {
                String pattern = ".*" + depositBankModel.Nickname + ".*";
                conditions.put("Nickname", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }

            if (!depositBankModel.Id.isEmpty()) {
                conditions.put("Id", depositBankModel.Id);
            }
            if (!depositBankModel.BankBrandName.isEmpty()) {
                conditions.put("BankBrandName", depositBankModel.BankBrandName);
            }
            if (depositBankModel.Status > 0) {
                conditions.put("Status", depositBankModel.Status);
            }
            if (!fromTime.isEmpty() && !endTime.isEmpty()) {
                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", (Object) fromTime);
                obj.put("$lte", (Object) endTime);
                conditions.put("CreatedAt", (Object) obj);
            }
            logger.info("chua chet ow day");
            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    long amount = 0;
                    try {
                        amount = document.getLong((Object) "Amount");
                    } catch (Exception e) {
                        Long.valueOf(document.getInteger((Object) "Amount"));
                    }

                    DepositBankModel model = new DepositBankModel(
                            document.getString((Object) "Id"),
                            document.getString((Object) "Nickname"),
                            document.getString((Object) "CreatedAt"),
                            document.getString((Object) "UpdatedAt"),
                            amount,
                            document.getInteger((Object) "Status"),
                            document.getString((Object) "BankBrandName"),
                            document.getString((Object) "BankAccountNumber"),
                            document.getString((Object) "BankAccountName"),
                            document.getString((Object) "Description"),
                            document.getString((Object) "UserApprove")

                    );
                    model.setUserSender(document.getString((Object) "UserSender"));
                    records.add(model);

                }
            });
            FindIterable iterable2 = col.find((Bson) new Document(conditions));
            logger.info("chua chet ow day 2");
            iterable2.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    long amount = 0;
                    try {
                        amount = document.getLong((Object) "Amount");
                    } catch (Exception e) {
                        Long.valueOf(document.getInteger((Object) "Amount"));
                    }
                    int code = document.getInteger((Object) "Status");
                    long count = (Long) num.get(0) + 1L;
                    num.set(0, count);
                    if (code == DvtConst.STATUS_APPROVE) {
                        long numSuccess = (Long) num.get(1) + 1L;
                        num.set(1, numSuccess);
                        long moneySuccess = (Long) num.get(2) + (long) amount;
                        num.set(2, moneySuccess);
                    }
                }
            });
            logger.info("chua chet ow day 4");
            DepositBankReponse res = new DepositBankReponse(num.get(0), num.get(2), num.get(1), records);
            res.setSuccess(true);
            res.setErrorCode("0");
            return res;


        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
