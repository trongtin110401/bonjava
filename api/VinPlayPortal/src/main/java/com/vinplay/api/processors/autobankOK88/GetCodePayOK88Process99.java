package com.vinplay.api.processors.autobankOK88;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.processors.Codepayok;
import com.vinplay.api.processors.GencommentCodepay;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;

public class GetCodePayOK88Process99 implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nickName = request.getParameter("nickname");
            String transid = request.getParameter("transid");
            String bank = request.getParameter("bank");
            String codepay = request.getParameter("codepay");
            GencommentCodepay gen = new GencommentCodepay();
            Codepayok codepay3 = gen.findNicknameOK88(nickName);

            if(codepay3 == null){
                gen.insertCodepayOK88(nickName,codepay,bank,transid);
                gen.insertCodepayOK88Log(nickName,codepay,bank,transid);
            }else{
                gen.updateCodepayOK88(nickName,false,codepay,bank,transid);
                gen.insertCodepayOK88Log(nickName,codepay,bank,transid);
            }
            String resp = "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"comment\":\"" + codepay + "\",\"TrainID\": \"" + transid + "\",\"bankname\":\"" + bank + "\",\"nickname\":\""+nickName+"\"}";
            return resp;

        }catch(Exception e) {
            e.printStackTrace();
            return e.getMessage();
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
            MongoCollection col = db.getCollection("CodePayOK99");
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
