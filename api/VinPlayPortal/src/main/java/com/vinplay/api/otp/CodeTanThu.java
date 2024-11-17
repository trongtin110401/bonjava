package com.vinplay.api.otp;

import com.vinplay.api.entities.CodeNewBie;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CodeTanThu {

    public boolean checkUse(String nickname) throws SQLException {
        String sql = "SELECT * FROM codetanthu WHERE nickname=?";

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setString(1, nickname);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    CodeNewBie code = new CodeNewBie(rs.getString("username"), rs.getString("nickname"), rs.getString("code"), rs.getInt("use"));
                    return code.getUse() == 1;
                }
            }
        }
        return false;
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

    public void InsertCode(CodeNewBie code) throws Exception {
        String sql = "INSERT INTO vinplay.codetanthu (username, nickname, code, use) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setString(1, code.getUsername());
            stm.setString(2, code.getNickname());
            stm.setString(3, code.getCode());
            stm.setInt(4, code.getUse());

            stm.executeUpdate();
        }
    }


}
