/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.pools.ConnectionPool
 */
package com.vinplay.dal.dao.impl;

import com.vinplay.dal.dao.GetUserIndexDAO;
import com.vinplay.vbee.common.pools.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetUserIndexDAOImpl
implements GetUserIndexDAO {
    @Override
    public int getRegister(String timeStart, String timeEnd) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        int res = 0;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = " SELECT COUNT(*) AS total  FROM vinplay.users  WHERE `create_time` >= ?    AND `create_time` <= ? ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, timeStart);
            stmt.setString(2, timeEnd);
            rs = stmt.executeQuery();
            if (rs.next()) {
                res = rs.getInt("total");
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    @Override
    public int getRecharge(String timeStart, String timeEnd) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        int res = 0;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = " SELECT COUNT(*) AS total  FROM vinplay.users  WHERE `create_time` >= ?    AND `create_time` <= ?    AND recharge_money > 0 ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, timeStart);
            stmt.setString(2, timeEnd);
            rs = stmt.executeQuery();
            if (rs.next()) {
                res = rs.getInt("total");
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    @Override
    public int getSecMobile(String timeStart, String timeEnd) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        int res = 0;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = " SELECT COUNT(*) AS total  FROM vinplay.users  WHERE `create_time` >= ?    AND `create_time` <= ?    AND (`status` & 16) ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, timeStart);
            stmt.setString(2, timeEnd);
            rs = stmt.executeQuery();
            if (rs.next()) {
                res = rs.getInt("total");
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    @Override
    public int getBoth(String timeStart, String timeEnd) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        int res = 0;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = " SELECT COUNT(*) AS total  FROM vinplay.users  WHERE `create_time` >= ?    AND `create_time` <= ?    AND recharge_money > 0    AND (`status` & 16) ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, timeStart);
            stmt.setString(2, timeEnd);
            rs = stmt.executeQuery();
            if (rs.next()) {
                res = rs.getInt("total");
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }
}

