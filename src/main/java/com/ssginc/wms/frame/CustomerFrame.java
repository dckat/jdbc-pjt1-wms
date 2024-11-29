package com.ssginc.wms.frame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerFrame extends JFrame {
    private JComboBox<String> categoryComboBox;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private JTable productTable;
    private DefaultTableModel tableModel;
    Color color = new Color(0xF6C5C5);


    public CustomerFrame() {
        // JFrame 설정
        setTitle("재고 관리 시스템_Customer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLayout(new BorderLayout()); // 기본 레이아웃 설정

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(1200, 30));
        JButton dropdownButton = new JButton("≡");
        dropdownButton.setSize(30, 30);

        // JPopupMenu 생성
        JPopupMenu menu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("로그아웃");
        JMenuItem editInfoItem = new JMenuItem("회원정보수정");
        JMenuItem deleteAccountItem = new JMenuItem("회원탈퇴");
        menu.add(logoutItem);
        menu.add(editInfoItem);
        menu.add(deleteAccountItem);
        dropdownButton.addActionListener(e -> menu.show(dropdownButton, 0, dropdownButton.getHeight()));

        // 각 메뉴 항목에 동작 추가
        editInfoItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "회원정보수정 선택됨"));
        deleteAccountItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "회원탈퇴 선택됨"));

        JLabel welcomeLabel = new JLabel("< " + "user1" + " > 님 환영합니다.     ", SwingConstants.RIGHT);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.setBackground(Color.GRAY);
        topPanel.add(dropdownButton, BorderLayout.EAST);
        dropdownButton.setBackground(Color.LIGHT_GRAY);
        add(topPanel, BorderLayout.NORTH);

        // Left Panel
        JPanel leftPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        JLabel llabel = new JLabel("");
        JButton invenUIButton = new JButton("재 고  현 황");
        JButton ordHistoryButton = new JButton("주 문  내 역");
        JButton incomeHistoryButton = new JButton("입고신청 내역");
        leftPanel.add(llabel);
        leftPanel.add(invenUIButton);
        leftPanel.add(ordHistoryButton);
        leftPanel.add(incomeHistoryButton);
        leftPanel.setPreferredSize(new Dimension(150, 600));
        add(leftPanel, BorderLayout.WEST);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        categoryComboBox = new JComboBox<>(new String[]{"전체", "상품 코드", "상품 이름", "주문 가격", "공급 가격", "재고 수량", "카테고리 코드"});
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");
        filterPanel.add(categoryComboBox);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // 테이블 설정
        tableModel = new DefaultTableModel(new String[]{"상품 코드", "상품 이름", "주문 가격", "공급 가격", "재고 수량", "카테고리 코드"}, 0);
        productTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel blabel = new JLabel("");
        JButton ordButton = new JButton("상품 주문");
        bottomPanel.add(blabel);
        bottomPanel.add(ordButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // 이벤트 리스너 추가
        searchButton.addActionListener(e -> {
            String selectedColumn = (String) categoryComboBox.getSelectedItem(); // 선택된 컬럼
            String searchKeyword = searchField.getText(); // 검색어
            loadProductData(selectedColumn, searchKeyword);
        });
        invenUIButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "재고현황 버튼 클릭됨"));
        ordHistoryButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "주문내역 버튼 클릭됨"));
        incomeHistoryButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "입고신청 내역 버튼 클릭됨"));


        // 초기 데이터 로드
        loadProductData("전체", "");

        // JFrame 표시
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public void loadProductData(String selectedColumn, String searchKeyword) {
        tableModel.setRowCount(0); // 기존 데이터 삭제

        // 콤보박스에서 선택한 값과 데이터베이스 컬럼명을 매핑
        String ColumnName = switch (selectedColumn) {
            case "상품 코드" -> "product_id";
            case "상품 이름" -> "product_name";
            case "주문 가격" -> "order_price";
            case "공급 가격" -> "supply_price";
            case "재고 수량" -> "product_amount";
            case "카테고리 코드" -> "category_id";
            default -> null; // "전체" 또는 비정상적인 값일 경우
        };

        String query = "SELECT * FROM product";

        // 검색 조건 추가
        if (ColumnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
            query += " WHERE " + ColumnName + " = ?";
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wms", "root", "1234");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // 검색 조건이 있는 경우 파라미터 설정
            if (ColumnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
                stmt.setString(1, searchKeyword);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("order_price"),
                        rs.getString("supply_price"),
                        rs.getString("product_amount"),
                        rs.getInt("category_id")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
        }
    }
}
