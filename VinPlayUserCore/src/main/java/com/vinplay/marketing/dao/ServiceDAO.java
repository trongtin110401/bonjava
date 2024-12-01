package com.vinplay.marketing.dao;

import com.vinplay.marketing.entity.Service;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {
    // Tên Connection Pool cố định
    private static final String CONNECTION_POOL_NAME = "mysqlpool_marketing";

    // Lấy kết nối từ ConnectionPool
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection(CONNECTION_POOL_NAME);
    }

    // Thêm một Service mới
    public void addService(Service service) throws SQLException {
        String sql = "INSERT INTO services (service_name, service_desc, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, service.getServiceName());
            stmt.setString(2, service.getServiceDesc());
            stmt.setTimestamp(3, Timestamp.valueOf(service.getCreatedAt()));
            stmt.setTimestamp(4, Timestamp.valueOf(service.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }

    // Lấy thông tin Service theo ID
    public Service getServiceById(int id) throws SQLException {
        String sql = "SELECT * FROM services WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setServiceName(rs.getString("service_name"));
                    service.setServiceDesc(rs.getString("service_desc"));
                    service.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    service.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return service;
                }
            }
        }
        return null; // Không tìm thấy Service
    }

    // Lấy danh sách tất cả Services
    public List<Service> getAllServices() throws SQLException {
        String sql = "SELECT * FROM services";
        List<Service> services = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("id"));
                service.setServiceName(rs.getString("service_name"));
                service.setServiceDesc(rs.getString("service_desc"));
                service.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                service.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                services.add(service);
            }
        }
        return services;
    }

    // Cập nhật Service
    public void updateService(Service service) throws SQLException {
        String sql = "UPDATE services SET service_name = ?, service_desc = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, service.getServiceName());
            stmt.setString(2, service.getServiceDesc());
            stmt.setTimestamp(3, Timestamp.valueOf(service.getUpdatedAt()));
            stmt.setInt(4, service.getId());
            stmt.executeUpdate();
        }
    }

    // Xóa Service theo ID
    public void deleteService(int id) throws SQLException {
        String sql = "DELETE FROM services WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

