package com.vinplay.marketing.dao;

import com.vinplay.marketing.entity.UserRetention;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRetentionDAO {
    // Tên Connection Pool cố định
    private static final String CONNECTION_POOL_NAME = "mysqlpool_marketing";

    // Lấy kết nối từ ConnectionPool
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection(CONNECTION_POOL_NAME);
    }

    // Thêm một UserRetention mới
    public void addUserRetention(UserRetention retention) throws SQLException {
        String sql = "INSERT INTO user_retention (user_id, first_access, d1_returned, d7_returned, d14_returned, d30_returned, visit_count, last_access, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, retention.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(retention.getFirstAccess()));
            stmt.setBoolean(3, retention.isD1Returned());
            stmt.setBoolean(4, retention.isD7Returned());
            stmt.setBoolean(5, retention.isD14Returned());
            stmt.setBoolean(6, retention.isD30Returned());
            stmt.setInt(7, retention.getVisitCount());
            stmt.setTimestamp(8, retention.getLastAccess() != null ? Timestamp.valueOf(retention.getLastAccess()) : null);
            stmt.setTimestamp(9, Timestamp.valueOf(retention.getCreatedAt()));
            stmt.setTimestamp(10, Timestamp.valueOf(retention.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }

    // Lấy UserRetention theo user_id
    public UserRetention getUserRetentionByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM user_retention WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserRetention retention = new UserRetention();
                    retention.setId(rs.getInt("id"));
                    retention.setUserId(rs.getInt("user_id"));
                    retention.setFirstAccess(rs.getTimestamp("first_access").toLocalDateTime());
                    retention.setD1Returned(rs.getBoolean("d1_returned"));
                    retention.setD7Returned(rs.getBoolean("d7_returned"));
                    retention.setD14Returned(rs.getBoolean("d14_returned"));
                    retention.setD30Returned(rs.getBoolean("d30_returned"));
                    retention.setVisitCount(rs.getInt("visit_count"));
                    retention.setLastAccess(rs.getTimestamp("last_access") != null ? rs.getTimestamp("last_access").toLocalDateTime() : null);
                    retention.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    retention.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return retention;
                }
            }
        }
        return null; // Không tìm thấy UserRetention
    }

    // Lấy danh sách tất cả UserRetention
    public List<UserRetention> getAllUserRetentions() throws SQLException {
        String sql = "SELECT * FROM user_retention";
        List<UserRetention> retentions = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UserRetention retention = new UserRetention();
                retention.setId(rs.getInt("id"));
                retention.setUserId(rs.getInt("user_id"));
                retention.setFirstAccess(rs.getTimestamp("first_access").toLocalDateTime());
                retention.setD1Returned(rs.getBoolean("d1_returned"));
                retention.setD7Returned(rs.getBoolean("d7_returned"));
                retention.setD14Returned(rs.getBoolean("d14_returned"));
                retention.setD30Returned(rs.getBoolean("d30_returned"));
                retention.setVisitCount(rs.getInt("visit_count"));
                retention.setLastAccess(rs.getTimestamp("last_access") != null ? rs.getTimestamp("last_access").toLocalDateTime() : null);
                retention.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                retention.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                retentions.add(retention);
            }
        }
        return retentions;
    }

    // Cập nhật UserRetention
    public void updateUserRetention(UserRetention retention) throws SQLException {
        String sql = "UPDATE user_retention SET first_access = ?, d1_returned = ?, d7_returned = ?, d14_returned = ?, d30_returned = ?, visit_count = ?, last_access = ?, updated_at = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(retention.getFirstAccess()));
            stmt.setBoolean(2, retention.isD1Returned());
            stmt.setBoolean(3, retention.isD7Returned());
            stmt.setBoolean(4, retention.isD14Returned());
            stmt.setBoolean(5, retention.isD30Returned());
            stmt.setInt(6, retention.getVisitCount());
            stmt.setTimestamp(7, retention.getLastAccess() != null ? Timestamp.valueOf(retention.getLastAccess()) : null);
            stmt.setTimestamp(8, Timestamp.valueOf(retention.getUpdatedAt()));
            stmt.setInt(9, retention.getUserId());
            stmt.executeUpdate();
        }
    }

    // Xóa UserRetention theo user_id
    public void deleteUserRetentionByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM user_retention WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
