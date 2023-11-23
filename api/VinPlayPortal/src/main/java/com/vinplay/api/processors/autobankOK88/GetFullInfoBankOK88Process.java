package com.vinplay.api.processors.autobankOK88;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class GetFullInfoBankOK88Process implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            ArrayList<BankInfo> ban = findBank();
            Gson gson = new Gson();
            if(ban == null){
                return "{\"errorcode\":300}";
            }else{
                return gson.toJson(ban);
            }

        }catch(Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public ArrayList<BankInfo> findBank(){
        try {
            ArrayList<BankInfo> listbank = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK88Bank");
            conditions.put("chinhanh", "HA NOI");
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String bank = document.getString((Object) "bank");
                    String stk = document.getString((Object) "stk");
                    String name = document.getString((Object) "name");
                    String chinhanh = document.getString((Object) "chinhanh");
                    BankInfo ba = new BankInfo(bank,stk,name,chinhanh);
                    listbank.add(ba);
                }
            });
            if(listbank.size() == 0){
                return listbank;
            }else{
                return listbank;
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
            return null;
        }

    }

}
