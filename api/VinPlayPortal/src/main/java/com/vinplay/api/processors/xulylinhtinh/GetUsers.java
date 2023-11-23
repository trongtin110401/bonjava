package com.vinplay.api.processors.xulylinhtinh;

import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetUsers {

    public ArrayList<String> getNicknameOK(int num){
        try {
            ArrayList<String> list_nickname = new ArrayList<>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String sql = "SELECT * FROM users WHERE `is_bot` = 0 AND `nick_name` IS NOT NULL LIMIT ?,300;";
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM users WHERE `is_bot` = 0 AND `nick_name` IS NOT NULL LIMIT ?,300;");
            stm.setInt(1, num);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                    String nickname = rs.getString("nick_name");
                    list_nickname.add(nickname);
            }
            rs.close();
            stm.close();
            if (conn != null) {
                conn.close();
            }
            return list_nickname;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }


}
