package com.vinplay.api.processors.AutoXuLyBank;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckNap {
    public long tongnapThe(String nickname){
        long tong = 0;
        ArrayList<checknapEnity> listtien = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("NapRutGame");
        conditions.put("Nickname", nickname);
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                long sotien = document.getLong((Object) "SoTien");
                String HinhThucTran = document.getString((Object) "HinhThucTran");
                checknapEnity check = new checknapEnity(nickname, HinhThucTran, sotien);
                listtien.add(check);
            }
        });
        for(checknapEnity s : listtien){
            if(s.getHinhtruc().equalsIgnoreCase("Nap The") || s.getHinhtruc().equalsIgnoreCase("Bank") || s.getHinhtruc().equalsIgnoreCase("CodePay")
             || s.getHinhtruc().equalsIgnoreCase("SmartLink") || s.getHinhtruc().equalsIgnoreCase("Momo")){
                tong = tong + s.getSotien();
            }
        }
        return tong;
    }
}
