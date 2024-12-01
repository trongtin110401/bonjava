package com.vinplay.marketing.dao;

import com.vinplay.marketing.entity.UserService;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServiceDAO {
    // Tên Connection Pool cố định
    private static final String CONNECTION_POOL_NAME = "mysqlpool_marketing";

    // Lấy kết nối từ ConnectionPool
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection(CONNECTION_POOL_NAME);
    }

    // Thêm một UserService mới
// Thêm một UserService mới và trả về ID của bản ghi mới
    public int addUserService(UserService userService) throws SQLException {
        String sql = "INSERT INTO user_services (user_id, service_id, utm_id, status, start_time, end_time, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userService.getUserId());
            stmt.setInt(2, userService.getServiceId());
            stmt.setInt(3, userService.getUtmId());
            stmt.setString(4, userService.getStatus());
            stmt.setTimestamp(5, Timestamp.valueOf(userService.getStartTime()));
            stmt.setTimestamp(6, userService.getEndTime() != null ? Timestamp.valueOf(userService.getEndTime()) : null);
            stmt.setTimestamp(7, Timestamp.valueOf(userService.getCreatedAt()));
            stmt.setTimestamp(8, Timestamp.valueOf(userService.getUpdatedAt()));

            // Thực hiện chèn dữ liệu
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating UserService failed, no rows affected.");
            }

            // Lấy ID của bản ghi vừa chèn
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về ID
                } else {
                    throw new SQLException("Creating UserService failed, no ID obtained.");
                }
            }
        }
    }


    // Lấy UserService theo ID
    public UserService getUserServiceById(int id) throws SQLException {
        String sql = "SELECT * FROM user_services WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserService userService = new UserService();
                    userService.setId(rs.getInt("id"));
                    userService.setUserId(rs.getInt("user_id"));
                    userService.setServiceId(rs.getInt("service_id"));
                    userService.setUtmId(rs.getInt("utm_id"));
                    userService.setStatus(rs.getString("status"));
                    userService.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    userService.setEndTime(rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null);
                    userService.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    userService.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return userService;
                }
            }
        }
        return null; // Không tìm thấy UserService
    }

    // Lấy danh sách UserService theo user_id
    public List<UserService> getUserServicesByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM user_services WHERE user_id = ?";
        List<UserService> userServices = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserService userService = new UserService();
                    userService.setId(rs.getInt("id"));
                    userService.setUserId(rs.getInt("user_id"));
                    userService.setServiceId(rs.getInt("service_id"));
                    userService.setUtmId(rs.getInt("utm_id"));
                    userService.setStatus(rs.getString("status"));
                    userService.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    userService.setEndTime(rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null);
                    userService.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    userService.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    userServices.add(userService);
                }
            }
        }
        return userServices;
    }

    // Cập nhật UserService
    public void updateUserService(UserService userService) throws SQLException {
        String sql = "UPDATE user_services SET service_id = ?, utm_id = ?, status = ?, start_time = ?, end_time = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userService.getServiceId());
            stmt.setInt(2, userService.getUtmId());
            stmt.setString(3, userService.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(userService.getStartTime()));
            stmt.setTimestamp(5, userService.getEndTime() != null ? Timestamp.valueOf(userService.getEndTime()) : null);
            stmt.setTimestamp(6, Timestamp.valueOf(userService.getUpdatedAt()));
            stmt.setInt(7, userService.getId());
            stmt.executeUpdate();
        }
    }

    // Xóa UserService theo ID
    public void deleteUserServiceById(int id) throws SQLException {
        String sql = "DELETE FROM user_services WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

