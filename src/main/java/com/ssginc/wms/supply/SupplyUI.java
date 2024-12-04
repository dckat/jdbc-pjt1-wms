package com.ssginc.wms.supply;


import com.ssginc.wms.frame.AdminFrame;
import com.ssginc.wms.product.ProductService;
import com.ssginc.wms.util.DecodeId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SupplyUI extends AdminFrame {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;
    private SupplyDAO supplyDAO;
    Color color = new Color(0x615959);

    public SupplyUI(String id) {
        // JFrame 설정
        super(id);
        setTitle("발주 등록 시스템_Admin");
        setupUI(id);

        // 초기 데이터 로드
        supplyDAO = new SupplyDAO();
        loadProductData("전체", "");

        // JFrame 표시
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI(String id) {
        Font fontC = new Font("맑은 고딕", Font.BOLD,12);

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
        categoryComboBox = new JComboBox<>(new String[]{"상품이름", "분류이름"});
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
        tableModel = new DefaultTableModel(new String[]{"분류코드", "분류이름", "상품코드", "상품이름", "재고수량", "발주단가", "주문단가"}, 0) {
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
            String searchKeyword = searchField.getText().trim(); // 검색어

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

        addSupplyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supplySelectedRows();
            }
        });
    }


    public void loadProductData(String selectedColumn, String searchKeyword) {
        tableModel.setRowCount(0); // 기존 데이터 삭제

        String columnName = switch (selectedColumn) {
            case "상품이름" -> "p.product_name";
            case "분류이름" -> "pc.category_name";
            default -> null;
        };

        List<CategoryProductVO> products = supplyDAO.listSupplyByKeyword(columnName, searchKeyword);
        for (CategoryProductVO product : products) {
            tableModel.addRow(new Object[]{
                    ProductService.encodeCategoryId(product.getCategoryId()),
                    product.getCategoryName(),
                    ProductService.encodeProductId(product.getProductId()),
                    product.getProductName(),
                    product.getProductAmount(),
                    product.getSupplyPrice(),
                    product.getOrdPrice()
            });
        }
    }


    private void supplySelectedRows() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "발주할 상품을 선택해주세요.");
            return;
        }

        // 선택된 행에서 상품 ID를 가져옵니다.
        int productId = DecodeId.decodeId(tableModel.getValueAt(selectedRow, 2).toString()); // 상품 ID가 있는 열의 인덱스를 확인하세요.
        String productName = (String) tableModel.getValueAt(selectedRow, 3); // 상품 이름

        // 사용자에게 발주 수량을 입력받습니다.
        String input = JOptionPane.showInputDialog(this,
                "상품명: " + productName + "\n발주 수량을 입력하세요:",
                "발주 등록",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            // 사용자가 취소한 경우 아무 작업도 하지 않고 리턴
            return;
        }

        if (input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "수량을 입력해주세요.");
            return;
        }

        try {
            int supplyAmount = Integer.parseInt(input.trim());
            if (supplyAmount <= 0) {
                JOptionPane.showMessageDialog(this, "유효한 수량을 입력해주세요.");
                return;
            }

            // SupplyVO 객체 생성 및 값 설정
            SupplyVO supplyVO = new SupplyVO();
            supplyVO.setProductId(productId);
            supplyVO.setSupplyAmount(supplyAmount);

            // DAO 메서드를 호출하여 발주를 등록합니다.
            supplyDAO.insertSupply(supplyVO);

            JOptionPane.showMessageDialog(this, "발주가 성공적으로 등록되었습니다.");
            loadProductData("전체", ""); // 테이블 데이터를 새로고침합니다.

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 숫자를 입력해주세요.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
        }
    }

}

