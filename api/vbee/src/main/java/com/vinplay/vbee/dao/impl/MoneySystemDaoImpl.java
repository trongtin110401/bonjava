/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.pools.ConnectionPool
 */
package com.vinplay.vbee.dao.impl;

import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.dao.MoneySystemDao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MoneySystemDaoImpl
implements MoneySystemDao {
    @Override
    public boolean updateMoneySystem(String name, long money) throws SQLException {
        boolean success = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        CallableStatement call = null;
        call = conn.prepareCall("CALL update_money_system(?,?,?)");
        int param = 1;
        call.setString(param++, name);
        call.setLong(param++, money);
        call.registerOutParameter(param, -6);
        call.executeUpdate();
        success = money > 0L ? call.getInt(param) == 2 : call.getInt(param) == 1;
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return success;
    }

    @Override
    public long getMoneySystem(String name) throws SQLException {
        long money = 0L;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String sql = "SELECT money FROM money_system WHERE name=?";
        PreparedStatement stm = conn.prepareStatement("SELECT money FROM money_system WHERE name=?");
        stm.setString(1, name);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            money = rs.getLong("money");
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return money;
    }
}

