package com.vinplay.api.backend.processors.xulyaccsun;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.pools.ConnectionPool;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteUserSunProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String username = request.getParameter("username");
        String nickname = request.getParameter("nickname");


        if(username == null && nickname == null){
            return "{\"trangthai\":\"That Bai\"}";
        }else if(username != null && nickname == null){
            DeleteUser(username.trim());
            return "{\"trangthai\":\"Thanh Cong\"}";
        }else if(username == null && nickname != null){
            DeleteUserByNickname(nickname.trim());
            return "{\"trangthai\":\"Thanh Cong\"}";
        }else if(username.trim().length() == 0 && nickname.trim().length() > 0){
            DeleteUserByNickname(nickname.trim());
            return "{\"trangthai\":\"Thanh Cong\"}";
        }else if(username.trim().length() > 0 && nickname.trim().length() == 0){
            DeleteUser(username.trim());
            return "{\"trangthai\":\"Thanh Cong\"}";
        }
        else{
            return "{\"trangthai\":\"That Bai\"}";
        }

    }
    public void DeleteUser(String username) {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "DELETE FROM vinplay.users where `user_name` = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, username);

            stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void DeleteUserByNickname(String nickname) {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "DELETE FROM vinplay.users where `nick_name` = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);

            stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}