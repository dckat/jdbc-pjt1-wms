package com.ssginc.wms.supply;


import com.ssginc.wms.frame.AdminFrame;
import com.ssginc.wms.product.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Vector;


public class ListSupplyUI extends AdminFrame {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;
    Color color = new Color(0x615959);


    public ListSupplyUI(String id) {
        // JFrame 설정
        super(id);
        setTitle("발주 내역 시스템_Admin");
        setupUI(id);

        // 초기 데이터 로드
        loadProductData("전체", "");

        // JFrame 표시
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI(String id) {
        Font fontC = new Font("맑은 고딕", Font.BOLD, 12);

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
        categoryComboBox = new JComboBox<>(new String[]{"전체", "상품 이름", "카테고리"});
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
        tableModel = new DefaultTableModel(new String[]{"발주 코드", "상품 코드", "상품 이름", "카테고리", "발주 단가", "발주 수량", "발주일"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1 || columnIndex == 4 || columnIndex == 5) {
                    return Integer.class;
                } else if (columnIndex == 6) {
                    return LocalDateTime.class;
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
        bottomPanel.add(blabel);
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
    }

    public void loadProductData(String selectedColumn, String searchKeyword) {
        SupplyDAO dao = new SupplyDAO();
        tableModel.setRowCount(0); // 기존 데이터 삭제
        ArrayList<SupplyProductVO> list = dao.listSupply(selectedColumn, searchKeyword);
        addElement(list);
    }

    public void addElement(ArrayList<SupplyProductVO> list) {
        for (SupplyProductVO productVO : list) {
            Vector<Object> v = new Vector<>();
            v.add(SupplyService.encodeSupplyId(productVO.getSupplyId()));
            v.add(ProductService.encodeProductId(productVO.getProductId()));
            v.add(productVO.getProductName());
            v.add(productVO.getCategoryName());
            v.add(productVO.getSupplyPrice());
            v.add(productVO.getSupplyAmount());
            v.add(productVO.getSupplyTime());
            tableModel.addRow(v);
        }
    }
}
