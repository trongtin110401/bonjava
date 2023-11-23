package com.vinplay.api.processors.xulylinhtinh;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.usercore.service.impl.SecurityServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class GetUserSunWinProcess implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String numStart = request.getParameter("num");
        int numy = Integer.parseInt(numStart);
        int numz = (numy-1)*50;
        ArrayList<accsun> list_acc = new ArrayList<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("accsunwin");

        FindIterable iterable = col.find().skip(numz).limit(50);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                String username = document.getString((Object) "username");
                String password = document.getString((Object) "password");
                String Codedl = document.getString((Object) "MaDaily");
                String phone = document.getString((Object) "phone");
               accsun acc = new accsun(username,password,Codedl,phone);
                list_acc.add(acc);

            }
        });

        return list_acc.toString();
    }
}
