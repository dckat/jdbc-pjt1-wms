package com.ssginc.wms.supply;

import com.ssginc.wms.hikari.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplyDAO {


    // 발주 내역 메서드
    public ArrayList<SupplyProductVO> listSupply(String selectedColumn, String searchKeyword) {
        // 콤보박스에서 선택한 값과 데이터베이스 컬럼명을 매핑
        String columnName = switch (selectedColumn) {
            case "발주 ID" -> "s.supply_id";
            case "상품 ID" -> "p.product_id";
            case "상품 이름" -> "p.product_name";
            case "카테고리" -> "pc.category_name";
            case "공급 가격" -> "p.supply_price";
            case "발주 수량" -> "s.supply_amount";
            case "발주 시간" -> "s.supply_time";
            default -> null; // "전체" 또는 비정상적인 값일 경우
        };

        String query = "SELECT " +
                "s.supply_id, " +
                "p.product_id, " +
                "p.product_name, " +
                "pc.category_name, " +
                "p.supply_price, " +
                "s.supply_amount, " +
                "s.supply_time " +
                "FROM supply s " +
                "JOIN product p ON s.product_id = p.product_id " +
                "JOIN product_category pc ON p.category_id = pc.category_id ";

        // 검색 조건 추가
        if (columnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
            query += "WHERE " + columnName + " = ? ";
        }

        query += "ORDER BY s.supply_id DESC";

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // 검색 조건이 있는 경우 파라미터 설정
            if (columnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
                stmt.setString(1, searchKeyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                ArrayList<SupplyProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    SupplyProductVO vo = new SupplyProductVO();
                    vo.setSupply_id(rs.getInt("supply_id"));
                    vo.setProduct_id(rs.getInt("product_id"));
                    vo.setProduct_name(rs.getString("product_name"));
                    vo.setCategory_name(rs.getString("category_name"));
                    vo.setSupply_price(rs.getInt("supply_price"));
                    vo.setSupply_amount(rs.getInt("supply_amount"));
                    vo.setSupply_time(rs.getTimestamp("supply_time").toLocalDateTime());
                    list.add(vo);
                }
                return list;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}