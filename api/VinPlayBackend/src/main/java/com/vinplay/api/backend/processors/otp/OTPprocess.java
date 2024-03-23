package com.vinplay.api.backend.processors.otp;

import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OTPprocess {
    public boolean checkUserActiveOTP(String nickname) throws SQLException {
        boolean check_null = false;
        boolean check = false;
        UserOTP uotp = null;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String sql = "SELECT * FROM active WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("create_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
        }

        if (uotp == null) {
            check_null = false;
        } else {
            check_null = true;
        }

        if (check_null == true) {
            if (uotp.getActive() == 1) {
                check = true;
            } else {
                check = false;
            }
        } else {
            check = false;
        }

        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return check;
    }

    public synchronized void InsertActive(UserOTP uotp) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "INSERT INTO vinplay.active (nickname,username,phone,otp,active,create_time,active_time,turn,timelog) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, uotp.getNickname());
            stm.setString(2, uotp.getUsername());
            stm.setString(3, uotp.getPhone());
            stm.setString(4, uotp.getOtp());
            stm.setInt(5, uotp.getActive());
            stm.setLong(6, uotp.getCreat_time());
            stm.setLong(7, uotp.getActive_time());
            stm.setInt(8, uotp.getTurn());
            stm.setString(9, uotp.getTimelog());
            int rs = stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public synchronized void UpdateActive(UserOTP uotp) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "UPDATE vinplay.active SET phone=?, active=?, create_time=?, active_time=?, turn=?, otp=?, timelog=? WHERE nickname=?";
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
        } catch (Exception e) {
            throw e;
        }
    }

    public UserOTP GetActive(String nickname) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        UserOTP uotp = null;
        String sql = "SELECT * FROM vinplay.active WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("create_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return uotp;
    }

    public UserOTP getUserOtpFind(String nickname) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        UserOTP uotp = null;
        String sql = "SELECT * FROM vinplay.active WHERE nickname=? limit 1";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("create_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return uotp;
    }

    public boolean CheckOTPok(String nickname, String otp) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        UserOTP uotp = null;
        boolean check = false;

        String sql = "SELECT * FROM vinplay.active WHERE nickname=? AND otp=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        stm.setString(2, otp);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("create_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
        }
        if (uotp == null) {
            check = false;
        } else {
            check = true;
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return check;
    }

    public boolean CheckPhoneExit(String phone) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        UserOTP uotp = null;
        boolean check = false;

        String sql = "SELECT * FROM vinplay.active WHERE phone=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, phone);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("create_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
        }
        if (uotp == null) {
            check = false;
        } else {
            check = true;
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return check;
    }

    public boolean ActiveOTP(Long create_time, Long active_time, int turn) {
        boolean check = false;
        if (active_time - create_time <= 180000 && turn <= 3) {
            check = true;
        } else {
            check = false;
        }
        return check;
    }

    public boolean CheckTimeGetOTP(Long create_time, Long get_time) {
        boolean check = false;
        if (get_time - create_time >= 180000) {
            check = true;
        } else {
            check = false;
        }
        return check;
    }


    public ArrayList<UserOTP> GetListOTPByTime(long start, long end) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        ArrayList<UserOTP> list_otp = new ArrayList<>();
        String sql = "SELECT * FROM vinplay.active where create_time >= ? and create_time <=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setLong(1, start);
        stm.setLong(2, end);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            UserOTP uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("create_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
            list_otp.add(uotp);
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return list_otp;
    }

    public ArrayList<UserOTP> GetListOTP() throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        ArrayList<UserOTP> list_otp = new ArrayList<>();
        String sql = "SELECT * FROM vinplay.active";
        PreparedStatement stm = conn.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            UserOTP uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("create_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
            list_otp.add(uotp);
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return list_otp;
    }

    public synchronized void DeleteOTP(String nickname) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        ArrayList<UserOTP> list_otp = new ArrayList<>();
        String sql = "DELETE FROM vinplay.active WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();

        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }

    }

    public synchronized void UpdateSDTOTP(String nickname, String phone) throws Exception {
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

    public boolean GetMobile(String nickname) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String phone = null;
        boolean check = false;
        String sql = "SELECT * FROM vinplay.users WHERE nick_name=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            phone = rs.getString(8);
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        if (phone == null) {
            check = false;
        } else if (phone.trim().length() == 0) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }


}
