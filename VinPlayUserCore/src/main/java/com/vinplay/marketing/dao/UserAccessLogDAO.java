package com.vinplay.marketing.dao;

import com.vinplay.marketing.entity.UserAccessLog;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccessLogDAO {
    // Tên Connection Pool cố định
    private static final String CONNECTION_POOL_NAME = "mysqlpool_marketing";

    // Lấy kết nối từ ConnectionPool
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection(CONNECTION_POOL_NAME);
    }

    // Thêm một UserAccessLog mới
    public void addUserAccessLog(UserAccessLog accessLog) throws SQLException {
        String sql = "INSERT INTO user_access_logs (user_id, access_time, utm_id, device, browser, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accessLog.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(accessLog.getAccessTime()));
            stmt.setInt(3, accessLog.getUtmId());
            stmt.setString(4, accessLog.getDevice());
            stmt.setString(5, accessLog.getBrowser());
            stmt.setTimestamp(6, accessLog.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    // Lấy danh sách UserAccessLog theo user_id
    public List<UserAccessLog> getLogsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM user_access_logs WHERE user_id = ?";
        List<UserAccessLog> logs = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserAccessLog log = new UserAccessLog();
                    log.setId(rs.getLong("id"));
                    log.setUserId(rs.getLong("user_id"));
                    log.setAccessTime(rs.getTimestamp("access_time").toLocalDateTime());
                    log.setUtmId(rs.getInt("utm_id"));
                    log.setDevice(rs.getString("device"));
                    log.setBrowser(rs.getString("browser"));
                    log.setCreatedAt(rs.getTimestamp("created_at"));
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    // Lấy danh sách tất cả UserAccessLog
    public List<UserAccessLog> getAllLogs() throws SQLException {
        String sql = "SELECT * FROM user_access_logs";
        List<UserAccessLog> logs = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UserAccessLog log = new UserAccessLog();
                log.setId(rs.getLong("id"));
                log.setUserId(rs.getLong("user_id"));
                log.setAccessTime(rs.getTimestamp("access_time").toLocalDateTime());
                log.setUtmId(rs.getInt("utm_id"));
                log.setDevice(rs.getString("device"));
                log.setBrowser(rs.getString("browser"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                logs.add(log);
            }
        }
        return logs;
    }

    // Xóa tất cả UserAccessLog theo user_id
    public void deleteLogsByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM user_access_logs WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}

