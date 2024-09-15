/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.GiftCodeMessage
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.response.GiftCodeByNickNameResponse
 *  com.vinplay.vbee.common.response.GiftCodeCountResponse
 *  com.vinplay.vbee.common.response.GiftCodeDeleteResponse
 *  com.vinplay.vbee.common.response.GiftCodeResponse
 *  com.vinplay.vbee.common.response.GiftCodeUpdateResponse
 *  com.vinplay.vbee.common.response.ReportGiftCodeResponse
 *  com.vinplay.vbee.common.response.giftcode.GiftcodeStatisticObj
 */
package com.vinplay.usercore.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.vinplay.dal.entities.report.ReportMoneyModelNew;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.service.GiftCodeService;
import com.vinplay.vbee.common.dto.*;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.GiftCodeMessage;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.*;
import com.vinplay.vbee.common.response.giftcode.GiftcodeStatisticObj;
import com.vinplay.vbee.common.statics.Consts;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.python.parser.ast.Str;
import scala.Int;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Accumulators.first;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.and;

public class GiftCodeServiceImpl
        implements GiftCodeService {
    @Override
    public boolean xuatGiftCode(GiftCodeMessage msg) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.xuatGiftCode(msg);
    }

    @Override
    public GiftCodeUpdateResponse updateGiftCode(String userName, String giftCode) throws SQLException, ParseException {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        GiftCodeUpdateResponse response = new GiftCodeUpdateResponse(false, "10001");
        if (userMap.containsKey((Object) userName)) {
            UserCacheModel user = (UserCacheModel) userMap.get((Object) userName);
            if (true) { //user.getMobile() != null && user.isHasMobileSecurity() // bỏ mobile và mobile sercurity
                if (giftCode.toUpperCase().contains("MK1")) {
                    String beforeDate = "2017-04-01 00:00:00";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date1 = sdf.parse("2017-04-01 00:00:00");
                    Date date2 = user.getCreateTime();
                    if (date2.before(date1)) {
                        response.setErrorCode("10004");
                        return response;
                    }
                }
                GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
                response = dao.updateGiftCode(userName, giftCode);
                return response;
            }
            response.setErrorCode("10003");
        }
        return response;
    }

    @Override
    public GiftCodeUpdateResponse updateSpecialGiftCodeNew(String userName, String giftCode) throws SQLException, ParseException {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        GiftCodeUpdateResponse response = new GiftCodeUpdateResponse(false, "10001");
        if (userMap.containsKey((Object) userName)) {
            UserCacheModel user = (UserCacheModel) userMap.get((Object) userName);
            if (true) { //user.getMobile() != null && user.isHasMobileSecurity()
                GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
                response = dao.updateSpecialGiftCodeNew(userName, giftCode);
                return response;
            }
            response.setErrorCode("10003");
        }
        return response;
    }

    @Override
    public List<GiftCodeResponse> searchAllGiftCode(String nickName, String giftcode, String price, String source, String timeStart, String timeEnd, String moneyType, String usegift, int page, int totalRecord, String type, String release, String timeType, String block) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.searchAllGiftCode(nickName, giftcode, price, source, timeStart, timeEnd, moneyType, usegift, page, totalRecord, type, release, timeType, block);
    }

    @Override
    public List<GiftCodeResponse> searchAllGiftCodeAdmin(String price, String timeStart, String timeEnd, String moneyType, String usegift, String source, int page, int totalRecord, String block) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.searchAllGiftCodeAdmin(price, timeStart, timeEnd, moneyType, usegift, source, page, totalRecord, block);
    }

    @Override
    public List<GiftCodeCountResponse> countGiftCodeByPrice(String price, String source, String timeStart, String timeEnd, String moneyType, String type, String timeType, String block) throws SQLException {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        ArrayList<GiftCodeCountResponse> res = new ArrayList<GiftCodeCountResponse>();
        GiftCodeCountResponse model = null;
        List<String> listprice = new ArrayList<String>();
        if (price.isEmpty()) {
            listprice = dao.ListAllPrice(Integer.parseInt(moneyType));
        } else {
            listprice.add(price);
        }
        for (String mp : listprice) {
            model = dao.countGiftCodeByPrice(mp, source, timeStart, timeEnd, moneyType, type, timeType, block);
            if (model == null) continue;
            res.add(model);
        }
        return res;
    }

    @Override
    public List<GiftcodeStatisticObj> thongKeGiftcodeDaXuat(String source, String timeStart, String timeEnd, String moneyType, String timeType) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.thongKeGiftcodeDaXuat(source, timeStart, timeEnd, moneyType, timeType);
    }

    @Override
    public List<GiftCodeCountResponse> countGiftCodeByPriceAdmin(String price, String timeStart, String timeEnd, String moneyType, String type, String block) throws SQLException {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        ArrayList<GiftCodeCountResponse> res = new ArrayList<GiftCodeCountResponse>();
        GiftCodeCountResponse model = null;
        List<String> listprice = new ArrayList<String>();
        if (price.isEmpty()) {
            listprice = dao.ListAllPrice(Integer.parseInt(moneyType));
        } else {
            listprice.add(price);
        }
        for (String mp : listprice) {
            model = dao.countGiftCodeByPriceAdmin(mp, timeStart, timeEnd, moneyType, type, block);
            if (model == null) continue;
            res.add(model);
        }
        return res;
    }

    @Override
    public List<ReportGiftCodeResponse> ToolReportGiftCode(String nickName, String source, String timeStart, String timeEnd, String moneyType, String timeType, String block) throws SQLException, ParseException, JsonProcessingException {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.ToolReportGiftCode(nickName, source, timeStart, timeEnd, moneyType, timeType, block);
    }

    @Override
    public boolean genGiftCode(GiftCodeMessage msg) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.genGiftCode(msg);
    }

    @Override
    public List<String> loadAllGiftcode() {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.loadAllGiftcode();
    }

    @Override
    public long countsearchAllGiftCode(String nickName, String giftCode, String price, String source, String timeStart, String timeEnd, String moneyType, String usegift, String type, String release, String timeType, String block) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        long totalRecords = dao.countsearchAllGiftCode(nickName, giftCode, price, source, timeStart, timeEnd, moneyType, usegift, type, release, timeType, block);
        return totalRecords;
    }

    @Override
    public long countsearchAllGiftCodeAdmin(String price, String source, String timeStart, String timeEnd, String moneyType, String usegift, String block) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.countsearchAllGiftCodeAdmin(price, source, timeStart, timeEnd, moneyType, usegift, block);
    }

    @Override
    public List<ReportGiftCodeResponse> ToolReportGiftCodeBySource(String source, String timeStart, String timeEnd, String moneyType, String type, int page, String timeType, String block) throws SQLException, ParseException, JsonProcessingException {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.ToolReportGiftCodeBySource(source, timeStart, timeEnd, moneyType, type, page, timeType, block);
    }

    @Override
    public String uploadFileGiftCode(String lstNickName, long vin, long xu) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.uploadFileGiftCode(lstNickName, vin, xu);
    }

    @Override
    public boolean RestoreGiftCode(String price, String source, String giftcode, String release) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.RestoreGiftCode(price, source, giftcode, release);
    }

    @Override
    public List<GiftCodeResponse> searchAllGiftCodeByNickName(String nickName, int page) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.searchAllGiftCodeByNickName(nickName, page);
    }

    public FindGiftCodeUsedByUserDto findGiftCodeByNickName(String nickName, int pageIndex, int pageSize) {
        FindGiftCodeUsedByUserDto results = new FindGiftCodeUsedByUserDto(false, "1001");
        List<UseGiftCodeDto> transactions = new ArrayList<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();

        int skip = (pageIndex - 1) * pageSize;

        MongoCollection<Document> collection = db.getCollection("user_gift_code");
        Document query = new Document("nick_name", nickName);
        MongoCursor<Document> cursor = collection.find(query).skip(skip).limit(pageSize).iterator();

        while (cursor.hasNext()) {
            Document document = cursor.next();
            UseGiftCodeDto giftCodeDto = new UseGiftCodeDto();
            giftCodeDto.setCode(document.getString("code"));
            giftCodeDto.setPrice(document.getInteger("price"));
            giftCodeDto.setActive(document.getBoolean("active"));
            giftCodeDto.setType(document.getString("type"));
            giftCodeDto.setTimelog(document.getString("created_time"));
            giftCodeDto.setNickname(document.getString("nick_name"));
            transactions.add(giftCodeDto);
        }
        results.setErrorCode("0");
        results.setSuccess(true);
        results.setPageIndex(pageIndex);
        results.setPageSize(pageSize);
        results.setTransactions(transactions);
        return results;
    }

    public FindAllGiftCodeDto findAllGiftCode(String nickName, String code, String price, Boolean active,
                                              String type, String startTime, String endTime, int pageIndex, int pageSize) {
        FindAllGiftCodeDto results = new FindAllGiftCodeDto(false, "1001");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        int skip = (pageIndex - 1) * pageSize;

        MongoCollection<Document> collection = db.getCollection("gift_code");
        Document query = new Document();
        if (nickName != null && !nickName.isEmpty()) {
            query.append("nickName", nickName);
        }
        if (code != null && !code.isEmpty()) {
            query.append("code", code);
        }
        if (StringUtils.isNotEmpty(price)) {
            query.append("price", price);
        }

        if (active != null) {
            query.append("active", active);
        }
        if (type != null && !type.isEmpty()) {
            query.append("type", type);
        }
        if ((startTime != null && !startTime.isEmpty()) && (endTime != null && !endTime.isEmpty())) {
            query.append("created_time", new Document("$gte", startTime).append("$lte", endTime));
        }

        Document sort = new Document("created_time", -1);
        MongoCursor<Document> cursor = collection.find(query).sort(sort).skip(skip).limit(pageSize).iterator();

        long totalCount = collection.count(query);

        List<GiftCodeDto> transactions = new ArrayList<>();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            GiftCodeDto giftCodeDto = new GiftCodeDto();
            giftCodeDto.setCode(document.getString("code"));
            giftCodeDto.setPrice(document.getInteger("price"));
            giftCodeDto.setQuantity(document.getInteger("quantity"));
            giftCodeDto.setActive(document.getBoolean("active"));
            giftCodeDto.setType(document.getString("type"));
            giftCodeDto.setExpirationDate(document.getInteger("expiration_date"));
            giftCodeDto.setExpirationTime(document.getString("expiration_time"));
            giftCodeDto.setNickName(document.getString("nick_name"));
            giftCodeDto.setUsedTime(document.getString("used_time"));
            giftCodeDto.setCreatedDate(document.getString("created_time"));
            transactions.add(giftCodeDto);
        }
        results.setTotal(totalCount);
        results.setErrorCode("0");
        results.setSuccess(true);
        results.setPageIndex(pageIndex);
        results.setPageSize(pageSize);
        results.setTransactions(transactions);
        return results;
    }

    @Override
    public long countAllGiftCodeByNickName(String nickName) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.countAllGiftCodeByNickName(nickName);
    }

    @Override
    public GiftCodeByNickNameResponse getUserInfoByGiftCode(String giftCode, IMap<String, UserCacheModel> userMap, int page, int totalRecord) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.getUserInfoByGiftCode(giftCode, userMap, page, totalRecord);
    }

    @Override
    public GiftCodeDeleteResponse DeleteGiftCode(String startDate, String endDate, String source, String price) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.DeleteGiftCode(startDate, endDate, source, price);
    }

    @Override
    public boolean saveGiftCode(GiftCodeDto giftCodeDto) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.saveGiftCode(giftCodeDto);
    }

    @Override
    public GiftCodeDto findActiveByCode(String code) {
        GiftCodeDAOImpl dao = new GiftCodeDAOImpl();
        return dao.findActiveByCode(code);
    }

    public void saveUserUseGiftCode(UseGiftCodeDto useGiftCodeDto) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("user_gift_code");
        Document doc = new Document();
        doc.append("code", useGiftCodeDto.getCode());
        doc.append("price", useGiftCodeDto.getPrice());
        doc.append("type", useGiftCodeDto.getType());
        doc.append("created_time", useGiftCodeDto.getTimelog());
        doc.append("active", useGiftCodeDto.isActive());
        doc.append("nick_name", useGiftCodeDto.getNickname());
        col.insertOne(doc);
    }

    public void updateGiftCode(GiftCodeDto giftCodeDto) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("gift_code");
        Document query = new Document("code", giftCodeDto.getCode());
        Document update = new Document("$set", new Document()
                .append("price", giftCodeDto.getPrice())
                .append("quantity", giftCodeDto.getQuantity())
                .append("type", giftCodeDto.getType())
                .append("length", giftCodeDto.getLength())
                .append("expiration_time", giftCodeDto.getExpirationTime())
                .append("active", giftCodeDto.isActive())
                .append("nick_name", giftCodeDto.getNickName())
                .append("used_time", giftCodeDto.getUsedTime()));
        col.updateOne(query, update);
    }

    public boolean checkUserUseGiftCode(String nickName, String type) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("nick_name", nickName);
        conditions.put("type", type);
        FindIterable iterable = db.getCollection("user_gift_code").find(new Document(conditions));
        return iterable.iterator().hasNext();
    }

    @Override
    public boolean checkUserTransactionAfterUseGiftCode(String nickName) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("nick_name", nickName);
        FindIterable<Document> iterable = db.getCollection("user_gift_code")
                .find(new Document(conditions))
                .sort(Sorts.descending("created_time"))
                .limit(1);
        if (iterable.first() == null) {
            return true;
        }
        Document firstDocument = iterable.first();
        String createdTime = firstDocument.getString("created_time");
        long price = firstDocument.getInteger("price").longValue();
        long totalBetValue = getTotalMoneyUser(createdTime, nickName);
        return totalBetValue >= (price * 0.5);
    }


    public long getTotalMoneyUser(String timeStart, String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<>();
        BasicDBObject timeCondition = new BasicDBObject();

        conditions.put("is_bot", false);
        conditions.put("nick_name", nickname);

        if (timeStart != null && !timeStart.isEmpty()) {
            timeCondition.put("$gte", timeStart);
            conditions.put("trans_time", timeCondition);
        }

        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(new Document(conditions)),
                // Chỉ lấy những bản ghi có service_name chứa chữ "Đặt cược"
//                Aggregates.match(Filters.regex("service_name", "cược")),
                // Loại bỏ các action không liên quan
                Aggregates.match(Filters.not(Filters.in("action_name", Consts.NO_GAME))),
                // Tính tổng số tiền exchange
                Aggregates.group(null, Accumulators.sum("totalMoney", new Document("$abs", "$money_exchange")))
        );

        AggregateIterable<Document> result = db.getCollection("log_money_user_vin").aggregate(pipeline);
        Document totalMoneyDoc = result.first();

        return totalMoneyDoc != null ? totalMoneyDoc.getLong("totalMoney") : 0L;
    }


    public void insertCampaignName(String campaignName) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("campaign_gift_code");
        Document existingDoc = collection.find(Filters.eq("name", campaignName)).first();

        if (existingDoc != null) {
            return;
        }
        Document newDocument = new Document("_id", System.currentTimeMillis())
                .append("name", campaignName);
        collection.insertOne(newDocument);
    }

    public List<CampaignName> getAllCampaign() {
        Map<String, CampaignName> campaigns = new HashMap<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("campaign_gift_code");
        Document sortCriteria = new Document("_id", 1);
        try (MongoCursor<Document> cursor = collection.find().sort(sortCriteria).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                CampaignName campaign = new CampaignName();
                campaign.setId(document.getLong("_id"));
                campaign.setCampaignName(document.getString("name"));
                campaigns.put(String.valueOf(campaign.getId()), campaign);
            }
        }

        // Thống kê GIFTCODE theo danh sách campain được truyền vào
//        List<String> types = campaigns.keySet().stream().map(String::valueOf).collect(Collectors.toList());

//        System.out.println(new Gson().toJson(types));

        // Xây dựng pipeline để thực hiện truy vấn

        // Danh sách type được truyền vào
//        List<String> types = Arrays.asList("1725850870622", "someOtherType");

        // Tạo pipeline của aggregation
        MongoCollection<Document> col = db.getCollection("gift_code");
        AggregateIterable<Document> result = col.aggregate(Arrays.asList(
                Document.parse(
                        "\n" +
                                "  {\n" +
                                "    $group: {\n" +
                                "      _id: \"$type\",\n" +
                                "      total_records: { $sum: 1 }, \n" +
                                "      total_active: {\n" +
                                "        $sum: {\n" +
                                "          $cond: [{ $eq: [\"$active\", true] }, 1, 0]\n" +
                                "        }\n" +
                                "      },\n" +
                                "      total_used: {\n" +
                                "        $sum: {\n" +
                                "          $cond: [\n" +
                                "            { $gt: [\"$used_time\", \"2024\"] },\n" +
                                "            1,\n" +
                                "            0\n" +
                                "          ]\n" +
                                "        }\n" +
                                "      }\n" +
                                "    }\n" +
                                "  }\n" +
                                "")
        ));

        // Gán lại kết quả thống kê cho campaign
        for (Document doc : result) {
            String type = doc.getString("_id");
            int totalCode = doc.getInteger("total_records", 0);
            int totalActive = doc.getInteger("total_active", 0);
            int totalUsed = doc.getInteger("total_used", 0);
            int totalUnused = totalCode - totalUsed;

            // Hiển thị các giá trị để kiểm tra
            System.out.println("Type: " + type);
            System.out.println("Total Code: " + totalCode);
            System.out.println("Total Active: " + totalActive);
            System.out.println("Total Used: " + totalUsed);
            System.out.println("Total Unused: " + totalUnused);
            System.out.println("-----------------------------");

            if (campaigns.get(type) != null) {
                campaigns.get(type).setTotal(totalCode);
                campaigns.get(Long.valueOf(type)).setQuantityActiveCode(totalActive);
                campaigns.get(Long.valueOf(type)).setUsed(totalUsed);
                campaigns.get(Long.valueOf(type)).setUnused(totalUnused);
            }
        }

        System.out.println(new Gson().toJson(campaigns.values()));

        return new ArrayList<>(campaigns.values());
    }

    public List<CampaignName> getAllCampaignWithoutGiftCodeInfo() {
        List<CampaignName> campaignNames = new ArrayList<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("campaign_gift_code");
        Document sortCriteria = new Document("_id", 1);
        try (MongoCursor<Document> cursor = collection.find().sort(sortCriteria).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                CampaignName campaignName = new CampaignName();
                campaignName.setId(document.getLong("_id"));
                campaignName.setCampaignName(document.getString("name"));
                campaignNames.add(campaignName);
            }
        }
        return campaignNames;
    }

    public void recallGiftCode(String type, String code) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("gift_code");
        Document query = new Document();

        if (code != null && !code.isEmpty()) {
            query.append("code", code);
        }
        if (type != null && !type.isEmpty()) {
            query.append("type", type);
        }
        Document update = new Document("$set", new Document("active", false));
        collection.updateMany(query, update);
    }

    public List<UserUsedGiftCodeAndDepositDto> getAllUserUsedGiftCodeAndDeposit(String type, int pageIndex, int pageSize) {
        List<UserUsedGiftCodeAndDepositDto> response = new ArrayList<>();
        MongoDatabase database = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> userGiftCodeCollection = database.getCollection("user_gift_code");
        MongoCollection<Document> userDeposit = database.getCollection("log_money_user_nap_vin");

        Document query = new Document();
        if (type != null && !type.isEmpty()) {
            query.append("type", type);
        }
        int skip = (pageIndex - 1) * pageSize;

        List<Document> userGiftCodeData = userGiftCodeCollection.find(query)
                .projection(new Document("nick_name", 1).append("created_time", 1).append("code", 1))
                .skip(skip)
                .limit(pageSize)
                .into(new ArrayList<>());

        for (Document userGiftCode : userGiftCodeData) {
            UserUsedGiftCodeAndDepositDto dto = new UserUsedGiftCodeAndDepositDto();
            String nickname = userGiftCode.getString("nick_name");
            String createdTime = userGiftCode.getString("created_time");
            String code = userGiftCode.getString("code");

            Document userDepositQuery = new Document("nick_name", nickname)
                    .append("create_time", new Document("$gte", createdTime));
            List<Document> userDepositResult = userDeposit.find(userDepositQuery).into(new ArrayList<>());
            if (userDepositResult.isEmpty()) {
                continue;
            }
            long money = 0;
            for (Document document : userDepositResult) {
                money += document.getLong("money_exchange");
            }
            dto.setNickName(nickname);
            dto.setDayUsedGiftCode(createdTime);
            dto.setMoney(money);
            dto.setCode(code);
            response.add(dto);
        }
        return response;
    }

    public void activeGiftCode(String type, String code) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("gift_code");
        Document query = new Document();

        if (code != null && !code.isEmpty()) {
            query.append("code", code);
        }
        if (type != null && !type.isEmpty()) {
            query.append("type", type);
        }
        query.append("$and", Arrays.asList(
                new Document("nick_name", new Document("$eq", null)),
                new Document("nick_name", new Document("$exists", false))
        ));
        Document update = new Document("$set", new Document("active", true));
        collection.updateMany(query, update);
    }

    public boolean deleteCampaign(long id) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> giftCode = db.getCollection("gift_code");

        Bson query = Filters.and(
                Filters.eq("type", String.valueOf(id)),
                Filters.eq("active", true)
        );
        giftCode.updateMany(query, Updates.set("active", false));
        MongoCollection<Document> campaign = db.getCollection("campaign_gift_code");
        campaign.deleteOne(Filters.eq("_id", id));
        return true;
    }
}

