package com.ssginc.wms.ord;

import com.ssginc.wms.hikari.HikariCPDataSource;
import com.ssginc.wms.inoutManage.OutgoingProductVO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdDAO {
    private final DataSource dataSource;

    public OrdDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    public void insertOrder(OrdVO vo) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO ord(product_id, user_id, " +
                     "ord_amount, ord_time)" +
                     "VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, vo.getProductId());
            ps.setString(2, vo.getUserId());
            ps.setInt(3, vo.getOrderAmount());
            ps.setTimestamp(4, Timestamp.valueOf(vo.getOrderTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<OrdProductVO> getOrderList() {
        try (Connection con = dataSource.getConnection();
             // 탈퇴한 회원의 주문내역은 나오지 않도록 처리
             PreparedStatement ps = con.prepareStatement("SELECT ord_id, p.product_id product_id, product_name, " +
                     "ord_price, ord_amount, " +
                     "(ord_price * ord_amount) total_price, " +
                     "ord_time, ord_status " +
                     "FROM ord o " +
                     "INNER JOIN product p " +
                     "ON o.product_id = p.product_id " +
                     "WHERE o.user_id NOT LIKE '%\\_%' AND product_status = 'present'")) {
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<OrdProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    OrdProductVO vo = new OrdProductVO();
                    vo.setOrderId(rs.getInt("ord_id"));
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setOrderPrice(rs.getInt("ord_price"));
                    vo.setOrderAmount(rs.getInt("ord_amount"));
                    vo.setTotalPrice(rs.getInt("total_price"));
                    vo.setOrderTime(rs.getTimestamp("ord_time").toLocalDateTime());
                    vo.setOrderStatus(rs.getString("ord_status"));
                    list.add(vo);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<OrdProductVO> getAdminOrdListByPeriod(String mode) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT ord_id, p.product_id product_id, product_name, " +
                     "p.category_id category_id, category_name, ord_price, ord_amount, " +
                     "(ord_price * ord_amount) total_price, " +
                     "ord_time, ord_status " +
                     "FROM product p " +
                     "INNER JOIN ord o " +
                     "ON p.product_id = o.product_id " +
                     "INNER JOIN product_category pc " +
                     "ON p.category_id = pc.category_id " +
                     "WHERE ord_time BETWEEN SUBDATE(NOW(), INTERVAL " +
                     switch(mode) {
                         case "1week" -> "1 WEEK) ";
                         case "1month" -> "1 MONTH) ";
                         case "3months" -> "3 MONTH) ";
                         default -> null;
                     } +
                     "AND NOW() AND user_id NOT LIKE '%\\_%' AND product_status = 'present' " +
                     "ORDER BY ord_time DESC")) {
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<OrdProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    OrdProductVO vo = new OrdProductVO();
                    vo.setOrderId(rs.getInt("ord_id"));
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setOrderPrice(rs.getInt("ord_price"));
                    vo.setOrderAmount(rs.getInt("ord_amount"));
                    vo.setTotalPrice(rs.getInt("total_price"));
                    vo.setOrderTime(rs.getTimestamp("ord_time").toLocalDateTime());
                    vo.setOrderStatus(rs.getString("ord_status"));
                    list.add(vo);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ArrayList<OrdProductVO> getOrdListByCustomerId(String userId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT ord_id, p.product_id product_id, product_name, " +
                     "p.category_id category_id, category_name, ord_price, ord_amount, " +
                     "(ord_price * ord_amount) total_price, " +
                     "ord_time, ord_status " +
                     "FROM product p " +
                     "INNER JOIN ord o " +
                     "ON p.product_id = o.product_id " +
                     "INNER JOIN product_category pc " +
                     "ON p.category_id = pc.category_id " +
                     "WHERE user_id = ? AND product_status = 'present' " +
                     "ORDER BY ord_time DESC")) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<OrdProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    OrdProductVO vo = new OrdProductVO();
                    vo.setOrderId(rs.getInt("ord_id"));
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setOrderPrice(rs.getInt("ord_price"));
                    vo.setOrderAmount(rs.getInt("ord_amount"));
                    vo.setTotalPrice(rs.getInt("total_price"));
                    vo.setOrderTime(rs.getTimestamp("ord_time").toLocalDateTime());
                    vo.setOrderStatus(rs.getString("ord_status"));
                    list.add(vo);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<OrdProductVO> getCustomerOrdListByUserIdAndPeriod(String userId, String mode) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT ord_id, p.product_id product_id, product_name, " +
                     "p.category_id category_id, category_name, ord_price, ord_amount, " +
                     "(ord_price * ord_amount) total_price, " +
                     "ord_time, ord_status " +
                     "FROM product p " +
                     "INNER JOIN ord o " +
                     "ON p.product_id = o.product_id " +
                     "INNER JOIN product_category pc " +
                     "ON p.category_id = pc.category_id " +
                     "WHERE ord_time BETWEEN SUBDATE(NOW(), INTERVAL " +
                     switch(mode) {
                         case "1week" -> "1 WEEK) ";
                         case "1month" -> "1 MONTH) ";
                         case "3months" -> "3 MONTH) ";
                         default -> null;
                     } +
                     "AND NOW() and user_id = ? AND product_status = 'present' " +
                     "ORDER BY ord_time DESC")) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<OrdProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    OrdProductVO vo = new OrdProductVO();
                    vo.setOrderId(rs.getInt("ord_id"));
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setOrderPrice(rs.getInt("ord_price"));
                    vo.setOrderAmount(rs.getInt("ord_amount"));
                    vo.setTotalPrice(rs.getInt("total_price"));
                    vo.setOrderTime(rs.getTimestamp("ord_time").toLocalDateTime());
                    vo.setOrderStatus(rs.getString("ord_status"));
                    list.add(vo);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteOrd(int[] ordId) {
        try {
            Connection con = dataSource.getConnection();
            try(PreparedStatement ps = con.prepareStatement("DELETE FROM ord WHERE ord_id = ?")) {
                con.setAutoCommit(false);
                for (int i = 0; i < ordId.length; i++) {
                    ps.setInt(1, ordId[i]);
                    ps.addBatch();
                }
                int[] row = ps.executeBatch();
                con.commit();
                return row.length;
            } catch (SQLException e) {
                try {
                    con.rollback();
                    e.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int[] updateOrdStatus(int[] ordIds, int[] ordAmounts) {
        try {
            Connection con = dataSource.getConnection();
            try(PreparedStatement ps = con.prepareStatement("UPDATE ord o INNER JOIN product p " +
                    "ON o.product_id = p.product_id " +
                    "SET ord_status = 'completed', product_amount = product_amount - ?, " +
                    "ord_complete_time = NOW() WHERE ord_id = ?")) {
                con.setAutoCommit(false);
                for (int i = 0; i < ordIds.length; i++) {
                    ps.setInt(1, ordAmounts[i]);
                    ps.setInt(2, ordIds[i]);
                    ps.addBatch();
                }
                int[] rows = ps.executeBatch();
                con.commit();
                return rows;
            } catch (SQLException e) {
                try {
                    con.rollback();
                    e.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // 출고 현황 조회 메서드
    public List<OutgoingProductVO> getCompletedOrd() {
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

        List<OutgoingProductVO> completedOrders = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                OutgoingProductVO vo = new OutgoingProductVO();
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
