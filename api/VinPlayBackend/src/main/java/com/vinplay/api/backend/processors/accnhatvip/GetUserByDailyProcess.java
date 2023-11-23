package com.vinplay.api.backend.processors.accnhatvip;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.backend.processors.accsunwin.account;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class GetUserByDailyProcess implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String codedl = request.getParameter("codedaily");
        ArrayList<com.vinplay.api.backend.processors.accsunwin.account> list_acc = new ArrayList<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("accnhatvip");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("MaDaily", codedl);
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                String username = document.getString((Object) "username");
                String password = document.getString((Object) "password");
                String MaDaily = document.getString((Object) "MaDaily");
                String phone = document.getString((Object) "phone");
                boolean dangky = document.getBoolean((Object) "dangky");
                String timelog = document.getString((Object) "timelog");
                String nickname = document.getString((Object) "nickname");
                String tien = document.getString((Object) "tien");
                com.vinplay.api.backend.processors.accsunwin.account acc = new account(username, password, MaDaily, phone, dangky,timelog,nickname,tien);
                list_acc.add(acc);

            }
        });

        Gson gson = new Gson();
        return gson.toJson(list_acc);
    }
}

