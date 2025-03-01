package game.modules.lobby;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.common.HttpCommon;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import okhttp3.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CheckNap {

    public naptmp tongnapTheELK(String nickname) {
//        try {
//            long tong = 0;
//            long tongthe = 0;
//            long tongadmin = 0;
//            long tongbank = 0;
//            long tongmomo = 0;
//            ArrayList<checknapEnity> listtien = new ArrayList<>();
//            HashMap<String, Object> conditions = new HashMap<String, Object>();
//
//            int timeretry = 10;
//            boolean check = false;
//            String sig = "\"successful\":1";
//            HistoryTransModel his = null;
//            ArrayList<HistoryTransModel> list_his = new ArrayList<>();
//            int retry = 3;
//            do {
//                retry--;
//                if (retry < 0) {
//                    break;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\"duhfusdfhie\"}},{\"match\":{\"hinhthuc.keyword\":\"Nạp tiền\"}},{\"match\":{\"trangthai.keyword\":\"Thành công\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":1000,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//                timeretry--;
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject person = jsonArray.getJSONObject(i);
//                    JSONObject test = person.getJSONObject("_source");
//                    String nickName = test.getString("nickName");
//                    String id = test.getString("id");
//                    String giaodich = test.getString("giaodich");
//                    String congGiaoDich = test.getString("congGiaoDich");
//                    String hinhthuc = test.getString("hinhthuc");
//                    String sotienx = test.getString("sotien");
//                    String trangthai = test.getString("trangthai");
//                    String ghiChu = test.getString("ghiChu");
//                    String hinhthucTrans = test.getString("hinhthucTrans");
//                    String transId = test.getString("transId");
//                    String createAt = test.getString("createAt");
////                    his = new HistoryTransModel(giaodich, congGiaoDich, hinhthuc, sotienx, trangthai, ghiChu, nickName, hinhthucTrans, transId, id, createAt);
//                    long sotien = Long.parseLong(sotienx);
//                    checknapEnity checkn = new checknapEnity(nickName, hinhthucTrans, sotien);
//                    listtien.add(checkn);
////                    list_his.add(his);
//                }
//            } while (check == false && timeretry > 0);
//
//            for (checknapEnity s : listtien) {
////            if(s.getHinhtruc().equalsIgnoreCase("CARD") || s.getHinhtruc().equalsIgnoreCase("BANK") || s.getHinhtruc().equalsIgnoreCase("CodePay")
////                    || s.getHinhtruc().equalsIgnoreCase("ONE_PAY") || s.getHinhtruc().equalsIgnoreCase("ADMIN_TRANSFER_TO_USER")){
//
//                if (s.getHinhtruc().equalsIgnoreCase("CARD")) {
//                    tongthe = tongthe + s.getSotien();
//                } else if (s.getHinhtruc().equalsIgnoreCase("ADMIN_TRANSFER_TO_USER")) {
//                    tongadmin = tongadmin + s.getSotien();
//                } else if (s.getHinhtruc().equalsIgnoreCase("BANK")) {
//                    tong = tong + s.getSotien();
//                    tongbank = tongbank + s.getSotien();
//                } else if (s.getHinhtruc().equalsIgnoreCase("MOMO")) {
//                    tong = tong + s.getSotien();
//                    tongmomo = tongmomo + s.getSotien();
//                }
//
//            }
//            naptmp ntmp = new naptmp(nickname, tong, tongthe, tongadmin);
//            ntmp.setNapbank(tongbank);
//            ntmp.setNapmomo(tongmomo);
//            return ntmp;
//
//        } catch (Exception e) {
//            return null;
//        }
        return null;
    }

    public long tongrutELK(String nickname) {
//        try {
//            long tong = 0;
//            ArrayList<checknapEnity> listtien = new ArrayList<>();
//            HashMap<String, Object> conditions = new HashMap<String, Object>();
//            int timeretry = 10;
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int retry = 3;
//            do {
//                retry--;
//                if (retry < 0) {
//                    break;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\"duhfusdfhie\"}},{\"match\":{\"hinhthuc.keyword\":\"Rút tiền\"}},{\"match\":{\"trangthai.keyword\":\"Đã duyệt\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//                timeretry--;
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject person = jsonArray.getJSONObject(i);
//                    JSONObject test = person.getJSONObject("_source");
//                    String nickName = test.getString("nickName");
//                    String id = test.getString("id");
//                    String giaodich = test.getString("giaodich");
//                    String congGiaoDich = test.getString("congGiaoDich");
//                    String hinhthuc = test.getString("hinhthuc");
//                    String sotienx = test.getString("sotien");
//                    String trangthai = test.getString("trangthai");
//                    String ghiChu = test.getString("ghiChu");
//                    String hinhthucTrans = test.getString("hinhthucTrans");
//                    String transId = test.getString("transId");
//                    String createAt = test.getString("createAt");
////                    his = new HistoryTransModel(giaodich, congGiaoDich, hinhthuc, sotienx, trangthai, ghiChu, nickName, hinhthucTrans, transId, id, createAt);
//                    long sotien = Long.parseLong(sotienx);
//                    checknapEnity checkn = new checknapEnity(nickName, hinhthucTrans, sotien);
//                    listtien.add(checkn);
//                }
//            } while (check == false && timeretry > 0);
//            for (checknapEnity s : listtien) {
//                tong = tong + s.getSotien();
//            }
//            return tong;
//        } catch (Exception e) {
//            return 0;
//        }
        return 0;
    }

    public long xinlocELK(String nickname) {
//        try {
//            long tong = 0;
//            ArrayList<checknapEnity> listtien = new ArrayList<>();
//            HashMap<String, Object> conditions = new HashMap<String, Object>();
//            int timeretry = 10;
//            boolean check = false;
//            String sig = "\"successful\":1";
//            int retry = 3;
//            do {
//                retry--;
//                if (retry < 0) {
//                    break;
//                }
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\"duhfusdfhie\"}},{\"match\":{\"hinhthuc.keyword\":\"Nhận Tiền\"}},{\"match\":{\"trangthai.keyword\":\"Thành Công\"}},{\"match\":{\"hinhthucTrans.keyword\":\"GAMER\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/history_user_transaction/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//                timeretry--;
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject person = jsonArray.getJSONObject(i);
//                    JSONObject test = person.getJSONObject("_source");
//                    String nickName = test.getString("nickName");
//                    String id = test.getString("id");
//                    String giaodich = test.getString("giaodich");
//                    String congGiaoDich = test.getString("congGiaoDich");
//                    String hinhthuc = test.getString("hinhthuc");
//                    String sotienx = test.getString("sotien");
//                    String trangthai = test.getString("trangthai");
//                    String ghiChu = test.getString("ghiChu");
//                    String hinhthucTrans = test.getString("hinhthucTrans");
//                    String transId = test.getString("transId");
//                    String createAt = test.getString("createAt");
////                    his = new HistoryTransModel(giaodich, congGiaoDich, hinhthuc, sotienx, trangthai, ghiChu, nickName, hinhthucTrans, transId, id, createAt);
//                    long sotien = Long.parseLong(sotienx);
//                    checknapEnity checkn = new checknapEnity(nickname, hinhthucTrans, sotien);
//                    listtien.add(checkn);
//                }
//
//            } while (check == false && timeretry > 0);
//            for (checknapEnity s : listtien) {
//                tong = tong + s.getSotien();
//            }
//            return tong;
//
//        } catch (Exception e) {
//            return 0;
//        }
        return 0;
    }

    public naptmp tongnapThe(String nickname) {
        long tong = 0;
        long tongthe = 0;
        long tongadmin = 0;
        long tongbank = 0;
        long tongmomo = 0;
        ArrayList<checknapEnity> listtien = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("History_User_transaction");
        conditions.put("nickName", nickname);
        conditions.put("hinhthuc", "recharge");
        conditions.put("trangthai", "Thành công");
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                String sotienx = document.getString((Object) "sotien");
                long sotien = Long.parseLong(sotienx);
                String HinhThucTran = document.getString((Object) "hinhthucTrans");
                checknapEnity check = new checknapEnity(nickname, HinhThucTran, sotien);
                listtien.add(check);
            }
        });
        for (checknapEnity s : listtien) {
//            if(s.getHinhtruc().equalsIgnoreCase("CARD") || s.getHinhtruc().equalsIgnoreCase("BANK") || s.getHinhtruc().equalsIgnoreCase("CodePay")
//                    || s.getHinhtruc().equalsIgnoreCase("ONE_PAY") || s.getHinhtruc().equalsIgnoreCase("ADMIN_TRANSFER_TO_USER")){
            tong = tong + s.getSotien();
            if (s.getHinhtruc().equalsIgnoreCase("CARD")) {
                tongthe = tongthe + s.getSotien();
            } else if (s.getHinhtruc().equalsIgnoreCase("ADMIN_TRANSFER_TO_USER")) {
                tongadmin = tongadmin + s.getSotien();
            } else if (s.getHinhtruc().equalsIgnoreCase("BANK")) {
                tongbank = tongbank + s.getSotien();
            } else if (s.getHinhtruc().equalsIgnoreCase("MOMO")) {
                tongmomo = tongmomo + s.getSotien();
            }

        }
        naptmp ntmp = new naptmp(nickname, tong, tongthe, tongadmin);
        ntmp.setNapbank(tongbank);
        ntmp.setNapmomo(tongmomo);
        return ntmp;
    }

    public long tongrut(String nickname) {
        long tong = 0;
        ArrayList<checknapEnity> listtien = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("History_User_transaction");
        conditions.put("nickName", nickname);
        conditions.put("hinhthuc", "Rút tiền");
        conditions.put("trangthai", "Đã duyệt");
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                String sotienx = document.getString((Object) "sotien");
                long sotien = Long.parseLong(sotienx);
                String HinhThucTran = document.getString((Object) "hinhthucTrans");
                checknapEnity check = new checknapEnity(nickname, HinhThucTran, sotien);
                listtien.add(check);
            }
        });
        for (checknapEnity s : listtien) {
            tong = tong + s.getSotien();


        }
        return tong;
    }

    public long xinloc(String nickname) {
        long tong = 0;
        ArrayList<checknapEnity> listtien = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("History_User_transaction");
        conditions.put("nickName", nickname);
        conditions.put("hinhthuc", "Nhận Tiền");
        conditions.put("trangthai", "Thành Công");
        conditions.put("hinhthucTrans", "GAMER");
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                String sotienx = document.getString((Object) "sotien");
                long sotien = Long.parseLong(sotienx);
                String HinhThucTran = document.getString((Object) "hinhthucTrans");
                checknapEnity check = new checknapEnity(nickname, HinhThucTran, sotien);
                listtien.add(check);
            }
        });
        for (checknapEnity s : listtien) {
            tong = tong + s.getSotien();
        }
        return tong;
    }

    public stkRut LaySTK(String nickname) {
        long tong = 0;
        ArrayList<stkRut> listtien = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("cashout_by_bank_ls");
        conditions.put("Username", nickname);
        conditions.put("Status", "success");
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                String name = document.getString((Object) "BankAccountName");
                String number = document.getString((Object) "BankAccountNumber");
                String bank = document.getString((Object) "BankName");
                long tien = document.getLong((Object) "AmountReal");
                stkRut rutx = new stkRut(nickname, number, name, bank, tien);
                listtien.add(rutx);
            }
        });
        if (listtien.size() == 0) {
            stkRut rutx = new stkRut(nickname, "", "", "", 0);
            listtien.add(rutx);
        }
        return listtien.get(listtien.size() - 1);
    }

    public void Notify(String nickname, long tiennap, long tienrut, long tongrut, long thecao, long xinloc, long napadmin, long sodu, long bankx, long taixiu) {
        Response response = null;
        try {

            Locale localeEN = new Locale("en", "EN");
            NumberFormat en = NumberFormat.getInstance(localeEN);
            String str1 = en.format(tiennap);
            String str2 = en.format(tienrut);
            String str3 = en.format(tongrut);
            String str4 = en.format(thecao);
            String str5 = en.format(xinloc);
            String str6 = en.format(napadmin);
            String str7 = en.format(sodu);
            String str8 = en.format(bankx);
            String str9 = en.format(taixiu);

            String noidung = "- Nick name: " + nickname + "%0A%0A- Số dư: " + str7 + "%0A%0A- Số tiền nạp: " + str1 + "%0A- Nạp ngân hàng và momo: " + str8 + "%0A- Nạp thẻ cào: " + str4 + "%0A- Nạp Admin: " + str6 + "%0A%0A- Số tiền rút: " + str3 + "%0A%0A- Xin lộc: " + str5 + "%0A%0Ayêu cầu rút: " + str2;

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot5281176154:AAHZdXuMV2ZJkkSD6KhUwQcX6MNdT9YSLQ8/sendMessage?chat_id=-772126416&text=" + noidung)
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
    }


}
