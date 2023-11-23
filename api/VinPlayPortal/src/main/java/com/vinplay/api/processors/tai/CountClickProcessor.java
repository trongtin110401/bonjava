package com.vinplay.api.processors.tai;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;


public class CountClickProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");
    private static final String ANALYTICS_DAILY = "analytics_daily";
    private static final String ANALYTICS_TAI_YOU88 = "analytics_tai_you88";

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        BaseResponseModel res = new BaseResponseModel(false, "1001");
        InsertELK elk = new InsertELK();
        try {
            String utm_dl = request.getParameter("utm_dl");
            String utm_source = request.getParameter("utm_source");
            String utm_medium = request.getParameter("utm_medium");
            String utm_campaign = request.getParameter("utm_campaign");
            if (!utm_dl.isEmpty()) {
                MongoDatabase db = MongoDBConnectionFactory.getDB();
                MongoCollection colData = db.getCollection(ANALYTICS_TAI_YOU88);
                MongoCollection colutm_dl = db.getCollection(ANALYTICS_DAILY);
                Document conditionData = new Document();
                Document conditionutm_dl = new Document();
                if (!checkExisted(utm_dl) ) {
                    conditionutm_dl.append("utm_dl", utm_dl);
                    colutm_dl.insertOne(conditionutm_dl);
                    elk.InsertDaily(utm_dl);
                }
                String time_create = VinPlayUtils.getCurrentDateTime();
                conditionData.append("utm_dl", utm_dl);
                conditionData.append("utm_source", utm_source);
                conditionData.append("utm_medium", utm_medium);
                conditionData.append("utm_campaign", utm_campaign);
                conditionData.append("create_time", time_create);
                colData.insertOne(conditionData);
                elk.InsertDailyTai88(utm_dl, utm_source, utm_medium, utm_campaign, time_create);
                res.setSuccess(true);
                res.setErrorCode("0");
                return toString();
            }

        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return res.toJson();
    }

    private Boolean checkExisted(String utm_dl) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("utm_dl", utm_dl);
        long totalRows = db.getCollection(ANALYTICS_DAILY).count(conditions);
        if (totalRows > 0)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }
}
