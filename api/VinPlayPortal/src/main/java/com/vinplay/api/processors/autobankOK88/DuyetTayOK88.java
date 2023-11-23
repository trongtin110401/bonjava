package com.vinplay.api.processors.autobankOK88;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.entities.BankCallBack;
import com.vinplay.api.entities.BankCallBackResponse;
import com.vinplay.api.processors.Codepayok;
import com.vinplay.api.processors.GencommentCodepay;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;

public class DuyetTayOK88 implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        BankCallBack bankcallback;
        BankCallBackResponse response = new BankCallBackResponse(200, "Thành công");
        String status = request.getParameter("status");
        String nickname = request.getParameter("nickname");
        GencommentCodepay gen = new GencommentCodepay();
        Codepayok codepay3 = gen.findNicknameOK88(nickname.trim());
        if(codepay3 == null){
            response.setErrorCode(500);
            response.setErrorDescription("Nickname khong ton tai");
            return response.toJson();
        }else{
            if(status.equalsIgnoreCase("1") || status.equalsIgnoreCase("2")){
                updateCodepayAAA(codepay3.getNickname(), true, codepay3.getCodepay(), codepay3.getBankname());
                return response.toJson();
            }else{
                response.setErrorCode(500);
                response.setErrorDescription("That Bai");
                return response.toJson();
            }
        }


    }
    public void updateCodepayAAA(String nickname, boolean use, String codepay, String bankname){
        try {
            int check = 0;
            if(use == true){
                check = 1;
            }else{
                check = 0;
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK88");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("codepay", codepay);
            doc.append("use", check);
            doc.append("createAt",timeAt);
            doc.append("bankname", bankname);
            col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }
}
