package com.ssginc.wms.supply.DAO;

import com.ssginc.wms.hikari.HikariCPDataSource;
import com.ssginc.wms.supply.VO.SupplyProductVO;
import com.ssginc.wms.supply.VO.SupplyVO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplyDAO {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private final DataSource dataSource;
    public SupplyDAO() {
        this.dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // 데이터베이스 연결 메서드
    private void getConnection() throws SQLException {
        // 데이터베이스 연결 코드
        this.conn = dataSource.getConnection(); // HikariCP를 통해 커넥션 가져오기
    }

    // 데이터베이스 연결 해제 메서드
    private void closeResources() {
        // 리소스 해제 코드
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close(); // 커넥션도 닫기
        } catch (SQLException e) {
            System.err.println("리소스 해제 중 오류 발생: " + e.getMessage());
        }
    }

    // KKH-01 모든 재고 현황 조회
    public void stockList(){

    }

    // KKH-02 재고 현황 조회(조건)
    public void stockList(String condition){

    }

    // KKH-03 발주 등록
    public void insert(SupplyVO supplyVO){
        String sql = "INSERT INTO Supply (supply_amount, product_id, supply_time) VALUES (?, ?, NOW())";
        // 구현 필요: PreparedStatement를 사용하여 쿼리 실행
    }

    // KKH-04 모든 발주 내역 조회
    public List<SupplyProductVO> listSupplies() {
        List<SupplyProductVO> supplies = new ArrayList<>();
        try {
            getConnection();
//            String sql = "SELECT " +
//                    "s.supply_id," +
//                    "p.product_id," +
//                    "p.product_name," +
//                    "pc.category_name," +
//                    "p.supply_price," +
//                    "s.supply_amount," +
//                    "s.supply_time" +
//                    "FROM supply s" +
//                    "JOIN product p ON s.product_id = p.product_id" +
//                    "JOIN product_category pc ON p.category_id = pc.category_id" +
//                    "ORDER BY s.supply_id DESC";
            String sql = "SELECT " +
                    "s.supply_id, " +
                    "p.product_id, " +
                    "p.product_name, " +
                    "pc.category_name, " +
                    "p.supply_price, " +
                    "s.supply_amount, " +
                    "s.supply_time " +
                    "FROM supply s " + // FROM 앞에 공백 추가
                    "JOIN product p ON s.product_id = p.product_id " + // JOIN 앞에 공백 추가
                    "JOIN product_category pc ON p.category_id = pc.category_id " + // JOIN 앞에 공백 추가
                    "ORDER BY s.supply_id DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SupplyProductVO supply = new SupplyProductVO();
                supply.setSupply_id(rs.getInt("supply_id"));
                supply.setProduct_id(rs.getInt("product_id"));
                supply.setProduct_name(rs.getString("product_name"));
                supply.setCategory_name(rs.getString("category_name"));
                supply.setSupply_price(rs.getInt("supply_price"));
                supply.setSupply_amount(rs.getInt("supply_amount"));
                // Timestamp를 LocalDateTime으로 변환하여 설정
                Timestamp timestamp = rs.getTimestamp("supply_time");
                if (timestamp != null) {
                    supply.setSupply_time(timestamp.toLocalDateTime());
                } else {
                    supply.setSupply_time(null); // null 처리
                }
                supplies.add(supply);
            }
        } catch (SQLException e) {
            System.err.println("발주 내역 조회 중 오류 발생: " + e.getMessage());
        } finally {
            closeResources();
        }
        return supplies;
    }

    // KKH-05 발주 내역 조회(조건)


    // ------------------------------------
    // KKH-06 모든 입고 신청 내역 조회
    // KKH-07 입고 신청 내역 조회(조건)        --> IncomeApplyDAO
    // KKH-08 발주 등록(입고 신청) KKH-08
    // ------------------------------------


}