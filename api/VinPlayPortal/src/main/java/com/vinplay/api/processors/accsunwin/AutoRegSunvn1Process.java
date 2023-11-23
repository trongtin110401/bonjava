package com.vinplay.api.processors.accsunwin;

import com.vinplay.api.entities.UserOTP;
import com.vinplay.api.otp.OTPELK;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.pools.ConnectionPool;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AutoRegSunvn1Process implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String uname = request.getParameter("username");
            String pwd = request.getParameter("password");
            String nickname = request.getParameter("nickname");
            String codedl = "sunvn1";
            String tien = request.getParameter("tien");
            String safe = request.getParameter("safe");
            String phone = request.getParameter("phone");


            long money = Long.parseLong(tien);
            long ketsat = Long.parseLong(safe);
            long tong_tien = money+ketsat;
            AutoRegSunvn1 auto = new AutoRegSunvn1();
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
                boolean taotienCheck = auto.taoTien(nickname, tong_tien+"");
                long create_time_2 = new Date().getTime();
                Date d = new Date(create_time_2);
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdfDate.format(d);
                UserOTP uotp = new UserOTP(nickname, uname,phone, "666666", 1, create_time_2, create_time_2, 1, strDate);
                OTPELK oelk = new OTPELK();
                InsertActive(uotp);
                oelk.InsertActiveELK(uotp);
                UpdateSDTOTP(nickname,phone);
                return "{\"errorCode\":\"200\"}";
            }else{
                return "{\"errorCode\":\"500\"}";
            }
        }catch (Exception e) {
            e.printStackTrace();
            return "{\"errorCode\":\"600\",\"msg\":\""+e.getMessage()+"\"}";
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
