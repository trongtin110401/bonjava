package com.vinplay.api.backend.models;

import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class OTPDAO {
    public ArrayList<UserOTP> GetListOTP() throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        ArrayList<UserOTP> list_otp = new ArrayList<>();
        String sql = "SELECT * FROM vinplay.active ORDER BY `creat_time` DESC LIMIT 1000";
        PreparedStatement stm = conn.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            UserOTP uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"),rs.getInt("active"), rs.getLong("creat_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
            list_otp.add(uotp);
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return  list_otp;
    }

    public void DeleteOTP(String nickname){
        try{
            Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            ArrayList<UserOTP> list_otp = new ArrayList<>();
            String sql = "DELETE FROM vinplay.active WHERE nickname=?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);
            stm.executeUpdate();
            stm.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void UpdateSDTOTP(String nickname, String phone) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String sql = "UPDATE vinplay.active SET phone=? WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, phone);
        stm.setString(2, nickname);
        stm.executeUpdate();

        stm.close();
        if (conn != null) {
            conn.close();
        }

    }

    public synchronized void UpdateSDTOTP2(String nickname, String phone) throws Exception{
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

    public void UpdateSDTUserDelete(String nickname) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String phone = null;
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

    public synchronized void UpdateActive(UserOTP uotp) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "UPDATE vinplay.active SET phone=?, active=?, creat_time=?, active_time=?, turn=?, otp=?, timelog=? WHERE nickname=?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, uotp.getPhone());
            stm.setInt(2, uotp.getActive());
            stm.setLong(3, uotp.getCreat_time());
            stm.setLong(4, uotp.getActive_time());
            stm.setInt(5, uotp.getTurn());
            stm.setString(6, uotp.getOtp());
            stm.setString(7, uotp.getTimelog());
            stm.setString(8, uotp.getNickname());

            int rs = stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        }catch (Exception e) {
            throw e;
        }
    }

    public UserOTP GetActive(String nickname) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        UserOTP uotp = null;
        String sql = "SELECT * FROM vinplay.active WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"),rs.getInt("active"), rs.getLong("creat_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return  uotp;
    }


}
