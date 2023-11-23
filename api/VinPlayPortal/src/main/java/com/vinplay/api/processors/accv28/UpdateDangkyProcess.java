package com.vinplay.api.processors.accv28;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;

public class UpdateDangkyProcess implements BaseProcessor<HttpServletRequest, String> {
        public String execute(Param<HttpServletRequest> param) {
                HttpServletRequest request = (HttpServletRequest)param.get();
                String username = request.getParameter("username");
                updateTrangThaiDK(username);
                return "1";
        }

        private void updateTrangThaiDK(String username) {
                try {
                        boolean dangky = true;
                        MongoDatabase db = MongoDBConnectionFactory.getDB();
                        MongoCollection col = db.getCollection("accv8");
                        Document doc = new Document();
                        doc.append("dangky",(Object)dangky);
                        col.updateOne((Bson) new Document("username", username), (Bson) new Document("$set", (Object) doc));

                }catch (Exception e) {
                        e.printStackTrace();
                }

        }
}
