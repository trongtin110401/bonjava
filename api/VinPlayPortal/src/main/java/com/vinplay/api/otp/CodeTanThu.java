package com.vinplay.api.otp;

import com.vinplay.api.entities.CodeNewBie;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CodeTanThu {

    public boolean checkUse(String nickname) throws SQLException {
        boolean check = false;
        CodeNewBie code = null;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String sql = "SELECT * FROM codetanthu WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        while (rs.next()){
            code = new CodeNewBie(rs.getString("username"), rs.getString("nickname"), rs.getString("code"), rs.getInt("use"));
        }
        if(code == null){
            check = false;
        }else{
            if(code.getUse() == 1){
                check = true;
            }else{
                check = false;
            }
        }

        return check;
    }

    public boolean checkCodeChinhxac(String code_input, String code_tanthu){
        boolean check = false;
        if(code_input.equalsIgnoreCase(code_tanthu)){
            check = true;
        }else{
            check = false;
        }
        return check;
    }

    public void InsertCode(CodeNewBie code) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "INSERT INTO vinplay.codetanthu (username,nickname,code,use) VALUES(?,?,?,?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, code.getUsername());
            stm.setString(2, code.getNickname());
            stm.setString(3, code.getCode());
            stm.setInt(4, code.getUse());
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
