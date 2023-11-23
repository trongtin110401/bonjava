package com.vinplay.api.dlmuaban;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowLichSuBanTienProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String accessToken = request.getParameter("at");
        String nickname = this.getUserNameByAccessToken(accessToken);

        ArrayList<BanTienEnity> listls = new ArrayList<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("BanTienChoDL");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        conditions.put("nickname", nickname);
        FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                ObjectId id = document.getObjectId("_id");
                String trainID = document.getString("trainID");
                String bank_nhan = document.getString("bank_nhan");
                String stk_nhan = document.getString("stk_nhan");
                String name_nhan = document.getString("name_nhan");
                int tien = document.getInteger("tien");
                String trangthai = document.getString("trangthai");
                String sttcode = document.getString("sttcode");
                String nickname = document.getString("nickname");
                String nickname_dl = document.getString("nickname_dl");
                Long dlid = document.getLong("dlid");
                String timelog = document.getString("timelog");
                String note = document.getString("note");
                BanTienEnity ban = new BanTienEnity(id.toString(), trainID, bank_nhan, stk_nhan, name_nhan, tien, trangthai, sttcode, nickname, nickname_dl,dlid, timelog, note);
                listls.add(ban);
            }
        });
        Gson gson = new Gson();
        return gson.toJson(listls);
    }
    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
