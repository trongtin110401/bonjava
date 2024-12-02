package com.vinplay.api;

import com.vinplay.marketing.entity.MarketingUser;

import java.sql.*;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        // Thông tin kết nối MySQL
        String jdbcUrl = "jdbc:mysql://139.180.211.178:3306/vinplay_marketing?useUnicode=yes&characterEncoding=UTF-8"; // Thay 'your_database_name' bằng tên database của bạn
        String jdbcUsername = "root"; // Thay 'your_username' bằng tên đăng nhập
        String jdbcPassword = "0Db20H9CP6T4jRNBB70bVTrf5o7IG9"; // Thay 'your_password' bằng mật khẩu

        Connection connection = null;

        try {
            // Tạo kết nối tới MySQL
            connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
            if (connection != null) {
                System.out.println("Connected to the MySQL database successfully!");
            } else {
                System.out.println("Failed to connect to the MySQL database.");
            }

            MarketingUser marketingUser = new MarketingUser();
            marketingUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            marketingUser.setName("name");
            marketingUser.setUtmId(2);

            String sql = "INSERT INTO users (name, email, utm_id, created_at) VALUES (?, ?, ?, ?)";
            try (Connection conn = connection;
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, marketingUser.getName());
                stmt.setString(2, marketingUser.getEmail());
                stmt.setInt(3, marketingUser.getUtmId());
                stmt.setTimestamp(4, marketingUser.getCreatedAt());

                // Thực hiện thêm user
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                // Lấy ID vừa sinh từ cơ sở dữ liệu
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        marketingUser.setId(generatedKeys.getInt(1)); // Gán ID vào đối tượng User
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đảm bảo đóng kết nối
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Connection closed.");
                } catch (SQLException e) {
                    System.out.println("Error closing the connection: " + e.getMessage());
                }
            }
        }
    }

}
