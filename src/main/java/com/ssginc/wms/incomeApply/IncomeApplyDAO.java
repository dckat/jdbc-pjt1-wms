package com.ssginc.wms.incomeApply;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncomeApplyDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/wms";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    // DB 연결 메서드
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 1. 입고 신청 내역 테이블 전시 (income_apply + product 테이블 조인 데이터 조회 메서드)
    public List<ProductIncomeApplyVO> getAllIncomeApply() {
        String query = """
                SELECT ia.apply_id, ia.product_id, ia.apply_time, ia.apply_status,
                       p.product_name, pc.category_name
                  FROM income_apply ia
                  JOIN product p ON ia.product_id = p.product_id
                  JOIN product_category pc ON p.category_id = pc.category_id
                """;    // WHERE user_id = [로그인한 사용자 id]; 작성하기 구매자용 화면이므로
        List<ProductIncomeApplyVO> incomeApplies = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // ResultSet 데이터를 VO로 변환
            while (rs.next()) {
                ProductIncomeApplyVO incomeApply = new ProductIncomeApplyVO();

                // ResultSet 데이터를 VO 객체에 설정
                incomeApply.setApplyId(rs.getInt("apply_id"));
                incomeApply.setProductId(rs.getInt("product_id"));
                incomeApply.setApplyTime(rs.getTimestamp("apply_time").toLocalDateTime());
                String status = rs.getString("apply_status");
                incomeApply.setApplyStatus(ProductIncomeApplyVO.Process.valueOf(status));
                incomeApply.setProductName(rs.getString("product_name"));
                incomeApply.setCategoryName(rs.getString("category_name")); // category_name 설정

                incomeApplies.add(incomeApply);
            }

        } catch (SQLException e) {
            System.err.println("DB 오류: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("apply_status 매핑 오류: " + e.getMessage());
        }

        return incomeApplies;
    }

    // 2. 입고 대기 상태에서 입고 취소 기능
    public boolean deletePendingIncomeApplies(List<Integer> applyIds) {
        String query = """
                DELETE ia
                  FROM income_apply ia
                 WHERE ia.apply_id = ? AND ia.apply_status = 'pending'
                """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int applyId : applyIds) {
                stmt.setInt(1, applyId);
                stmt.addBatch(); // 배치에 추가
            }

            int[] result = stmt.executeBatch(); // 일괄 실행
            return result.length == applyIds.size();

        } catch (SQLException e) {
            System.err.println("삭제 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    //3. 기간에 따라 품목 필터링 기능
    public List<ProductIncomeApplyVO> getIncomeApplyWithinPeriod(int days) {
        String query = """
            SELECT ia.apply_id, ia.product_id, ia.apply_time, ia.apply_status,
                   p.product_name, pc.category_name
              FROM income_apply ia
              JOIN product p ON ia.product_id = p.product_id
              JOIN product_category pc ON p.category_id = pc.category_id
             WHERE ia.apply_time >= NOW() - INTERVAL ? DAY
            """;

        List<ProductIncomeApplyVO> incomeApplies = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, days); // 필터링할 기간을 설정

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProductIncomeApplyVO incomeApply = new ProductIncomeApplyVO();
                    incomeApply.setApplyId(rs.getInt("apply_id"));
                    incomeApply.setProductId(rs.getInt("product_id"));
                    incomeApply.setApplyTime(rs.getTimestamp("apply_time").toLocalDateTime());
                    incomeApply.setApplyStatus(ProductIncomeApplyVO.Process.valueOf(rs.getString("apply_status")));
                    incomeApply.setProductName(rs.getString("product_name"));
                    incomeApply.setCategoryName(rs.getString("category_name"));
                    incomeApplies.add(incomeApply);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 오류: " + e.getMessage());
        }
        return incomeApplies;
    }

    //4. 드롭다운으로 데이터의 종류를 컬럼별로 나누어 검색 기능 구현
    public List<ProductIncomeApplyVO> searchIncomeApplies(String column, String searchText) {
        String query = """
                SELECT ia.apply_id, ia.product_id, ia.apply_time, ia.apply_status,
                       p.product_name, pc.category_name
                  FROM income_apply ia
                  JOIN product p ON ia.product_id = p.product_id
                  JOIN product_category pc ON p.category_id = pc.category_id
                 WHERE LOWER(%s) LIKE LOWER(?)
                """.formatted(column);  // 쿼리에서 column을 동적으로 설정

        List<ProductIncomeApplyVO> incomeApplies = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchText + "%"); // 검색어 설정

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProductIncomeApplyVO incomeApply = new ProductIncomeApplyVO();
                    incomeApply.setApplyId(rs.getInt("apply_id"));
                    incomeApply.setProductId(rs.getInt("product_id"));
                    incomeApply.setApplyTime(rs.getTimestamp("apply_time").toLocalDateTime());
                    incomeApply.setApplyStatus(ProductIncomeApplyVO.Process.valueOf(rs.getString("apply_status")));
                    incomeApply.setProductName(rs.getString("product_name"));
                    incomeApply.setCategoryName(rs.getString("category_name"));
                    incomeApplies.add(incomeApply);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB 오류: " + e.getMessage());
        }
        return incomeApplies;
    }
}
