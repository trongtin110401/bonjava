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
import java.util.Base64;

public class AutoBankCallbackOK88Process implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        BankCallBack bankcallback;
        BankCallBackResponse response = new BankCallBackResponse(200, "Thành công");
        String key = "SuNvIn88";
        String comment = request.getParameter("comment");
        String amountx = request.getParameter("amount");
        String signature = request.getParameter("signature");
        String originalInput = key+comment+amountx;
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        GencommentCodepay gen = new GencommentCodepay();
        CallAPIOK88 callok = new CallAPIOK88();
        comment = comment.toUpperCase();

//        Codepayok codepay33 = gen.findCodepayOK88(comment);
//        return "code = "+codepay33.getCodepay()+" | trans id = "+codepay33.getTransid()+" | nick = "+codepay33.getNickname();

        if(signature.equals(encodedString)){
            Codepayok codepay3 = gen.findCodepayOK88(comment.toUpperCase());
            if(codepay3.getUse() == 0){
                updateCodepayAAA(codepay3.getNickname(), true, codepay3.getCodepay(),codepay3.getBankname());
                gen.updateCodepayOK88Log(codepay3.getNickname(),true, codepay3.getCodepay(),codepay3.getBankname(),codepay3.getTransid());
                callok.Call(codepay3.getNickname(), codepay3.getTransid(), amountx);
                return response.toJson();
            }else{
                response.setErrorCode(500);
                response.setErrorDescription("Code da dc nap");
                return response.toJson();
            }

        }else {
            response.setErrorCode(500);
            response.setErrorDescription("That Bai");
            return response.toJson();
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
