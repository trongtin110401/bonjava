package com.vinplay.api.otp;

import com.vinplay.api.entities.UserOTP;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OTPprocess {
    public boolean checkUserActiveOTP(String nickname) throws SQLException {
        String sql = "SELECT * FROM active WHERE nickname=?";
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);
            rs = stm.executeQuery();
            if (rs.next()) {
                UserOTP uotp = new UserOTP(
                        rs.getString("nickname"),
                        rs.getString("username"),
                        rs.getString("phone"),
                        rs.getString("otp"),
                        rs.getInt("active"),
                        rs.getLong("creat_time"),
                        rs.getLong("active_time"),
                        rs.getInt("turn"),
                        rs.getString("timelog")
                );
                return uotp.getActive() == 1;
            }

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return false;
    }


    public synchronized void InsertActive(UserOTP uotp) throws Exception {
        String sql = "INSERT INTO vinplay.active (nickname, username, phone, otp, active, creat_time, active_time, turn, timelog) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, uotp.getNickname());
            stm.setString(2, uotp.getUsername());
            stm.setString(3, uotp.getPhone());
            stm.setString(4, uotp.getOtp());
            stm.setInt(5, uotp.getActive());
            stm.setLong(6, uotp.getCreat_time());
            stm.setLong(7, uotp.getActive_time());
            stm.setInt(8, uotp.getTurn());
            stm.setString(9, uotp.getTimelog());

            stm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace(); // Ideally, use a logging framework for production code
            throw e; // Rethrow the exception to maintain the method contract
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }


    public synchronized void UpdateActive(UserOTP uotp) throws Exception {
        String sql = "UPDATE vinplay.active SET phone=?, active=?, creat_time=?, active_time=?, turn=?, otp=?, timelog=? WHERE nickname=?";

        PreparedStatement stm = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);

            stm.setString(1, uotp.getPhone());
            stm.setInt(2, uotp.getActive());
            stm.setLong(3, uotp.getCreat_time());
            stm.setLong(4, uotp.getActive_time());
            stm.setInt(5, uotp.getTurn());
            stm.setString(6, uotp.getOtp());
            stm.setString(7, uotp.getTimelog());
            stm.setString(8, uotp.getNickname());

            stm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace(); // Ideally, use a logging framework for production code
            throw e; // Rethrow the exception to maintain the method contract
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }


    public UserOTP GetActive(String nickname) throws Exception {
        UserOTP uotp = null;
        String sql = "SELECT * FROM vinplay.active WHERE nickname=?";

        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);

            stm.setString(1, nickname);

            rs = stm.executeQuery();
            if (rs.next()) {
                uotp = new UserOTP(
                        rs.getString("nickname"),
                        rs.getString("username"),
                        rs.getString("phone"),
                        rs.getString("otp"),
                        rs.getInt("active"),
                        rs.getLong("create_time"),
                        rs.getLong("active_time"),
                        rs.getInt("turn"),
                        rs.getString("timelog")
                );
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ideally, use a logging framework for production code
            throw e; // Rethrow the exception to maintain the method contract
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return uotp;
    }


    public UserOTP getUserOtpFind(String nickname) throws Exception {
        UserOTP uotp = null;
        String sql = "SELECT * FROM vinplay.active WHERE nickname=? LIMIT 1";

        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);

            stm.setString(1, nickname);

            rs = stm.executeQuery();
            if (rs.next()) {
                uotp = new UserOTP(
                        rs.getString("nickname"),
                        rs.getString("username"),
                        rs.getString("phone"),
                        rs.getString("otp"),
                        rs.getInt("active"),
                        rs.getLong("creat_time"),
                        rs.getLong("active_time"),
                        rs.getInt("turn"),
                        rs.getString("timelog")
                );
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ideally, use a logging framework for production code
            throw e; // Rethrow the exception to maintain the method contract
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return uotp;
    }


    public boolean CheckOTPok(String nickname, String otp) throws Exception {
        boolean check = false;
        String sql = "SELECT * FROM vinplay.active WHERE nickname=? AND otp=?";

        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);

            stm.setString(1, nickname);
            stm.setString(2, otp);

            rs = stm.executeQuery();
            if (rs.next()) {
                // Create UserOTP object if record exists (optional, depending on use)
                UserOTP uotp = new UserOTP(
                        rs.getString("nickname"),
                        rs.getString("username"),
                        rs.getString("phone"),
                        rs.getString("otp"),
                        rs.getInt("active"),
                        rs.getLong("creat_time"),
                        rs.getLong("active_time"),
                        rs.getInt("turn"),
                        rs.getString("timelog")
                );
                check = true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ideally, use a logging framework for production code
            throw e; // Rethrow the exception to maintain the method contract
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return check;
    }


    public boolean CheckPhoneExit(String phone) throws Exception {
        boolean check = false;
        String sql = "SELECT * FROM vinplay.active WHERE phone=?";

        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, phone);

            rs = stm.executeQuery();
            if (rs.next()) {
                // Create UserOTP object if record exists
                UserOTP uotp = new UserOTP(
                        rs.getString("nickname"),
                        rs.getString("username"),
                        rs.getString("phone"),
                        rs.getString("otp"),
                        rs.getInt("active"),
                        rs.getLong("creat_time"),
                        rs.getLong("active_time"),
                        rs.getInt("turn"),
                        rs.getString("timelog")
                );
                check = true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ideally, use a logging framework for production code
            throw e; // Rethrow the exception to maintain the method contract
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
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


//    public ArrayList<UserOTP> GetListOTP() throws Exception {
//        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
//        ArrayList<UserOTP> list_otp = new ArrayList<>();
//        String sql = "SELECT * FROM vinplay.active";
//        PreparedStatement stm = conn.prepareStatement(sql);
//        ResultSet rs = stm.executeQuery();
//        while (rs.next()) {
//            UserOTP uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("creat_time"), rs.getLong("active_time"), rs.getInt("turn"), rs.getString("timelog"));
//            list_otp.add(uotp);
//        }
//        rs.close();
//        stm.close();
//        if (conn != null) {
//            conn.close();
//        }
//        return list_otp;
//    }

//    public synchronized void DeleteOTP(String nickname) throws Exception {
//        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
//        ArrayList<UserOTP> list_otp = new ArrayList<>();
//        String sql = "DELETE FROM vinplay.active WHERE nickname=?";
//        PreparedStatement stm = conn.prepareStatement(sql);
//        stm.setString(1, nickname);
//        ResultSet rs = stm.executeQuery();
//
//        rs.close();
//        stm.close();
//        if (conn != null) {
//            conn.close();
//        }
//
//    }

    public synchronized void UpdateSDTOTP(String nickname, String phone) throws Exception {
        String sql = "UPDATE vinplay.users SET mobile=? WHERE nick_name=?";
        ArrayList<UserOTP> list_otp = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, phone);
            stm.setString(2, nickname);
            stm.executeUpdate();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public boolean GetMobile(String nickname) throws Exception {
        String phone = null;
        boolean check;
        String sql = "SELECT mobile FROM vinplay.users WHERE nick_name=?";
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);

            rs = stm.executeQuery();
            if (rs.next()) {
                phone = rs.getString("mobile"); // Using column name for clarity
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ideally, use a logging framework for production code
            throw e; // Rethrow the exception to maintain the method contract
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        if (phone == null || phone.trim().isEmpty()) {
            check = false;
        } else {
            check = true;
        }

        return check;
    }


}
