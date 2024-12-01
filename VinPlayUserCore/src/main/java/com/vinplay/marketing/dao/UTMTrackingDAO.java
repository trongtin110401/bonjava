package com.vinplay.marketing.dao;

import com.vinplay.marketing.entity.UTMTracking;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UTMTrackingDAO {
    // Tên Connection Pool cố định
    private static final String CONNECTION_POOL_NAME = "mysqlpool_marketing";

    // Lấy kết nối từ ConnectionPool
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection(CONNECTION_POOL_NAME);
    }

    // Thêm một UTMTracking mới
    public void addUTMTracking(UTMTracking utmTracking) throws SQLException {
        String sql = "INSERT INTO utm_tracking (utm_source, utm_medium, utm_campaign, utm_term, utm_content, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utmTracking.getUtmSource());
            stmt.setString(2, utmTracking.getUtmMedium());
            stmt.setString(3, utmTracking.getUtmCampaign());
            stmt.setString(4, utmTracking.getUtmTerm());
            stmt.setString(5, utmTracking.getUtmContent());
            stmt.setTimestamp(6, Timestamp.valueOf(utmTracking.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(utmTracking.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }

    // Lấy UTMTracking theo campaign
    public UTMTracking getUTMTrackingByCampaign(String campaign) throws SQLException {
        String sql = "SELECT * FROM utm_tracking WHERE utm_campaign = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, campaign);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UTMTracking utmTracking = new UTMTracking();
                    utmTracking.setId(rs.getInt("id"));
                    utmTracking.setUtmSource(rs.getString("utm_source"));
                    utmTracking.setUtmMedium(rs.getString("utm_medium"));
                    utmTracking.setUtmCampaign(rs.getString("utm_campaign"));
                    utmTracking.setUtmTerm(rs.getString("utm_term"));
                    utmTracking.setUtmContent(rs.getString("utm_content"));
                    utmTracking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    utmTracking.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return utmTracking;
                }
            }
        }
        return null; // Không tìm thấy UTMTracking
    }


    // Lấy thông tin UTMTracking theo ID
    public UTMTracking getUTMTrackingById(int id) throws SQLException {
        String sql = "SELECT * FROM utm_tracking WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UTMTracking utmTracking = new UTMTracking();
                    utmTracking.setId(rs.getInt("id"));
                    utmTracking.setUtmSource(rs.getString("utm_source"));
                    utmTracking.setUtmMedium(rs.getString("utm_medium"));
                    utmTracking.setUtmCampaign(rs.getString("utm_campaign"));
                    utmTracking.setUtmTerm(rs.getString("utm_term"));
                    utmTracking.setUtmContent(rs.getString("utm_content"));
                    utmTracking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    utmTracking.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return utmTracking;
                }
            }
        }
        return null; // Không tìm thấy UTMTracking
    }

    // Lấy danh sách tất cả UTMTracking
    public List<UTMTracking> getAllUTMTrackings() throws SQLException {
        String sql = "SELECT * FROM utm_tracking";
        List<UTMTracking> utmTrackings = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UTMTracking utmTracking = new UTMTracking();
                utmTracking.setId(rs.getInt("id"));
                utmTracking.setUtmSource(rs.getString("utm_source"));
                utmTracking.setUtmMedium(rs.getString("utm_medium"));
                utmTracking.setUtmCampaign(rs.getString("utm_campaign"));
                utmTracking.setUtmTerm(rs.getString("utm_term"));
                utmTracking.setUtmContent(rs.getString("utm_content"));
                utmTracking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                utmTracking.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                utmTrackings.add(utmTracking);
            }
        }
        return utmTrackings;
    }

    // Cập nhật thông tin UTMTracking
    public void updateUTMTracking(UTMTracking utmTracking) throws SQLException {
        String sql = "UPDATE utm_tracking SET utm_source = ?, utm_medium = ?, utm_campaign = ?, utm_term = ?, utm_content = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utmTracking.getUtmSource());
            stmt.setString(2, utmTracking.getUtmMedium());
            stmt.setString(3, utmTracking.getUtmCampaign());
            stmt.setString(4, utmTracking.getUtmTerm());
            stmt.setString(5, utmTracking.getUtmContent());
            stmt.setTimestamp(6, Timestamp.valueOf(utmTracking.getUpdatedAt()));
            stmt.setInt(7, utmTracking.getId());
            stmt.executeUpdate();
        }
    }

    // Xóa UTMTracking theo ID
    public void deleteUTMTracking(int id) throws SQLException {
        String sql = "DELETE FROM utm_tracking WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
