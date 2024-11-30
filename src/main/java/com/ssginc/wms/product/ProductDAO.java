package com.ssginc.wms.product;

import com.ssginc.wms.hikari.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductDAO {
    private final DataSource dataSource;

    public ProductDAO() {
        this.dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    public ArrayList<UserProductVO> listUserProduct() {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT product_id, product_name, " +
                     "c.category_id category_id, category_name, ord_price, product_amount\n" +
                     "FROM product p\n" +
                     "INNER JOIN product_category c\n" +
                     "ON p.category_id = c.category_id")) {
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<UserProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    UserProductVO vo = new UserProductVO();
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setCategoryCode(rs.getInt("category_id"));
                    vo.setCategoryName(rs.getString("category_name"));
                    vo.setOrderPrice(rs.getInt("ord_price"));
                    vo.setProductAmount(rs.getInt("product_amount"));
                    list.add(vo);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 키워드에 따라 상품 검색
    // 검색어에 포함된 모든 물품 검색
    public ArrayList<UserProductVO> searchProductByKeyword(String selected, String keyword) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT product_id, product_name, " +
                     "c.category_id category_id, category_name, ord_price, product_amount\n" +
                     "FROM product p\n" +
                     "INNER JOIN product_category c\n" +
                     "ON p.category_id = c.category_id\n" +
                     "WHERE " + selected + " like ?")) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<UserProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    UserProductVO vo = new UserProductVO();
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setCategoryCode(rs.getInt("category_id"));
                    vo.setCategoryName(rs.getString("category_name"));
                    vo.setOrderPrice(rs.getInt("ord_price"));
                    vo.setProductAmount(rs.getInt("product_amount"));
                    list.add(vo);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
