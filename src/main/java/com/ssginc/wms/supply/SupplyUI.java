package com.ssginc.wms.supply;


import com.ssginc.wms.hikari.HikariCPDataSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SupplyUI extends JFrame {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;
    Color color = new Color(0x615959);


    public SupplyUI(String id) {
        // JFrame 설정
        setTitle("발주 등록 시스템_Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout()); // 기본 레이아웃 설정
        Font fontT  = new Font("맑은 고딕", Font.BOLD, 16);
        Font fontC = new Font("맑은 고딕", Font.BOLD,12);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(1200, 30));
        JButton dropdownButton = new JButton("≡");
        dropdownButton.setSize(30, 30);

        // JPopupMenu 생성
        JPopupMenu menu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("로그아웃");
        menu.add(logoutItem);
        dropdownButton.addActionListener(e -> menu.show(dropdownButton, 0, dropdownButton.getHeight()));

        JLabel welcomeLabel = new JLabel("< " + id + " > 님 환영합니다.     ", SwingConstants.RIGHT);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.setBackground(Color.GRAY);
        topPanel.add(dropdownButton, BorderLayout.EAST);
        dropdownButton.setBackground(Color.LIGHT_GRAY);
        add(topPanel, BorderLayout.NORTH);

        // Left Panel
        JPanel leftPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        JLabel llabel = new JLabel("");
        JButton invenUIButton = new JButton("재 고  현 황");
        invenUIButton.setBackground(color);
        invenUIButton.setForeground(Color.white);
        invenUIButton.setFont(fontT);
        JButton ioUIButton = new JButton("입출고 현황");
        ioUIButton.setBackground(color);
        ioUIButton.setForeground(Color.white);
        ioUIButton.setFont(fontT);
        JButton incomeButton = new JButton("입고신청 관리");
        incomeButton.setBackground(color);
        incomeButton.setForeground(Color.white);
        incomeButton.setFont(fontT);
        JButton ordButton = new JButton("주 문  관 리");
        ordButton.setBackground(color);
        ordButton.setForeground(Color.white);
        ordButton.setFont(fontT);
        JButton purordButton = new JButton("발 주  관 리");
        purordButton.setBackground(color);
        purordButton.setForeground(Color.white);
        purordButton.setFont(fontT);

        leftPanel.add(llabel);
        leftPanel.add(invenUIButton);
        leftPanel.add(ioUIButton);
        leftPanel.add(incomeButton);
        leftPanel.add(ordButton);
        leftPanel.add(purordButton);
        leftPanel.setPreferredSize(new Dimension(150, 600));
        add(leftPanel, BorderLayout.WEST);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new BorderLayout()); // BorderLayout 사용

        // 왼쪽에 위치할 버튼 패널
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton orderListButton = new JButton("발주 내역");
        JButton orderRegisterButton = new JButton("발주 등록");

        // 버튼 스타일 설정
        orderListButton.setFont(fontC);
        orderListButton.setBackground(color);
        orderListButton.setForeground(Color.WHITE);
        orderRegisterButton.setFont(fontC);
        orderRegisterButton.setBackground(color);
        orderRegisterButton.setForeground(Color.WHITE);
        leftButtonPanel.add(orderListButton);
        leftButtonPanel.add(orderRegisterButton);

        // 오른쪽에 위치할 검색 패널
        JPanel rightSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        categoryComboBox = new JComboBox<>(new String[]{"전체", "상품 코드", "상품 이름", "주문 가격", "공급 가격", "재고 수량", "카테고리 코드", "카테고리 이름"});
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");
        rightSearchPanel.add(categoryComboBox);
        rightSearchPanel.add(searchField);
        rightSearchPanel.add(searchButton);

        // 왼쪽과 오른쪽 패널을 필터 패널에 배치
        filterPanel.add(leftButtonPanel, BorderLayout.WEST);
        filterPanel.add(rightSearchPanel, BorderLayout.EAST);
        centerPanel.add(filterPanel, BorderLayout.NORTH);


        // 테이블 설정
        tableModel = new DefaultTableModel(new String[]{"카테고리 ID", "카테고리 이름", "상품 ID", "상품 이름", "재고 수량", "공급 가격", "주문 가격"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 2 || columnIndex == 4 || columnIndex == 5 || columnIndex == 6) {
                    return Integer.class;
                } else {
                    return String.class;
                }
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 모든 셀을 편집 불가능하게 설정
            }
        };
        productTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel blabel = new JLabel("");
        JButton addSupplyButton = new JButton("선택 상품 발주");
        bottomPanel.add(blabel);
        bottomPanel.add(addSupplyButton);
        addSupplyButton.setFont(fontC);
        add(bottomPanel, BorderLayout.SOUTH);

        // 이벤트 리스너 추가
        searchButton.addActionListener(e -> {
            String selectedColumn = (String) categoryComboBox.getSelectedItem(); // 선택된 컬럼
            String searchKeyword = searchField.getText(); // 검색어
            loadProductData(selectedColumn, searchKeyword);
        });

        orderListButton.addActionListener(e -> {
            new ListSupplyUI(id);  // 발주 내역 화면 열기
            this.dispose();
        });
        orderRegisterButton.addActionListener(e -> {
            new SupplyUI(id);  // 발주 등록 화면 열기
            this.dispose();
        });

//        invenUIButton.addActionListener(e -> {
//            new InventoryAdminFrame("admin");
//            this.dispose();
//        });
        ioUIButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "입출고 관리 버튼 클릭됨"));
        incomeButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "입고신청 관리 버튼 클릭됨"));
        ordButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "주문 관리 버튼 클릭됨"));
        purordButton.addActionListener(e -> {
            new ListSupplyUI(id);  // 새로운 UI 열기
            this.dispose();        // 현재 창 닫기
        });

        addSupplyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supplySelectedRows();
            }
        });

        // 초기 데이터 로드
        loadProductData("전체", "");

        // JFrame 표시
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public void loadProductData(String selectedColumn, String searchKeyword) {
        tableModel.setRowCount(0); // 기존 데이터 삭제

        // 콤보박스에서 선택한 값과 데이터베이스 컬럼명을 매핑
        String columnName = switch (selectedColumn) {
            case "상품 ID" -> "p.product_id";
            case "상품 이름" -> "p.product_name";
            case "주문 가격" -> "p.ord_price";
            case "공급 가격" -> "p.supply_price";
            case "재고 수량" -> "p.product_amount";
            case "카테고리 ID" -> "p.category_id";
            case "카테고리 이름" -> "pc.category_name";
            default -> null; // "전체" 또는 비정상적인 값일 경우
        };

        String query = """
        SELECT 
            p.category_id, 
            pc.category_name, 
            p.product_id, 
            p.product_name, 
            p.product_amount, 
            p.supply_price, 
            p.ord_price
        FROM PRODUCT p
        JOIN PRODUCT_CATEGORY pc 
        ON p.category_id = pc.category_id
        """;

        // 검색 조건 추가
        if (columnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
            query += " WHERE " + columnName + " = ?";
        }

        try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // 검색 조건이 있는 경우 파라미터 설정
            if (columnName != null && searchKeyword != null && !searchKeyword.isEmpty()) {
                stmt.setString(1, searchKeyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("category_id"),       // 카테고리 코드
                            rs.getString("category_name"), // 카테고리 이름
                            rs.getInt("product_id"),       // 상품 코드
                            rs.getString("product_name"),  // 상품 이름
                            rs.getInt("product_amount"),   // 재고 수량
                            rs.getInt("supply_price"),     // 공급 가격
                            rs.getInt("ord_price")         // 주문 가격
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
        }
    }


    // 발주 등록 기능 KKH-03
    private void supplySelectedRows() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "발주할 상품을 선택해주세요.");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 2); // 상품 ID
        String productName = (String) tableModel.getValueAt(selectedRow, 3); // 상품 이름

        String input = JOptionPane.showInputDialog(this,
                "상품명: " + productName + "\n발주 수량을 입력하세요:",
                "발주 등록",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "수량을 입력해주세요.");
            return; // 사용자가 취소하거나 빈 값을 입력한 경우
        }

        try {
            int supplyAmount = Integer.parseInt(input.trim());
            if (supplyAmount <= 0) {
                JOptionPane.showMessageDialog(this, "유효한 수량을 입력해주세요.");
                return;
            }

            // 데이터베이스 작업 수행
            try (Connection conn = HikariCPDataSource.getInstance().getDataSource().getConnection()) {
                conn.setAutoCommit(false); // 트랜잭션 시작

                // 발주 내역 추가
                String insertSupplySQL = "INSERT INTO Supply (supply_amount, product_id, supply_time) VALUES (?, ?, NOW())";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSupplySQL)) {
                    pstmt.setInt(1, supplyAmount);
                    pstmt.setInt(2, productId);
                    pstmt.executeUpdate();
                }

                // 상품 재고량 업데이트
                String updateProductSQL = "UPDATE PRODUCT SET product_amount = product_amount + ? WHERE product_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateProductSQL)) {
                    pstmt.setInt(1, supplyAmount);
                    pstmt.setInt(2, productId);
                    pstmt.executeUpdate();
                }

                // 입고신청 테이블 입고 상태 업데이트
                String updateIncomeApplySQL = "UPDATE income_apply SET apply_status = 'completed' WHERE product_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateIncomeApplySQL)) {
                    pstmt.setInt(1, productId);
                    pstmt.executeUpdate();
                }

                conn.commit(); // 트랜잭션 커밋
                JOptionPane.showMessageDialog(this, "발주가 성공적으로 등록되었습니다.");
                loadProductData("전체", ""); // 테이블 새로고침
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 숫자를 입력해주세요.");
        }
    }

}

