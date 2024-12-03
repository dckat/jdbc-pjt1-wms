package com.ssginc.wms.inoutcomeManagement;

import com.ssginc.wms.hikari.HikariCPDataSource;
import com.ssginc.wms.supply.SupplyProductVO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplyDAO {
    private final DataSource dataSource;

    public SupplyDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
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
