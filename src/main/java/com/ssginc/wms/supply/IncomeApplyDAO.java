package com.ssginc.wms.supply;

import com.ssginc.wms.hikari.HikariCPDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncomeApplyDAO {

    // 관리자 입고 신청 내역 메서드(전체)
    public List<ProductIncomeApplyVO> listIncomeApply(String columnName, String searchKeyword) {
        List<ProductIncomeApplyVO> incomeApplies = new ArrayList<>();
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

        query += " ORDER BY ia.apply_time DESC";

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (columnName != null && !columnName.equals("전체") && searchKeyword != null && !searchKeyword.isEmpty()) {
                stmt.setString(1, searchKeyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProductIncomeApplyVO incomeApply = new ProductIncomeApplyVO();
                    incomeApply.setApplyId(rs.getInt("apply_id"));
                    incomeApply.setProductId(rs.getInt("product_id"));
                    incomeApply.setProductName(rs.getString("product_name"));
                    incomeApply.setCategoryName(rs.getString("category_name"));
                    incomeApply.setUserId(rs.getString("user_id"));
                    incomeApply.setApplyTime(rs.getTimestamp("apply_time").toLocalDateTime());

                    String status = rs.getString("apply_status");
                    if (status != null) {
                        incomeApply.setApplyStatus(ProductIncomeApplyVO.Process.valueOf(status.toLowerCase()));
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
    public List<ProductIncomeApplyVO> listPendingIncomeApplies() {
        List<ProductIncomeApplyVO> incomeApplies = new ArrayList<>();
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
        WHERE ia.user_id NOT LIKE '%\\_%' AND ia.apply_status = 'pending'
        ORDER BY ia.apply_time DESC
    """;

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProductIncomeApplyVO incomeApply = new ProductIncomeApplyVO();
                incomeApply.setApplyId(rs.getInt("apply_id"));
                incomeApply.setProductId(rs.getInt("product_id"));
                incomeApply.setProductName(rs.getString("product_name"));
                incomeApply.setCategoryName(rs.getString("category_name"));
                incomeApply.setUserId(rs.getString("user_id"));
                incomeApply.setApplyTime(rs.getTimestamp("apply_time").toLocalDateTime());
                incomeApply.setApplyStatus(ProductIncomeApplyVO.Process.valueOf(rs.getString("apply_status").toLowerCase()));
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
