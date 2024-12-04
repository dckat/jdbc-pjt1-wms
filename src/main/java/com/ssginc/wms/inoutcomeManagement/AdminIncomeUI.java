package com.ssginc.wms.inoutcomeManagement;

import com.ssginc.wms.frame.AdminFrame;
import com.ssginc.wms.product.ProductService;
import com.ssginc.wms.supply.SupplyProductVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class AdminIncomeUI extends AdminFrame {
    private JTable table;
    private SupplyDAO supplyDAO;
    Color color = new Color(0x615959);
    private DefaultTableModel model;  // 테이블 모델을 클래스 변수로 선언
    private List<SupplyProductVO> supplyList;  // 원본 데이터를 저장할 리스트

    public AdminIncomeUI(String id) {
        super(id);
        Font fontT = new Font("맑은 고딕", Font.BOLD, 16);

        // Center Panel (with column dropdown and filter button)
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Dropdown for selecting column to filter by
        String[] columnNames = {
                "입고 코드", "상품 코드", "상품 이름", "카테고리 이름", "입고 단가",
                "입고량", "총 금액", "입고일"
        };
        JComboBox<String> columnDropdown = new JComboBox<>(columnNames);
        JTextField filterField = new JTextField(15);
        JButton filterButton = new JButton("검색");
        JButton refreshButton = new JButton("새로고침");  // 새로고침 버튼 추가

        filterPanel.add(columnDropdown);
        filterPanel.add(filterField);
        filterPanel.add(filterButton);
        filterPanel.add(refreshButton);  // 새로고침 버튼을 filterPanel에 추가
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        supplyDAO = new SupplyDAO();
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);

        addIncomeElement();

        // JTable 생성
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 하단에 출고 현황 버튼과 새로고침 버튼을 추가하는 부분
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // 버튼을 중앙에 배치
        JButton outStockButton = new JButton("출고 현황");
        bottomPanel.add(outStockButton);  // 출고 현황 버튼 추가
        bottomPanel.add(refreshButton);  // 새로고침 버튼 추가
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);  // centerPanel의 하단에 버튼을 배치
        add(bottomPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            addIncomeElement();
        });

        outStockButton.addActionListener(e -> {
            dispose();
            new AdminOutcomeUI(id);
        });

        // Add filter functionality to the filter button
        filterButton.addActionListener(e -> {
            String selectedColumn = (String) columnDropdown.getSelectedItem();
            String filterText = filterField.getText().toLowerCase();
            int columnIndex = Arrays.asList(columnNames).indexOf(selectedColumn);

            // Filter the table based on the selected column and filter text
            DefaultTableModel filteredModel = new DefaultTableModel(columnNames, 0);
            for (SupplyProductVO supply : supplyList) {
                // Add condition based on selected column
                String cellValue = "";
                switch (columnIndex) {
                    case 0: cellValue = String.valueOf(supply.getSupplyId()); break;
                    case 1: cellValue = String.valueOf(supply.getProductId()); break;
                    case 2: cellValue = supply.getProductName(); break;
                    case 3: cellValue = supply.getCategoryName(); break;
                    case 4: cellValue = String.valueOf(supply.getSupplyPrice()); break;
                    case 5: cellValue = String.valueOf(supply.getSupplyAmount()); break;
                    case 6: cellValue = String.valueOf(supply.getTotalPrice()); break;
                    case 7: cellValue = supply.getSupplyTime().toString(); break;
                }

                if (cellValue.toLowerCase().contains(filterText)) {
                    Object[] row = new Object[] {
                            supply.getSupplyId(),
                            supply.getProductId(),
                            supply.getProductName(),
                            supply.getCategoryName(),
                            supply.getSupplyPrice(),
                            supply.getSupplyAmount(),
                            supply.getTotalPrice(),
                            supply.getSupplyTime()
                    };
                    filteredModel.addRow(row);
                }
            }
            table.setModel(filteredModel);  // Update table with filtered data
        });

        add(centerPanel, BorderLayout.CENTER);

        // 프레임 표시
        setVisible(true);
    }

    public void addIncomeElement() {
        // JTable에 표시할 데이터 준비
        model.setRowCount(0);

        // 테이블에 데이터 추가
        supplyList = supplyDAO.getSuppliesWithProductAndCategory();
        for (SupplyProductVO supply : supplyList) {
            Vector<Object> v = new Vector<>();
            v.add(InoutcomeManagementService.encodeIncomeId(supply.getSupplyId()));
            v.add(ProductService.encodeProductId(supply.getProductId()));
            v.add(supply.getProductName());
            v.add(supply.getCategoryName());
            v.add(supply.getSupplyPrice());
            v.add(supply.getSupplyAmount());
            v.add(supply.getTotalPrice());
            v.add(supply.getSupplyTime());
            model.addRow(v);
        }
    }
}
