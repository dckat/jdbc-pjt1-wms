package com.example.Project1.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    // MySQL 연결 설정
    private static final String URL = "jdbc:mysql://localhost:3306/project"; // 데이터베이스 이름 변경
    private static final String USER = "root"; // MySQL 사용자명
    private static final String PASSWORD = "1234"; // MySQL 비밀번호



    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 데이터를 INSERT하는 메서드
    public static boolean insertUser(String id, String password, String name, String ssn, String email, String phone, String address) {
        String query = "INSERT INTO user (id, pw, name, ssn, email, phone_number, address, join_date) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, ssn);
            pstmt.setString(5, email);
            pstmt.setString(6, phone);
            pstmt.setString(7, address);

            int rowsInserted = pstmt.executeUpdate();
            conn.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

