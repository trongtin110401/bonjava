package com.vinplay.marketing.dao;

import com.vinplay.marketing.entity.Agency;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgencyDAO {

    private static final String SELECT_AGENCY_BY_CODE = "SELECT * FROM agency WHERE code = ?";
    private static final String INSERT_AGENCY = "INSERT INTO agency (code, name, status) VALUES (?, ?, ?)";
    private static final String SELECT_AGENCY_BY_ID = "SELECT * FROM agency WHERE id = ?";
    private static final String SELECT_ALL_AGENCIES = "SELECT * FROM agency";
    private static final String UPDATE_AGENCY = "UPDATE agency SET code = ?, name = ?, status = ? WHERE id = ?";
    private static final String DELETE_AGENCY = "DELETE FROM agency WHERE id = ?";
    private static final String CONNECTION_POOL_NAME = "mysqlpool_marketing";

    // Method to get connection from ConnectionPool
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection(CONNECTION_POOL_NAME);
    }

    // Create a new agency
    public void addAgency(String code, String name, int status) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_AGENCY)) {
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, status);
            preparedStatement.executeUpdate();
        }
    }

    // Retrieve an agency by ID
    public Agency getAgencyById(int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AGENCY_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Agency(
                            resultSet.getInt("id"),
                            resultSet.getString("code"),
                            resultSet.getString("name"),
                            resultSet.getTimestamp("created_at"),
                            resultSet.getTimestamp("updated_at"),
                            resultSet.getInt("status")
                    );
                }
            }
        }
        return null;
    }

    // Retrieve an agency by code
    public Agency getAgencyByCode(String code) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AGENCY_BY_CODE)) {
            preparedStatement.setString(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Agency(
                            resultSet.getInt("id"),
                            resultSet.getString("code"),
                            resultSet.getString("name"),
                            resultSet.getTimestamp("created_at"),
                            resultSet.getTimestamp("updated_at"),
                            resultSet.getInt("status")
                    );
                }
            }
        }
        return null;
    }

    // Retrieve all agencies
    public List<Agency> getAllAgencies() throws SQLException {
        List<Agency> agencies = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_AGENCIES);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                agencies.add(new Agency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("name"),
                        resultSet.getTimestamp("created_at"),
                        resultSet.getTimestamp("updated_at"),
                        resultSet.getInt("status")
                ));
            }
        }
        return agencies;
    }

    // Update an existing agency
    public void updateAgency(int id, String code, String name, int status) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_AGENCY)) {
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, status);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
        }
    }

    // Delete an agency
    public void deleteAgency(int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_AGENCY)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }



}
