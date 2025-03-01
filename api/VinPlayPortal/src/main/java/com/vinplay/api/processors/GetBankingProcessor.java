package com.vinplay.api.processors;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.processors.RutBankSTK.InfoBankEnity;
import com.vinplay.api.processors.cashout.GenCommentBank;
import com.vinplay.api.processors.momo.GenBank;
import com.vinplay.bank.AutoBankClient;
import com.vinplay.bank.BankGenTrainID;
import com.vinplay.common.HttpCommon;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.python.parser.ast.Str;
import scala.Array;
import scala.Int;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;


public class GetBankingProcessor implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String bank = request.getParameter("bank");
            String amount = request.getParameter("amount");
            String comment_code = request.getParameter("content");
            String sender = request.getParameter("userSend");
            String banknum = request.getParameter("banknum");
            String accessToken = request.getParameter("at");
            String userName = this.getUserNameByAccessToken(accessToken);
            int amountx = Integer.parseInt(amount);
            long tieen = Long.parseLong(amount);

            sender = sender.replaceAll("_"," ");
            comment_code = comment_code.replaceAll("_"," ");

            HistoryTransModel hiss = null;
            boolean check_don_ton_dai = GetHistorybynickname2(userName);
            if(check_don_ton_dai == true){
                if(amountx < 50000){
                    return "0";
                }else{
                    GenCommentBank gencmt = new GenCommentBank();
                    if(comment_code == null){
                        comment_code = gencmt.GenContent(userName);
                    }

                    AutoBankClient au = new AutoBankClient();
                    String TransID = String.valueOf(VinPlayUtils.generateTransId());
                    String bankcode = "";
                    if(bank.equalsIgnoreCase("techcombank")){
                        bankcode = "10040";
                    }else if(bank.equalsIgnoreCase("Vietinbank")){
                        bankcode = "10020";
                    }else if(bank.equalsIgnoreCase("Vietcombank")){
                        bankcode = "10010";
                    }else if(bank.equalsIgnoreCase("BIDV")){
                        bankcode = "10050";
                    }else if(bank.equalsIgnoreCase("ACB")){
                        bankcode = "10060";
                    }else if(bank.equalsIgnoreCase("MBBank") || bank.equalsIgnoreCase("MB Bank")){
                        bankcode = "10030";
                    }else{
                        bankcode = "error";
                    }
                    String dataall = sender + "|" + bank + "|" + "napBank" + "|" + TransID + "|" + comment_code;
                    RechargeServiceImpl reg = new RechargeServiceImpl();
                    reg.rechargeByBankManual2(userName, tieen, banknum, dataall);
                    String response_final = "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"comment\":\""+comment_code+"\",\"qrcode\":\"xxxxx\",\"amount\":"+amountx+",\"type\":\"Bank\",\"bankCode\":\""+bankcode+"\",\"TrainID\":"+TransID+"}";

                    return response_final;
            }


            }else{
                return "{\"errorCode\":300}";
            }


        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

    public HistoryTransModel GetHistorybynickname(String nickname){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            HistoryTransModel his = null;
//            ArrayList<HistoryTransModel> list_his = new ArrayList<>();
//
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    break;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\""+nickname+"\"}},{\"match\":{\"congGiaoDich.keyword\":\"Ngân Hàng\"}},{\"match\":{\"trangthai.keyword\":\"Đang xử lý\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":1000,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                if(n == 0){
//                    return null;
//                }else{
//                    for (int i = 0; i < n; ++i) {
//                        final JSONObject person = jsonArray.getJSONObject(i);
//                        JSONObject test = person.getJSONObject("_source");
//                        String nickName = test.getString("nickName");
//                        String id = test.getString("id");
//                        String giaodich = test.getString("giaodich");
//                        String congGiaoDich = test.getString("congGiaoDich");
//                        String hinhthuc = test.getString("hinhthuc");
//                        String sotien = test.getString("sotien");
//                        String trangthai = test.getString("trangthai");
//                        String ghiChu = test.getString("ghiChu");
//                        String hinhthucTrans = test.getString("hinhthucTrans");
//                        String transId = test.getString("transId");
//                        String createAt = test.getString("createAt");
//                        HistoryTransModel hisx = new HistoryTransModel(giaodich, congGiaoDich, hinhthuc, sotien, trangthai, ghiChu, nickName, hinhthucTrans, transId, id, createAt);
//                        list_his.add(hisx);
//                    }
//                }
//
//
//            }while (check == false);
//            return list_his.get(0);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return null;

    }


    public boolean GetHistorybynickname2(String nickname){
        try {
            ArrayList<String> list_nick = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_nh_manual");
            conditions.put("Nickname", nickname);
            conditions.put("Status", 1);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String Id = document.getString((Object) "Id");
                    list_nick.add(Id);
                }
            });
            if(list_nick.size() == 0){
                return true;
            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }
    }


}

