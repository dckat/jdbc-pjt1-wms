package com.ssginc.wms.ord;

import com.ssginc.wms.hikari.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

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

    public ArrayList<OrdProductVO> listByUserId(String userId) {
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
                     "WHERE user_id = ? " +
                     "ORDER BY ord_time DESC")) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<OrdProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    OrdProductVO vo = new OrdProductVO();
                    vo.setOrderId(rs.getInt("ord_id"));
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setCategoryId(rs.getInt("category_id"));
                    vo.setCategoryName(rs.getString("category_name"));
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

    public ArrayList<OrdProductVO> listByPeriod(String userId, String mode) {
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
                     "AND NOW() and user_id = ? " +
                     "ORDER BY ord_time DESC")) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<OrdProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    OrdProductVO vo = new OrdProductVO();
                    vo.setOrderId(rs.getInt("ord_id"));
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setCategoryId(rs.getInt("category_id"));
                    vo.setCategoryName(rs.getString("category_name"));
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

    public int deleteOrder(int[] ordId) {
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
}
