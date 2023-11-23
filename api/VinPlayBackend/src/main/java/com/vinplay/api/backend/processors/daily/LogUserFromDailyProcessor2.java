package com.vinplay.api.backend.processors.daily;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.daily.entities.UserFromDailyResponse;
import com.vinplay.daily.entities.UserWinLostResponse;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.entities.HistoryTransResponse;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class LogUserFromDailyProcessor2 implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");
    private static final String SUCCESS = "THÀNH CÔNG";
    private static final String APPROVED = "ĐÃ DUYỆT";
    private static final String MOMO = "MOMO";
    private static final String BANK = "BANK";
    private static final String CARD = "CARD";
    private static final String ONE_PAY = "ONE_PAY";
    private static final String CARD_OUT = "RUT_CARD";
    private static final String BANK_OUT = "RUT_BANK";

    public synchronized String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        UserFromDailyResponse res = new UserFromDailyResponse(false, "1001");
        try {
            String codeDaily = request.getParameter("dl");
            String startTime = request.getParameter("ts");
            String endTime = request.getParameter("te");
            String pageStr = request.getParameter("p");
            String typeSearch = request.getParameter("type");
            int maxItem = Integer.parseInt(request.getParameter("record"));
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;

            if(typeSearch.equals("elk")) {
                // THỐNG KÊ DÙNG THEO ELK: get user form id daily
                APIelk apielk = new APIelk();
//                int tonguser = apielk.TotalUserbyIDDaiLy(codeDaily);
//                numStart = tonguser - (maxItem*page) < 0 ? (tonguser - maxItem)  : (tonguser - (maxItem*page));
                ArrayList<UserWinLostResponse> list_nickname_byIDdaily = apielk.GetUserbyIDDaiLy(codeDaily, numStart, maxItem);
                int tong_userDaily = apielk.TotalUserbyIDDaiLy(codeDaily);
                final List<UserWinLostResponse> list_nap_rut = new ArrayList<>();
                for(UserWinLostResponse user : list_nickname_byIDdaily){
                    long nap = apielk.GetTongTienNapByUser(user.NickName, startTime, endTime);
                    long rut = apielk.GetTongTienRutByUser(user.NickName, startTime, endTime);
                    long send = apielk.GetTongTienByUserSend(user.NickName, startTime, endTime);
                    long receive = apielk.GetTongTienByUserReceive(user.NickName, startTime, endTime);
                    UserWinLostResponse listUser = new UserWinLostResponse(user.NickName, user.UserName, user.CreateAt, nap + receive, rut + send);
                    list_nap_rut.add(listUser);
                }
//                Collections.reverse(list_nap_rut);
                res.ListUser = list_nap_rut;
                res.HistoryTransIn = null;
                res.HistoryTransOut = null;
                res.TotalUser = tong_userDaily;
            }

            if(typeSearch.equals("mongo")) {

                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                final List<HistoryTransModel> records1 = new ArrayList<>();
                final List<HistoryTransModel> records2 = new ArrayList<>();
                final List<UserWinLostResponse> records3 = new ArrayList<>();
                MongoDatabase db = MongoDBConnectionFactory.getDB();
                MongoCollection col = db.getCollection("user_map_daily");


                BasicDBObject objSort = new BasicDBObject();
                objSort.put("_id", -1);
                Document conditions = new Document();
                if (!codeDaily.isEmpty()) {
                    conditions.put("id_daily", codeDaily);
                } else {
                    return res.toJson();
                }
                //todo: check validate parameter
                long totalRows = db.getCollection("user_map_daily").count(conditions);

                FindIterable iterable = col.find( new Document(conditions)).sort( objSort).skip(numStart).limit(maxItem);
                iterable.forEach((Block<Document>) document -> {
                    String nickNameUser = document.getString("nickName");
                    String UserNameUser = document.getString("user_name");
                    String CreateAtUser = document.getString("time_log");
                    if (!nickNameUser.isEmpty()) {// GET LIST USER
                        UserWinLostResponse listUser = new UserWinLostResponse(nickNameUser, UserNameUser, CreateAtUser, 0, 0);
                        records3.add(listUser);
                    }
                });
                res.ListUser = records3;
                res.HistoryTransIn = records2;
                res.HistoryTransOut = records1;
                res.TotalUser = totalRows;
            }

            return res.toJson();
        } catch (Exception e) {
            //todo : set lại mã code theo ý
            res.setErrorCode("1002");
            logger.error("LogUserFromDailyProcessor error with " + e.getStackTrace());
        }
        return res.toJson();
    }
}
