package game;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.dao.PokeGoDAO;
import com.vinplay.vbee.common.models.minigame.pokego.LSGDPokeGo;
import com.vinplay.vbee.common.models.minigame.pokego.TopPokeGo;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;

import java.util.*;

public class SlotExtendDaoImpl implements PokeGoDAO {
    @Override
    public List<TopPokeGo> getTopPokeGo(int moneyType, int pageNumber) {
        int pageSize = 1;
        int skipNumber = (pageNumber - 1) * 10;
        final List<TopPokeGo> results = new ArrayList();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable<Document> iterable = null;
        Document conditions = new Document();
        conditions.put("$or", Arrays.asList(new Document("result", 3), new Document("result", 4)));
        conditions.put("money_type", moneyType);
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_slot_extend").find(conditions).sort(sortCondtions).skip(skipNumber).limit(10);
        iterable.forEach(new Block<Document>() {
            public void apply(Document document) {
                TopPokeGo entry = new TopPokeGo();
                entry.un = document.getString("user_name");
                entry.bv = document.getLong("bet_value");
                entry.pz = document.getLong("prize");
                entry.ts = document.getString("time_log");
                results.add(entry);
            }
        });
        return results;
    }

    public int countLSGD(String username, int moneyType) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("user_name", username);
        conditions.put("money_type", moneyType);
        long totalRows = db.getCollection("log_slot_extend").count(conditions);
        return (int)totalRows;
    }

    public List<LSGDPokeGo> getLSGD(String username, int moneyType, int pageNumber) {
        int skipNumber = (pageNumber - 1) * 10;
        final List<LSGDPokeGo> results = new ArrayList();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable<Document> iterable = null;
        Document conditions = new Document();
        conditions.put("user_name", username);
        conditions.put("money_type", moneyType);
        iterable = db.getCollection("log_slot_extend").find(conditions).skip(skipNumber).limit(10);
        iterable.forEach(new Block<Document>() {
            public void apply(Document document) {
                LSGDPokeGo entry = new LSGDPokeGo();
                entry.rf = document.getLong("reference_id");
                entry.un = document.getString("user_name");
                entry.bv = document.getLong("bet_value");
                entry.pz = document.getLong("prize");
                entry.lb = document.getString("lines_betting");
                entry.lw = document.getString("lines_win");
                entry.ps = document.getString("prizes_on_line");
                entry.ts = document.getString("time_log");
                entry.matrix = document.getString("matrix");
                results.add(entry);
            }
        });
        return results;
    }

    public long getLastRefenceId() {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Map<String, Object> conditions = new HashMap();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        FindIterable<Document> iterable = db.getCollection("log_slot_extend").find(new Document(conditions)).sort(objsort).limit(1);
        Document document = iterable != null ? iterable.first() : null;
        long referenceId = document == null ? 0L : document.getLong("reference_id");
        return referenceId;
    }

    public List<TopPokeGo> getTop(int moneyType, int number) {
        final List<TopPokeGo> results = new ArrayList();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable<Document> iterable = null;
        Document conditions = new Document();
        conditions.put("$or", Arrays.asList(new Document("result", 3), new Document("result", 4)));
        conditions.put("money_type", moneyType);
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_slot_extend").find(conditions).sort(sortCondtions).limit(number);
        iterable.forEach(new Block<Document>() {
            public void apply(Document document) {
                TopPokeGo entry = new TopPokeGo();
                entry.un = document.getString("user_name");
                entry.bv = document.getLong("bet_value");
                entry.pz = document.getLong("prize");
                entry.ts = document.getString("time_log");
                results.add(entry);
            }
        });
        return results;
    }
}
