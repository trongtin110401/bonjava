package com.vinplay.api.processors.xulylinhtinh;

import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetUsers {

    public ArrayList<String> getNicknameOK(int num) {
        ArrayList<String> list_nickname = new ArrayList<>();
        String sql = "SELECT nick_name FROM users WHERE is_bot = 0 AND nick_name IS NOT NULL LIMIT ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, num);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    String nickname = rs.getString("nick_name");
                    list_nickname.add(nickname);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list_nickname;
    }



}
