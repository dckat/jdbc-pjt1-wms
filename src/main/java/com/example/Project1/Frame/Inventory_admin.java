package com.example.Project1.Frame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Inventory_admin extends JFrame {
    private JTable table;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;

    public Inventory_admin() {
        setTitle("재고 현황");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);

        // 상단 패널 (분류 버튼 및 검색)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel categoryLabel = new JLabel("분류:");
        categoryComboBox = new JComboBox<>(new String[]{"과일", "야채", "생선"});
        JButton searchButton = new JButton("검색");
        topPanel.add(categoryLabel);
        topPanel.add(categoryComboBox);
        topPanel.add(searchButton);

        // 테이블 설정
        tableModel = new DefaultTableModel(new Object[]{"상품 코드", "상품 이름", "상품 가격", "카테고리", "재고 수량"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // 메인 레이아웃 구성
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // 데이터 로드
        loadData();

        // 검색 버튼 이벤트
        searchButton.addActionListener(e -> loadData());

        setVisible(true);
    }

    private void loadData() {
        String selectedCategory = categoryComboBox.getSelectedItem().toString();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // DB 연결
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "password");

            // SQL 쿼리
            String sql = "SELECT product_code, product_name, price, category, stock_quantity FROM product WHERE category=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, selectedCategory);
            rs = pstmt.executeQuery();

            // 테이블 데이터 초기화
            tableModel.setRowCount(0);

            // 결과 데이터 추가
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("product_code"),
                        rs.getString("product_name"),
                        rs.getInt("price"),
                        rs.getString("category"),
                        rs.getInt("stock_quantity")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터 로드 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        } finally {
            // 리소스 해제
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Inventory_admin::new);
    }
}

