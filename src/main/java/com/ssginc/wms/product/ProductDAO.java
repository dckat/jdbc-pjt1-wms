package com.ssginc.wms.product;

import com.ssginc.wms.hikari.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO {
    private final DataSource dataSource;

    public ProductDAO() {
        this.dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    public ArrayList<CustomerProductVO> listUserProduct() {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT product_id, product_name, " +
                     "c.category_id category_id, category_name, ord_price, product_amount\n" +
                     "FROM product p\n" +
                     "INNER JOIN product_category c\n" +
                     "ON p.category_id = c.category_id")) {
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<CustomerProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    CustomerProductVO vo = new CustomerProductVO();
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setCategoryId(rs.getInt("category_id"));
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
    public ArrayList<CustomerProductVO> searchProductByKeyword(String selected, String keyword) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT product_id, product_name, " +
                     "c.category_id category_id, category_name, ord_price, product_amount\n" +
                     "FROM product p\n" +
                     "INNER JOIN product_category c\n" +
                     "ON p.category_id = c.category_id\n" +
                     "WHERE " + selected + " like ?")) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<CustomerProductVO> list = new ArrayList<>();
                while (rs.next()) {
                    CustomerProductVO vo = new CustomerProductVO();
                    vo.setProductId(rs.getInt("product_id"));
                    vo.setProductName(rs.getString("product_name"));
                    vo.setCategoryId(rs.getInt("category_id"));
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

    //
    // 상품 목록 조회
    public List<ProductVO> getProducts(String columnName, String searchKeyword) {
        List<ProductVO> productList = new ArrayList<>();
        String query = "SELECT * FROM product";

        if (columnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
            query += " WHERE " + columnName + " = ?";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (columnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
                stmt.setString(1, searchKeyword);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProductVO product = new ProductVO();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setOrderPrice(rs.getInt("ord_price"));
                product.setSupplyPrice(rs.getInt("supply_price"));
                product.setProductAmount(rs.getInt("product_amount"));
                product.setCategoryId(rs.getInt("category_id"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    // 상품 추가
    public boolean insertProduct(ProductVO product) {
        String insertQuery = "INSERT INTO product (product_name, ord_price, supply_price, product_amount, category_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getOrderPrice());
            pstmt.setInt(3, product.getSupplyPrice());
            pstmt.setInt(4, product.getProductAmount());
            pstmt.setInt(5, product.getCategoryId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 상품 업데이트
    public boolean updateProduct(ProductVO product) {
        String updateQuery = "UPDATE product SET product_name = ?, ord_price = ?, supply_price = ?, product_amount = ?, category_id = ? WHERE product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getOrderPrice());
            pstmt.setInt(3, product.getSupplyPrice());
            pstmt.setInt(4, product.getProductAmount());
            pstmt.setInt(5, product.getCategoryId());
            pstmt.setInt(6, product.getProductId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 상품 삭제
    public boolean deleteProduct(int productId) {
        String deleteQuery = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ProductVO getProductById(int productId) {
        String query = "SELECT * FROM product WHERE product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ProductVO product = new ProductVO();
                    product.setProductId(rs.getInt("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setOrderPrice(rs.getInt("ord_price"));
                    product.setSupplyPrice(rs.getInt("supply_price"));
                    product.setProductAmount(rs.getInt("product_amount"));
                    product.setCategoryId(rs.getInt("category_id"));
                    return product;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, Integer> getAmountById() {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT product_id, product_amount " +
                     "FROM product")) {
            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Integer> proMap = new HashMap<>();
                while (rs.next()) {
                    proMap.put(rs.getInt("product_id"), rs.getInt("product_amount"));
                }
                return proMap;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
