package com.vinplay.api.processors;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class GencommentCodepay {
    public String GenContent(String nickname){
        //txt to decimal
        char[] nick_char = nickname.toCharArray();
        ArrayList<Integer> list1 = new ArrayList<>();
        for(char c : nick_char){
            int y = (int) c;
            list1.add(y);
        }

        //decimal to hex
        String hex = "";
        for(int i : list1){
            String h = Integer.toHexString(i);
            hex = hex+h+",";
        }

        // hex to key
        hex = hex.toUpperCase();
        int sum = 0;
        int size=3;
        int range=65536;//16 bit
        ArrayList<String> list3 = new ArrayList<>();
        String[] list2 = hex.split(",");
        for(String s : list2){
            int u = Integer.parseInt(s,16);
            sum = sum + u;
        }
        sum %= range;
        String sum1 = Integer.toHexString(sum).toUpperCase();
        String sum2 = "000000000" + sum1;
        String sum3 = sum2.substring(sum2.length()-size);
        sum3 = nickname.substring(0, 2).toUpperCase() + sum3 + nickname.length();
        return sum3;

    }

    private static final String alpha = "abcdefghijklmnopqrstuvwxyz"; // a-z
    private static final String alphaUpperCase = alpha.toUpperCase(); // A-Z
    private static final String digits = "0123456789"; // 0-9
    private static final String ALPHA_NUMERIC = alphaUpperCase + digits;
    private static Random generator = new Random();
    public static int randomNumber(int min, int max) {
        return generator.nextInt((max - min) + 1) + min;
    }

    public String randomMaChuyenTien() {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < 7; i++) {
//            int number = randomNumber(0, ALPHA_NUMERIC.length() - 1);
//            char ch = ALPHA_NUMERIC.charAt(number);
//            sb.append(ch);
//        }
//        return sb.toString();
        return genUniQueCodepay();
    }

    public static String RANDOM_STRING_BY_LENGTH(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static String base_convert(final String inputValue, final int fromBase, final int toBase) {
        if (fromBase < 2 || fromBase > 36 || toBase < 2 || toBase > 36) {
            return null;
        }
        String ret = null;
        try {
            Thread.sleep(1);
            ret = Long.toString(Long.parseLong(inputValue, fromBase), toBase);
        } catch(Exception ex) {
            ex.printStackTrace();
        };
        return ret;
    }

    public static synchronized String genUniQueCodepay() {
        String data= base_convert((new Date().getTime())+"",10,36);
        data = data.substring(1,data.length()).toUpperCase();
        System.out.println(data);
        if(data.contains("LX")) {
            data.replaceAll("LX", RANDOM_STRING_BY_LENGTH(2));
        }
        return data;
    }


    public void insertCodepay(String nickname, String codepay, String bankname, String transid){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("nickname", nickname);
            doc.append("codepay", codepay);
            doc.append("use", 0);
            doc.append("createAt", timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.insertOne((Object)doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void insertCodepayDL(String nickname, String codepay, String bankname, String transid){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("codepay_daily");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("nickname", nickname);
            doc.append("codepay", codepay);
            doc.append("use", 0);
            doc.append("createAt", timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.insertOne((Object)doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void insertCodepayOK88(String nickname, String codepay, String bankname, String transid){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK88");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("nickname", nickname);
            doc.append("codepay", codepay);
            doc.append("use", 0);
            doc.append("createAt", timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.insertOne((Object)doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void insertCodepayOK99(String nickname, String codepay, String bankname, String transid){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK99");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("nickname", nickname);
            doc.append("codepay", codepay);
            doc.append("use", 0);
            doc.append("createAt", timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.insertOne((Object)doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void insertCodepayOK88Log(String nickname, String codepay, String bankname, String transid){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK88Log");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("nickname", nickname);
            doc.append("codepay", codepay);
            doc.append("use", 0);
            doc.append("createAt", timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.insertOne((Object)doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }


    public void insertCodepayOK99Log(String nickname, String codepay, String bankname, String transid){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK99Log");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("nickname", nickname);
            doc.append("codepay", codepay);
            doc.append("use", 0);
            doc.append("createAt", timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.insertOne((Object)doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }


    public void updateCodepay(String nickname, boolean use, String codepay, String bankname, String transid){
        try {
            int check = 0;
            if(use == true){
                check = 1;
            }else{
                check = 0;
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("codepay", codepay);
            doc.append("use", check);
            doc.append("createAt",timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void updateCodepayOK88Log(String nickname, boolean use, String codepay, String bankname, String transid){
        try {
            int check = 0;
            if(use == true){
                check = 1;
            }else{
                check = 0;
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK88Log");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("codepay", codepay);
            doc.append("use", check);
            doc.append("createAt",timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.updateOne((Bson) new Document("transid", transid), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void updateCodepayOK99Log(String nickname, boolean use, String codepay, String bankname, String transid){
        try {
            int check = 0;
            if(use == true){
                check = 1;
            }else{
                check = 0;
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK99Log");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("codepay", codepay);
            doc.append("use", check);
            doc.append("createAt",timeAt);
            doc.append("bankname", bankname);
            doc.append("transid", transid);
            col.updateOne((Bson) new Document("transid", transid), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }


    public void updateCodepayOK88(String nickname, boolean use, String codepay, String bankname, String transid){
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
            doc.append("transid", transid);
            col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void updateCodepayOK99(String nickname, boolean use, String codepay, String bankname, String transid){
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
            doc.append("transid", transid);
            col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public Codepayok findCodepay(String codepay){
        try {
            ArrayList<Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            conditions.put("codepay", codepay);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String nickname = document.getString((Object) "nickname");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if(listcodepay.size() == 0){
                return null;
            }else{
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
        return null;
    }

    public Codepayok findCodepayDL(String codepay){
        try {
            ArrayList<Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("codepay_daily");
            conditions.put("codepay", codepay);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String nickname = document.getString((Object) "nickname");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if(listcodepay.size() == 0){
                return null;
            }else{
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
        return null;
    }

    public Codepayok findCodepayOK88(String codepay){
        try {
            ArrayList<Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK88");
            conditions.put("codepay", codepay);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String nickname = document.getString((Object) "nickname");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if(listcodepay.size() == 0){
                return null;
            }else{
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
        return null;
    }

    public Codepayok findCodepayOK99(String codepay){
        try {
            ArrayList<Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK99");
            conditions.put("codepay", codepay);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String nickname = document.getString((Object) "nickname");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if(listcodepay.size() == 0){
                return null;
            }else{
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
        return null;
    }


    public Codepayok findNickname(String nickname){
        try {
            ArrayList<Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            conditions.put("nickname", nickname);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String codepay = document.getString((Object) "codepay");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if(listcodepay.size() == 0){
                return null;
            }else{
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
        return null;
    }

    public Codepayok findNicknameDL(String nickname){
        try {
            ArrayList<Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("codepay_daily");
            conditions.put("nickname", nickname);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String codepay = document.getString((Object) "codepay");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if(listcodepay.size() == 0){
                return null;
            }else{
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
        return null;
    }

    public Codepayok findNicknameOK88(String nickname){
        try {
            ArrayList<Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK88");
            conditions.put("nickname", nickname);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String codepay = document.getString((Object) "codepay");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if(listcodepay.size() == 0){
                return null;
            }else{
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
        return null;
    }

    public Codepayok findNicknameOK99(String nickname){
        try {
            ArrayList<Codepayok> listcodepay = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK99");
            conditions.put("nickname", nickname);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String codepay = document.getString((Object) "codepay");
                    int use = document.getInteger((Object) "use");
                    String timelog = document.getString((Object) "createAt");
                    String bankname = document.getString((Object) "bankname");
                    String transid = document.getString((Object) "transid");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog, bankname, transid);
                    listcodepay.add(cp);
                }
            });
            if(listcodepay.size() == 0){
                return null;
            }else{
                return listcodepay.get(0);
            }

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
        return null;
    }

}
