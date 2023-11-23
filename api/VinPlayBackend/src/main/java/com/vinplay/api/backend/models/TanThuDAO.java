package com.vinplay.api.backend.models;

import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TanThuDAO {
    public void InsertCodeTT(CodeTT codett) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "INSERT INTO vinplay.giftcodett (code,money, timelog,stop) VALUES(?,?,?,?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, codett.getCode());
            stm.setInt(2, codett.getMoney());
            stm.setString(3, codett.getTimelog());
            stm.setInt(4, codett.getStop());
            int rs = stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            throw e;
        }

    }

    public void DeleteCodeTT(String codett) {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "DELETE FROM vinplay.giftcodett where `code` = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, codett);

            stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void UpdateOTP(String code, int stop) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "UPDATE vinplay.giftcodett SET `stop`=? WHERE `code`=?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1,stop);
            stm.setString(2, code);
            int rs = stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        }catch (Exception e) {
            throw e;
        }
    }

    public ArrayList<UserCode> GetUserCodeTT(String code) {
        ArrayList<UserCode> list_user = new ArrayList<>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "SELECT * FROM vinplay.codetanthu WHERE `code`=?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, code);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UserCode uc = new UserCode(rs.getString("nickname"), rs.getString("code"), rs.getInt("use"), rs.getString("timelog"), rs.getString("username"), rs.getString("phone"), rs.getInt("active"));
                list_user.add(uc);
            }
            rs.close();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException sql) {
            // empty catch block
        }
        return list_user;
    }

    public ArrayList<CodeTT> GetCodeTT() {
        ArrayList<CodeTT> list_code = new ArrayList<>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "SELECT * FROM vinplay.giftcodett";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                CodeTT tt = new CodeTT(rs.getString("code"), rs.getInt("money"), rs.getString("timelog"), rs.getInt("stop"));
                list_code.add(tt);
            }
            rs.close();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException sql) {
            // empty catch block
        }
        return list_code;
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

public ArrayList<FullUserCode> GetFullUserCode(String code){
    try {
        ArrayList<FullUserCode> list_full_usercode = new ArrayList<>();
    TanThuDAO ttdao = new TanThuDAO();
    ArrayList<UserCode> list_user = ttdao.GetUserCodeTT(code);
    for(UserCode uc : list_user){
        UserOTP uo = ttdao.GetActive(uc.getNickname());
        FullUserCode full = new FullUserCode(uo.getUsername(), uc.getNickname(),uo.getPhone(), uo.getActive(),uc.getCode(),uc.getUse(), uc.getTimelog());
        list_full_usercode.add(full);
    }
    return list_full_usercode;
    } catch (Exception e) {
        e.printStackTrace();
    }
        return null;
}

}
