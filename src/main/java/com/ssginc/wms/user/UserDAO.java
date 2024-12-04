package com.ssginc.wms.user;

import com.ssginc.wms.user.UserVO;

import com.ssginc.wms.hikari.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class UserDAO {
    private final DataSource dataSource;

    public UserDAO() {
        this.dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    public UserVO authenticateUser(String userId, String userPw) {
        String sql = "SELECT user_id, user_pw, user_grade FROM users WHERE user_id = ? AND user_pw = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, userPw);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserVO user = new UserVO(); // 기본 생성자 호출
                user.setUserId(rs.getString("user_id"));      // Setter로 값 설정
                user.setUserPw(rs.getString("user_pw"));
                user.setUserGrade(rs.getString("user_grade"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 인증 실패
    }

    public String findUserId(UserVO user) {
        String sql = "SELECT user_id FROM users WHERE user_name = ? AND user_ssn = ? AND user_email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getUserSsn());
            stmt.setString(3, user.getUserEmail());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("user_id"); // 일치하는 사용자 ID 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 일치하는 사용자 없음
    }

    public String findUserPassword(UserVO user) {
        String sql = "SELECT user_pw FROM users WHERE user_id = ? AND user_name = ? AND user_ssn = ? AND user_email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUserName());
            stmt.setString(3, user.getUserSsn());
            stmt.setString(4, user.getUserEmail());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("user_pw"); // 비밀번호 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 일치하는 데이터 없음
    }

    public boolean isIdDuplicate(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // 중복 ID 존재
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // 중복되지 않음
    }

    // 회원 정보 삽입
    public boolean insertUser(UserVO user) {
        String sql = "INSERT INTO users (user_id, user_pw, user_name, user_ssn, user_email, user_phone_number, user_address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUserPw());
            stmt.setString(3, user.getUserName());
            stmt.setString(4, user.getUserSsn());
            stmt.setString(5, user.getUserEmail());
            stmt.setString(6, user.getUserPhone());
            stmt.setString(7, user.getUserAddress());
            return stmt.executeUpdate() > 0; // 삽입 성공 여부 반환
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserInfo(UserVO user, String loggedInUserId) {
        String query = "UPDATE users SET user_id = ?, user_pw = ?, user_name = ?, user_email = ?, user_phone_number = ?, user_address = ? WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUserPw());
            pstmt.setString(3, user.getUserName());
            pstmt.setString(4, user.getUserEmail());
            pstmt.setString(5, user.getUserPhone());
            pstmt.setString(6, user.getUserAddress());
            pstmt.setString(7, loggedInUserId); // user_id 고정
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user info: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean markUserAsDeleted(String oldUserId, String newDeletedId) {
        String query = "UPDATE users SET user_id = ? WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newDeletedId); // 새 ID
            pstmt.setString(2, oldUserId);   // 기존 ID

            System.out.println("Updating user ID: " + oldUserId + " -> " + newDeletedId); // 디버그 로그
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update successful: " + rowsAffected + " rows affected.");
                return true; // 성공 여부 반환
            } else {
                System.out.println("Update failed: No matching user ID found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateUniqueDeletedId(String userId) {
        Random random = new Random();
        String newId;
        do {
            newId = userId + "_deleted" + random.nextInt(10000);
        } while (checkUserIdExists(newId)); // 중복 체크
        return newId;
    }

    public boolean checkUserIdExists(String userId) {
        String query = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}


