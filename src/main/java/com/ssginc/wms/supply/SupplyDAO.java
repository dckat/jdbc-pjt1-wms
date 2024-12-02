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

    // 발주할 상품 데이터를 검색하는 메서드
    public List<CategoryProductVO> listSupplies(String columnName, String searchKeyword) {
        List<CategoryProductVO> products = new ArrayList<>();
        String query = """
            SELECT 
                p.category_id, 
                pc.category_name, 
                p.product_id, 
                p.product_name, 
                p.product_amount, 
                p.supply_price, 
                p.ord_price 
            FROM product p
            JOIN product_category pc ON p.category_id = pc.category_id
        """;

        if (columnName != null && !columnName.equals("전체") && searchKeyword != null && !searchKeyword.isEmpty()) {
            query += " WHERE " + columnName + " = ?";
        }

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (columnName != null && !columnName.equals("전체") && searchKeyword != null && !searchKeyword.isEmpty()) {
                stmt.setString(1, searchKeyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CategoryProductVO product = new CategoryProductVO();
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setCategoryName(rs.getString("category_name"));
                    product.setProductId(rs.getInt("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setProductAmount(rs.getInt("product_amount"));
                    product.setSupplyPrice(rs.getInt("supply_price"));
                    product.setOrdPrice(rs.getInt("ord_price"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // 발주 등록 메서드
    public void registerSupply(int productId, int supplyAmount) throws SQLException {
        String insertSupplySQL = "INSERT INTO Supply (supply_amount, product_id, supply_time) VALUES (?, ?, NOW())";
        String updateProductSQL = "UPDATE PRODUCT SET product_amount = product_amount + ? WHERE product_id = ?";

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertSupplySQL);
                 PreparedStatement pstmt2 = conn.prepareStatement(updateProductSQL)) {

                pstmt1.setInt(1, supplyAmount);
                pstmt1.setInt(2, productId);
                pstmt1.executeUpdate();

                pstmt2.setInt(1, supplyAmount);
                pstmt2.setInt(2, productId);
                pstmt2.executeUpdate();

                conn.commit(); // 트랜잭션 커밋
            } catch (SQLException e) {
                conn.rollback(); // 오류 발생 시 롤백
                throw e;
            }
        }
    }
}