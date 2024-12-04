package com.ssginc.wms.incomeApply;

import com.ssginc.wms.hikari.HikariCPDataSource;
import com.ssginc.wms.supply.SupplyVO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IncomeApplyDAO {
    private final DataSource dataSource;

    // DB 연결 메서드
    public IncomeApplyDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
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

        try (Connection conn = dataSource.getConnection();
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

        try (Connection conn = dataSource.getConnection();
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
             WHERE ia.apply_time >= NOW() - INTERVAL ? DAY AND product_status = 'present'
            """;

        List<ProductIncomeApplyVO> incomeApplies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
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
                 WHERE LOWER(%s) LIKE LOWER(?) AND product_status = 'present'
                """.formatted(column);  // 쿼리에서 column을 동적으로 설정

        List<ProductIncomeApplyVO> incomeApplies = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
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

    // 입고 신청 데이터 삽입 메서드
    public void insertIncomeApply(IncomeApplyVO incomeApplyVO) {
        String sql = "INSERT INTO income_apply (product_id, user_id, apply_time) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incomeApplyVO.getProductId());
            pstmt.setString(2, incomeApplyVO.getUserId());
            pstmt.setTimestamp(3, Timestamp.valueOf(incomeApplyVO.getApplyTime()));

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("입고 신청 데이터가 성공적으로 삽입되었습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<com.ssginc.wms.supply.ProductIncomeApplyVO> listIncomeApply(String columnName, String searchKeyword) {
        List<com.ssginc.wms.supply.ProductIncomeApplyVO> incomeApplies = new ArrayList<>();
        String query = """
        SELECT 
            ia.apply_id,
            p.product_id,
            p.product_name,
            pc.category_name,
            ia.user_id,
            ia.apply_time,
            ia.apply_status
        FROM income_apply ia
        JOIN product p ON ia.product_id = p.product_id
        JOIN product_category pc ON p.category_id = pc.category_id
    """;

        // 기본 조건: user_id에 _가 포함되지 않은 행만 선택
        String baseCondition = " WHERE ia.user_id NOT LIKE '%\\_%'";

        // 검색 조건 추가
        if (columnName != null && !columnName.equals("전체") && searchKeyword != null && !searchKeyword.isEmpty()) {
            query += baseCondition + " AND " + columnName + " = ?";
        } else {
            query += baseCondition;
        }

        query += " AND product_status = 'present' ORDER BY ia.apply_time DESC";

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (columnName != null && !columnName.equals("전체") && searchKeyword != null && !searchKeyword.isEmpty()) {
                stmt.setString(1, searchKeyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    com.ssginc.wms.supply.ProductIncomeApplyVO incomeApply = new com.ssginc.wms.supply.ProductIncomeApplyVO();
                    incomeApply.setApplyId(rs.getInt("apply_id"));
                    incomeApply.setProductId(rs.getInt("product_id"));
                    incomeApply.setProductName(rs.getString("product_name"));
                    incomeApply.setCategoryName(rs.getString("category_name"));
                    incomeApply.setUserId(rs.getString("user_id"));
                    incomeApply.setApplyTime(rs.getTimestamp("apply_time").toLocalDateTime());

                    String status = rs.getString("apply_status");
                    if (status != null) {
                        incomeApply.setApplyStatus(com.ssginc.wms.supply.ProductIncomeApplyVO.Process.valueOf(status.toLowerCase()));
                    }

                    incomeApplies.add(incomeApply);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomeApplies;
    }

    // 관리자 입고 신청 내역 메서드(미승인)
    public List<com.ssginc.wms.supply.ProductIncomeApplyVO> listPendingIncomeApplies() {
        List<com.ssginc.wms.supply.ProductIncomeApplyVO> incomeApplies = new ArrayList<>();
        String query = """
        SELECT
            ia.apply_id,
            p.product_id,
            p.product_name,
            pc.category_name,
            ia.user_id,
            ia.apply_time,
            ia.apply_status
        FROM income_apply ia
        JOIN product p ON ia.product_id = p.product_id
        JOIN product_category pc ON p.category_id = pc.category_id
        WHERE ia.user_id NOT LIKE '%\\_%' AND ia.apply_status = 'pending' AND p.product_status = 'present'
        ORDER BY ia.apply_time DESC
    """;

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                com.ssginc.wms.supply.ProductIncomeApplyVO incomeApply = new com.ssginc.wms.supply.ProductIncomeApplyVO();
                incomeApply.setApplyId(rs.getInt("apply_id"));
                incomeApply.setProductId(rs.getInt("product_id"));
                incomeApply.setProductName(rs.getString("product_name"));
                incomeApply.setCategoryName(rs.getString("category_name"));
                incomeApply.setUserId(rs.getString("user_id"));
                incomeApply.setApplyTime(rs.getTimestamp("apply_time").toLocalDateTime());
                incomeApply.setApplyStatus(com.ssginc.wms.supply.ProductIncomeApplyVO.Process.valueOf(rs.getString("apply_status").toLowerCase()));
                incomeApplies.add(incomeApply);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomeApplies;
    }

    // 입고 신청에 대한 발주 등록 및 상태 업데이트 메서드
    public void registerApply(SupplyVO supplyVO) throws SQLException {
        String insertSupplySQL = "INSERT INTO Supply (supply_amount, product_id, supply_time) VALUES (?, ?, NOW())";
        String updateProductSQL = "UPDATE PRODUCT SET product_amount = product_amount + ? WHERE product_id = ?";
        String updateIncomeApplySQL = "UPDATE income_apply SET apply_status = 'completed' WHERE product_id = ?";

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작

            try (PreparedStatement pstmt1 = conn.prepareStatement(insertSupplySQL);
                 PreparedStatement pstmt2 = conn.prepareStatement(updateProductSQL);
                 PreparedStatement pstmt3 = conn.prepareStatement(updateIncomeApplySQL)) {

                // SupplyVO 객체를 사용하여 PreparedStatement에 값 설정
                pstmt1.setInt(1, supplyVO.getSupplyAmount());
                pstmt1.setInt(2, supplyVO.getProductId());
                pstmt1.executeUpdate();

                // 상품 재고량 업데이트
                pstmt2.setInt(1, supplyVO.getSupplyAmount());
                pstmt2.setInt(2, supplyVO.getProductId());
                pstmt2.executeUpdate();

                // 입고 신청 상태 업데이트
                pstmt3.setInt(1, supplyVO.getProductId());
                pstmt3.executeUpdate();

                conn.commit(); // 트랜잭션 커밋
            } catch (SQLException e) {
                conn.rollback(); // 오류 발생 시 롤백
                throw e;
            }
        }
    }
}
