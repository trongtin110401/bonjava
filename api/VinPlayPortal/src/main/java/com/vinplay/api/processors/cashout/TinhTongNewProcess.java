package com.vinplay.api.processors.cashout;

import com.mongodb.BasicDBObject;
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

public class TinhTongNewProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String idDaily = request.getParameter("maDaily");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        ArrayList<NapRutModel> listnap = new ArrayList<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("NapRutGame");
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject conditions = new BasicDBObject();
        obj.put("$gte", (Object)startDate);
        obj.put("$lte", (Object)endDate);
        conditions.put("CreateAt", (Object)obj);
        conditions.put("MaDaily", (Object)idDaily);
        FindIterable iterable = col.find((Bson)conditions).skip(0).limit(99999);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                String nickname = document.getString((Object)"nick_name");
                String maDaily = document.getString((Object)"MaDaily");
                long sotienx = document.getLong((Object)"SoTien");
                String hinhthuctran = document.getString((Object)"HinhThucTran");
                String tranIDx = document.getString((Object)"TranID");
                String creatat = document.getString((Object)"CreateAt");
                NapRutModel napgame = new NapRutModel(tranIDx,nickname,maDaily, sotienx,hinhthuctran, creatat);
                listnap.add(napgame);
            }
        });
        long napbank = 0;
        long napthe = 0;
        long napcodepay = 0;
        long napmm = 0;
        long napsmart = 0;
        long rutbank = 0;
        long rutthe = 0;
        for(NapRutModel n : listnap){
            if(n.getHinhThucTran().equalsIgnoreCase("Bank")){
                napbank = napbank + n.getSoTien();
            }
            if(n.getHinhThucTran().equalsIgnoreCase("Nap The")){
                napthe = napthe + n.getSoTien();
            }
            if(n.getHinhThucTran().equalsIgnoreCase("CodePay")){
                napcodepay = napcodepay + n.getSoTien();
            }
            if(n.getHinhThucTran().equalsIgnoreCase("SmartLink")){
                napsmart = napsmart + n.getSoTien();
            }
            if(n.getHinhThucTran().equalsIgnoreCase("Momo")){
                napmm = napmm + n.getSoTien();
            }
            if(n.getHinhThucTran().equalsIgnoreCase("Rut Bank")){
                rutbank = rutbank + n.getSoTien();
            }
            if(n.getHinhThucTran().equalsIgnoreCase("Rut Card")){
                rutthe = rutthe + n.getSoTien();
            }
        }
        long total_nap = napbank + napthe + napcodepay + napmm + napsmart;
        long total_rut = rutbank + rutthe;
        String result = "{\"idDaiLy\": \""+idDaily+"\", \"tongnap\": "+total_nap+", \"tongrut\": "+total_rut+", \"napbank\":"+napbank+",\"nap the\":"+napthe+",\"napcode\":"+napcodepay+",\"napsmart\":"+napsmart+",\"napmomo\":"+napmm+",\"rutbank\":"+rutbank+",\"rutthe\":"+rutthe+"}";
        return result;
    }

}

