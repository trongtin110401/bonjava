/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.response.TopRechargeMoneyResponse
 */
package com.vinplay.dal.dao.impl;

import com.vinplay.dal.dao.TopRechargeMoneyDAO;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.response.TopRechargeMoneyResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopRechargeMoneyDAOImpl
implements TopRechargeMoneyDAO {
    @Override
    public List<TopRechargeMoneyResponse> getTopRechargeMoney(int top, String nickName, int page, int bot) throws SQLException {
        ArrayList<TopRechargeMoneyResponse> results = new ArrayList<TopRechargeMoneyResponse>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            int numStart = (page - 1) * 50;
            int numEnd = 50;
            String sql = "";
            String conditions = "";
            String orderby = " ORDER BY `recharge_money` desc";
            String limit = " LIMIT " + numStart + "," + 50;
            String query = "SELECT nick_name,recharge_money FROM users where 1=1 ";
            if (top > 0) {
                sql = "SELECT nick_name,recharge_money FROM users where 1=1 " + conditions + " ORDER BY `recharge_money` desc" + limit;
            }
            if (bot == 0) {
                conditions = " AND is_bot=" + bot;
                sql = "SELECT nick_name,recharge_money FROM users where 1=1 " + conditions + " ORDER BY `recharge_money` desc" + limit;
            }
            if (bot == 1) {
                sql = "SELECT nick_name,recharge_money FROM users where 1=1 " + conditions + " ORDER BY `recharge_money` desc" + limit;
            }
            if (!nickName.equals("") && !nickName.equals(null)) {
                conditions = " AND nick_name='" + nickName + "'";
                sql = "SELECT nick_name,recharge_money FROM users where 1=1 " + conditions + " ORDER BY `recharge_money` desc" + limit;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TopRechargeMoneyResponse entry = new TopRechargeMoneyResponse();
                entry.userName = rs.getString("user_name");
                entry.money = rs.getLong("recharge_money");
                results.add(entry);
            }
            rs.close();
            stmt.close();
        }
        return results;
    }
}

