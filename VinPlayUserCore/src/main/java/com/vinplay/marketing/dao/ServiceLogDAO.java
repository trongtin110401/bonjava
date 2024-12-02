package com.vinplay.marketing.dao;

import com.vinplay.marketing.entity.ServiceLog;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceLogDAO {
    // Tên Connection Pool cố định
    private static final String CONNECTION_POOL_NAME = "mysqlpool_marketing";

    // Lấy kết nối từ ConnectionPool
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection(CONNECTION_POOL_NAME);
    }

    // Thêm một ServiceLog mới
    public void addServiceLog(ServiceLog log) throws SQLException {
        String sql = "INSERT INTO service_logs (user_service_id, action, action_time, details, created_at, action_value) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getUserServiceId());
            stmt.setString(2, log.getAction());
            stmt.setTimestamp(3, Timestamp.valueOf(log.getActionTime()));
            stmt.setString(4, log.getDetails());
            stmt.setTimestamp(5, Timestamp.valueOf(log.getCreatedAt()));
            stmt.setLong(6, log.getActionValue());
            stmt.executeUpdate();
        }
    }

    // Lấy danh sách ServiceLog theo user_service_id
    public List<ServiceLog> getLogsByUserServiceId(int userServiceId) throws SQLException {
        String sql = "SELECT * FROM service_logs WHERE user_service_id = ?";
        List<ServiceLog> logs = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userServiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ServiceLog log = new ServiceLog();
                    log.setId(rs.getInt("id"));
                    log.setUserServiceId(rs.getInt("user_service_id"));
                    log.setAction(rs.getString("action"));
                    log.setActionTime(rs.getTimestamp("action_time").toLocalDateTime());
                    log.setDetails(rs.getString("details"));
                    log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    log.setActionValue(rs.getLong("action_value"));
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    // Lấy tất cả ServiceLogs
    public List<ServiceLog> getAllLogs() throws SQLException {
        String sql = "SELECT * FROM service_logs";
        List<ServiceLog> logs = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ServiceLog log = new ServiceLog();
                log.setId(rs.getInt("id"));
                log.setUserServiceId(rs.getInt("user_service_id"));
                log.setAction(rs.getString("action"));
                log.setActionTime(rs.getTimestamp("action_time").toLocalDateTime());
                log.setDetails(rs.getString("details"));
                log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                log.setActionValue(rs.getLong("action_value"));
                logs.add(log);
            }
        }
        return logs;
    }

    // Xóa tất cả ServiceLog theo user_service_id
    public void deleteLogsByUserServiceId(int userServiceId) throws SQLException {
        String sql = "DELETE FROM service_logs WHERE user_service_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userServiceId);
            stmt.executeUpdate();
        }
    }

    // Xóa ServiceLog theo id
    public void deleteLogById(int id) throws SQLException {
        String sql = "DELETE FROM service_logs WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

