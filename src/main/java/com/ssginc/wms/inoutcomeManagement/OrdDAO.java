package com.ssginc.wms.inoutcomeManagement;

import com.ssginc.wms.hikari.HikariCPDataSource;
import com.ssginc.wms.inoutcomeManagement.OutComeProductVO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdDAO {
    private final DataSource dataSource;

    // DB 연결 메서드
    private OrdDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // 출고 현황 조회 메서드
    public List<OutComeProductVO> getCompletedOrders() {
        String sql = """
            SELECT 
                o.ord_id AS ordId,
                o.ord_amount AS ordAmount,
                o.product_id AS productId,
                o.ord_status AS ordStatus,
                o.ord_complete_time AS ordCompleteTime,
                p.product_name AS productName,
                p.ord_price AS ordPrice,
                pc.category_name AS categoryName
            FROM 
                ord o
            JOIN 
                product p ON o.product_id = p.product_id
            JOIN 
                product_category pc ON p.category_id = pc.category_id
            WHERE 
                o.ord_status = 'completed'
            """;

        List<OutComeProductVO> completedOrders = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                OutComeProductVO vo = new OutComeProductVO();
                vo.setOrdId(rs.getInt("ordId"));
                vo.setOrdAmount(rs.getInt("ordAmount"));
                vo.setProductId(rs.getInt("productId"));


                // LocalDateTime으로 변환
                Timestamp timestamp = rs.getTimestamp("ordCompleteTime");
                if (timestamp != null) {
                    vo.setOrdCompleteTime(timestamp.toLocalDateTime());
                }

                vo.setProductName(rs.getString("productName"));
                vo.setOrdPrice(rs.getInt("ordPrice"));
                vo.setCategoryName(rs.getString("categoryName"));

                completedOrders.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completedOrders;
    }
}
