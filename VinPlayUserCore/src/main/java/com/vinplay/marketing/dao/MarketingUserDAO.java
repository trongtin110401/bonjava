package com.vinplay.marketing.dao;

import com.vinplay.marketing.entity.MarketingUser;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarketingUserDAO {

    // Tên Connection Pool cố định
    private static final String CONNECTION_POOL_NAME = "mysqlpool_marketing";

    // Lấy kết nối từ ConnectionPool
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection(CONNECTION_POOL_NAME);
    }

    // Thêm một user mới
    // Thêm một user mới và trả về đối tượng User với ID đã sinh ra
    public MarketingUser addUser(MarketingUser marketingUser) throws SQLException {
        String sql = "INSERT INTO users (name, email, utm_id, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, marketingUser.getName());
            stmt.setString(2, marketingUser.getEmail());
            stmt.setInt(3, marketingUser.getUtmId());
            stmt.setTimestamp(4, Timestamp.valueOf(marketingUser.getCreatedAt()));

            // Thực hiện thêm user
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            // Lấy ID vừa sinh từ cơ sở dữ liệu
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    marketingUser.setId(generatedKeys.getInt(1)); // Gán ID vào đối tượng User
                    return marketingUser;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }


    // Lấy thông tin user theo ID
    // Lấy thông tin user theo ID
    public MarketingUser getUserById(long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MarketingUser marketingUser = new MarketingUser();
                    marketingUser.setId(rs.getLong("id"));
                    marketingUser.setName(rs.getString("name"));
                    marketingUser.setEmail(rs.getString("email"));
                    marketingUser.setUtmId(rs.getInt("utm_id")); // Lấy giá trị utm_id
                    marketingUser.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return marketingUser;
                }
            }
        }
        return null; // Không tìm thấy user
    }

    // Lấy thông tin User theo tên
    public MarketingUser getUserByName(String name) throws SQLException {
        String sql = "SELECT * FROM users WHERE name = ?";
        List<MarketingUser> marketingUsers = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                MarketingUser marketingUser = null;
                while (rs.next()) {
                    marketingUser = new MarketingUser();
                    marketingUser.setId(rs.getLong("id"));
                    marketingUser.setName(rs.getString("name"));
                    marketingUser.setEmail(rs.getString("email"));
                    marketingUser.setUtmId(rs.getInt("utm_id")); // Nếu có UTM ID
                    marketingUser.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                return marketingUser;
            }
        }
    }


    // Lấy danh sách tất cả user
    public List<MarketingUser> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        List<MarketingUser> marketingUsers = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MarketingUser marketingUser = new MarketingUser();
                marketingUser.setId(rs.getInt("id"));
                marketingUser.setName(rs.getString("name"));
                marketingUser.setEmail(rs.getString("email"));
                marketingUser.setUtmId(rs.getInt("utm_id"));
                marketingUser.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                marketingUsers.add(marketingUser);
            }
        }
        return marketingUsers;
    }

    // Cập nhật thông tin user
    public void updateUser(MarketingUser marketingUser) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, utm_id = ?, created_at = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, marketingUser.getName());
            stmt.setString(2, marketingUser.getEmail());
            stmt.setInt(3, marketingUser.getUtmId());
            stmt.setTimestamp(4, Timestamp.valueOf(marketingUser.getCreatedAt()));
            stmt.setLong(5, marketingUser.getId());
            stmt.executeUpdate();
        }
    }

    // Xóa user theo ID
    public void deleteUser(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}


