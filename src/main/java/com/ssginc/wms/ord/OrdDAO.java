package com.ssginc.wms.ord;

import com.ssginc.wms.hikari.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

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
}
