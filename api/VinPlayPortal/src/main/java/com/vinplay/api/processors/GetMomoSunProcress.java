package com.vinplay.api.processors;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.processors.cashout.GenCommentBank;
import com.vinplay.api.processors.cashout.NapSunVinBankMomo;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.BankPartnerModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GetMomoSunProcress implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");
    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            Gson gson = new Gson();
            String accessToken = request.getParameter("at");
            RechargeServiceImpl rechargeService = new RechargeServiceImpl();
            String nickname = this.getUserNameByAccessToken(accessToken);
            DepositBankModel depositBankModel = rechargeService.finMoMoDeposit(nickname);
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(depositBankModel != null) {
                Date currentDate = new Date();
                Date dateCreate = sim.parse(depositBankModel.getCreatedAt());
//                Long time_end = Math.abs(time_check - timelog);
                Long time_con = (currentDate.getTime() - dateCreate.getTime())/1000;
                if(time_con > 720) {
                    //qua thoi gian
                    rechargeService.cancelMomoById(depositBankModel.getId());
                }else {
                    //con han
                    long real_con = 720-time_con;
                    String respx = "{ \"comment\": \""+depositBankModel.Description+"\", \"TrainID\": \""+depositBankModel.Id+"\", \"phoneNum\": \""+depositBankModel.BankAccountNumber+"\", \"phoneName\": \""+depositBankModel.BankAccountName+"\", \"timeToExpired\": "+real_con+", \"amount\": 1 }";
                    return respx;
                }
            }else {
            }
            NapSunVinBankMomo napsun = new NapSunVinBankMomo();
            String TranID = String.valueOf(VinPlayUtils.generateTransId());
            BankPartnerModel requestTaoCode = napsun.sendBenThuBaTaoCodePay("momo", "momo", 1, TranID);
            logger.info("tao code "+ gson.toJson(requestTaoCode));
            String dataall = "Momo" + "|" + "momo" + "|" + requestTaoCode.phoneName + "|" + TranID + "|" + requestTaoCode.code;
            logger.info("tao code "+ dataall);
            RechargeServiceImpl reg = new RechargeServiceImpl();
            reg.rechargeByBankManual2(nickname, 1, requestTaoCode.phoneNum, dataall);
            String respx = "{ \"comment\": \""+requestTaoCode.code+"\", \"TrainID\": \""+TranID+"\", \"phoneNum\": \""+requestTaoCode.phoneNum+"\", \"phoneName\": \""+requestTaoCode.phoneName+"\", \"timeToExpired\": "+720+", \"amount\": 1 }";
            return respx;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

    public boolean GetHistorybynickname2(String nickname, String comment) {
        try {
            ArrayList<String> list_nick = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            conditions.put("Nickname", nickname);
            conditions.put("Description", comment);
            conditions.put("Status", 1);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String Id = document.getString((Object) "Id");
                    list_nick.add(Id);
                }
            });
            if (list_nick.size() == 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    public boolean GetHistorybynickname3(String nickname, String comment) {
        try {
            ArrayList<String> list_nick = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            conditions.put("Nickname", nickname);
            conditions.put("Status", 1);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String Id = document.getString((Object) "Id");
                    list_nick.add(Id);
                }
            });
            if (list_nick.size() == 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }
}
