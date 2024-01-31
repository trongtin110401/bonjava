package com.vinplay.api.dao;

import com.mongodb.client.MongoDatabase;
import com.vinplay.api.entities.CodeTT;
import com.vinplay.api.entities.UseCode;
import com.vinplay.api.entities.UserOTP;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class ManageGiftCodeDAO {
    public synchronized boolean CheckSpecialGiftCodes(String gift_code) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("status", 0);
        conditions.put("gift_code", gift_code);
        long count = db.getCollection("special_gift_code").count((Bson) new Document(conditions));
        return count > 0;
    }

    public CodeTT getCodeTT(String code) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");

        CodeTT codeTT = new CodeTT();
        String sql = "SELECT * FROM vinplay.giftcodett where code = ? and stop = 0 order by timelog desc LIMIT 1";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, code);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            codeTT = new CodeTT(rs.getString("code"), rs.getInt("money"), rs.getString("timelog"), rs.getInt("stop"));
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return codeTT;
    }

    public UserOTP getUserActiveOTP(String nickname) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        UserOTP uotp = null;
        String sql = "SELECT * FROM vinplay.active WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"), rs.getInt("active"), rs.getLong("create_time"), rs.getLong("active_time"), rs.getInt("turn"));
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return uotp;
    }

    public boolean checkUseCode(String nickname) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        boolean check = false;
        String sql = "SELECT * FROM vinplay.codetanthu WHERE nickname=? AND `use`=1";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            check = true;
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return check;
    }

    public void insertCodeTanThu(UseCode usercode) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "INSERT INTO vinplay.codetanthu (`nickname`,`CODE`, `use`, `timelog`, `username`, `phone`, `active`) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, usercode.getNickname());
            stm.setString(2,usercode.getCode());
            stm.setInt(3, usercode.getUse());
            stm.setString(4, usercode.getTimelog());
            stm.setString(5, usercode.getUsername());
            stm.setString(6, usercode.getPhone());
            stm.setInt(7, usercode.getActive());

            int rs = stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        }catch (Exception e) {
            throw e;
        }
    }
}
