package com.ssginc.wms.supply;

import com.ssginc.wms.hikari.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplyDAO {
    private final DataSource dataSource;

    public SupplyDAO() {
        this.dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

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
            default -> null;  // "전체" 또는 비정상적인 값일 경우
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

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // 검색 조건이 있는 경우 파라미터 설정
            if (columnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
                stmt.setString(1, searchKeyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                ArrayList<SupplyProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    SupplyProductVO vo = new SupplyProductVO();
                    vo.setSupplyId(rs.getInt("supply_id"));
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setCategoryName(rs.getString("category_name"));
                    vo.setSupplyPrice(rs.getInt("supply_price"));
                    vo.setSupplyAmount(rs.getInt("supply_amount"));
                    vo.setSupplyTime(rs.getTimestamp("supply_time").toLocalDateTime());
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

        try (Connection conn = dataSource.getConnection();
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
    public void registerSupply(SupplyVO supplyVO) throws SQLException {
        String insertSupplySQL = "INSERT INTO Supply (supply_amount, product_id, supply_time) VALUES (?, ?, NOW())";
        String updateProductSQL = "UPDATE PRODUCT SET product_amount = product_amount + ? WHERE product_id = ?";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertSupplySQL);
                 PreparedStatement pstmt2 = conn.prepareStatement(updateProductSQL)) {

                // SupplyVO 객체를 사용하여 PreparedStatement에 값 설정
                pstmt1.setInt(1, supplyVO.getSupplyAmount());
                pstmt1.setInt(2, supplyVO.getProductId());
                pstmt1.executeUpdate();

                pstmt2.setInt(1, supplyVO.getSupplyAmount());
                pstmt2.setInt(2, supplyVO.getProductId());
                pstmt2.executeUpdate();

                conn.commit(); // 트랜잭션 커밋
            } catch (SQLException e) {
                conn.rollback(); // 오류 발생 시 롤백
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 조인된 데이터를 가져오는 메서드
    public List<SupplyProductVO> getSuppliesWithProductAndCategory() {
        String sql = """
            SELECT 
                s.supply_id AS supplyId,
                s.supply_amount AS supplyAmount,
                s.product_id AS productId,
                p.product_name AS productName,
                p.supply_price AS supplyPrice,
                pc.category_name AS categoryName,
                s.supply_time AS supplyTime,
                (s.supply_amount * p.supply_price) AS totalPrice
            FROM 
                supply s
            JOIN 
                product p ON s.product_id = p.product_id
            JOIN 
                product_category pc ON p.category_id = pc.category_id
            """;

        List<SupplyProductVO> supplyProducts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                SupplyProductVO vo = new SupplyProductVO();
                vo.setSupplyId(rs.getInt("supplyId"));
                vo.setSupplyAmount(rs.getInt("supplyAmount"));
                vo.setProductId(rs.getInt("productId"));
                vo.setProductName(rs.getString("productName"));
                vo.setSupplyPrice(rs.getInt("supplyPrice"));
                vo.setCategoryName(rs.getString("categoryName"));
                vo.setSupplyTime(rs.getTimestamp("supplyTime").toLocalDateTime());
                vo.setTotalPrice(rs.getInt("totalPrice"));

                supplyProducts.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return supplyProducts;
    }
}