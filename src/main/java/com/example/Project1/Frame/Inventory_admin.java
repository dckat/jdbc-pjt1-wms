package com.example.Project1.Frame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Inventory_admin extends JFrame {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;

    public Inventory_admin() {
        setTitle("재고 관리 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 800);

        // 상단 패널 (분류, 검색 버튼)
        JPanel topPanel = new JPanel();
        categoryComboBox = new JComboBox<>(new String[]{"전체", "과일", "야채", "생선"});
        JButton searchButton = new JButton("검색");
        topPanel.add(new JLabel("분류:"));
        topPanel.add(categoryComboBox);
        topPanel.add(searchButton);

        // 중앙 JTable
        tableModel = new DefaultTableModel(new String[]{"상품코드", "분류코드", "분류이름", "상품이름", "상품가격", "재고수량"}, 0);
        productTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(productTable);

        // 좌측 버튼 패널
        JPanel leftPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton inventoryButton = new JButton("재고현황");
        JButton stockButton = new JButton("입출고 관리");
        JButton orderHistoryButton = new JButton("주문 내역 조회");
        leftPanel.add(inventoryButton);
        leftPanel.add(stockButton);
        leftPanel.add(orderHistoryButton);

        // 메인 프레임에 컴포넌트 추가
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // 버튼 및 검색 이벤트 설정
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProductData((String) categoryComboBox.getSelectedItem());
            }
        });

        inventoryButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "재고현황 버튼 클릭됨"));
        stockButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "입출고 관리 버튼 클릭됨"));
        orderHistoryButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "주문 내역 조회 버튼 클릭됨"));

        // 초기 데이터 로드
        loadProductData("전체");
        setVisible(true);
    }

    private void loadProductData(String category) {
        tableModel.setRowCount(0); // 기존 데이터 삭제
        String query = "SELECT * FROM product";

        // 카테고리가 전체가 아닐 경우 조건 추가
        if (!"전체".equals(category)) {
            query += " WHERE category = ?";
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/project", "root", "1234");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!"전체".equals(category)) {
                stmt.setString(1, category);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("product_code"),
                        rs.getString("category"),
                        rs.getString("product_name"),
                        rs.getInt("price"),
                        rs.getInt("stock_quantity")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
        }
    }
}


