package com.vinplay.api.processors.cashout;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.processors.momo.ELKAutoBankNew;
import com.vinplay.lognaprut.impl.InsertELK;
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

public class GenCommentBank {
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            int number = randomNumber(0, ALPHA_NUMERIC.length() - 1);
            char ch = ALPHA_NUMERIC.charAt(number);
            sb.append(ch);
        }
        return sb.toString();
    }

    public synchronized void GenContent2(String nickname){
        ELKAutoBankNew elk = new ELKAutoBankNew();
        GenCommentBank gen = new GenCommentBank();
        boolean check = false;
        do{
            String commentcode = "LX"+gen.randomMaChuyenTien().toUpperCase();
            String nick = elk.GetNicknameByCode(commentcode);
            String codex = elk.GetCodeByNick(nickname);
            if(nick == null && codex == null){
                elk.InsertCodeUserBankELK(nickname, commentcode);
                check = true;
            }
        }while (check == false);

    }

    public void insertCodepay(String nickname, String codepay){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("nickname", nickname);
            doc.append("codepay", codepay);
            doc.append("use", 0);
            doc.append("createAt", timeAt);
            col.insertOne(doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void updateCodepay(String nickname, boolean use, String codepay){
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
                    int use = document.getInteger((Object) "nickname");
                    String timelog = document.getString((Object) "createAt");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog);
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

    public synchronized String TaoMaCodePay(String nickname) throws ParseException {
        boolean check = false;
        String codepay_final = "";
        do{
            String commentcode = "LX"+randomMaChuyenTien().toUpperCase();
            Codepayok codepay1 = findCodepay(commentcode);
            Codepayok codepay2 = findNickname(nickname);
            if(codepay1 == null && codepay2 == null){
                insertCodepay(nickname, commentcode);
                check = true;
                codepay_final = commentcode;
            }else if(codepay2 != null && codepay1 == null){
                Long time_check = new Date().getTime();
                SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date out = sim.parse(codepay2.getTimelog());
                Long timelog = out.getTime();
                Long time_end = time_check - timelog;
                if(codepay2.getUse() == 1){
                    updateCodepay(nickname,false, commentcode);
                    check = true;
                    codepay_final = commentcode;
                }else if(codepay2.getUse() == 0 && time_end > 1200000 ){
                    updateCodepay(nickname,false, commentcode);
                    check = true;
                    codepay_final = commentcode;
                }else{
                    check = false;
                }

            }
        }while (check == false);
        return codepay_final;
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
                    int use = document.getInteger((Object) "nickname");
                    String timelog = document.getString((Object) "createAt");
                    Codepayok cp = new Codepayok(nickname, codepay, use, timelog);
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

    public String NapTien(String nickname) throws ParseException {
        String commentCode = TaoMaCodePay(nickname);
        return commentCode;
    }




}
