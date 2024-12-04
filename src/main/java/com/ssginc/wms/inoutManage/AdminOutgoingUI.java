package com.ssginc.wms.inoutManage;
import com.ssginc.wms.frame.AdminFrame;
import com.ssginc.wms.ord.OrdDAO;
import com.ssginc.wms.product.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class AdminOutgoingUI extends AdminFrame {
    private JTable table;
    private OrdDAO ordDAO;
    Color color = new Color(0x615959);
    private DefaultTableModel model;
    private List<OutgoingProductVO> ordList;


    public AdminOutgoingUI(String id) {
        super(id);
        Font fontT  = new Font("맑은 고딕", Font.BOLD, 16);

        // Center Panel (with column dropdown and filter button)
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Dropdown for selecting column to filter by
        String[] columnNames = {
                "상품이름",
                "분류이름"
        };

        String[] columnNames2 = {
                "출고코드",
                "상품코드",
                "분류이름",
                "상품이름",
                "출고단가",
                "출고수량",
                "출고일"
        };
        JComboBox<String> columnDropdown = new JComboBox<>(columnNames);
        JTextField filterField = new JTextField(15);
        JButton filterButton = new JButton("검색");

        filterPanel.add(columnDropdown);
        filterPanel.add(filterField);
        filterPanel.add(filterButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        ordDAO = new OrdDAO();
        model = new DefaultTableModel(columnNames2, 0);
        table = new JTable(model);

        addOutcomeElement();

        // JTable에 표시할 데이터 준비
       JScrollPane scrollPane = new JScrollPane(table);
       centerPanel.add(scrollPane, BorderLayout.CENTER);
       add(centerPanel, BorderLayout.CENTER);

        // 하단에 입고 현황 버튼과 새로고침 버튼을 추가하는 부분
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));  // 버튼을 우측에 배치
        JButton outStockButton = new JButton("입고 현황");
        bottomPanel.add(outStockButton);  // 입고 현황 버튼 추가
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);  // centerPanel의 하단에 버튼을 배치
        add(bottomPanel, BorderLayout.SOUTH);

        outStockButton.addActionListener(e -> {
            dispose();
            new AdminIncomeUI(id);
        });

        // Add filter functionality to the filter button
        filterButton.addActionListener(e -> {
            String selectedColumn = (String) columnDropdown.getSelectedItem();
            String filterText = filterField.getText().toLowerCase();
            int columnIndex = Arrays.asList(columnNames2).indexOf(selectedColumn);

            // Filter the table based on the selected column and filter text
            DefaultTableModel filteredModel = new DefaultTableModel(columnNames2, 0);
            for (OutgoingProductVO ord : ordList) {
                // Add condition based on selected column
                String cellValue = "";
                switch (columnIndex) {
                    case 0: cellValue = String.valueOf(ord.getOrdId()); break;
                    case 1: cellValue = String.valueOf(ord.getProductId()); break;
                    case 2: cellValue = ord.getCategoryName(); break;
                    case 3: cellValue = ord.getProductName(); break;
                    case 4: cellValue = String.valueOf(ord.getOrdPrice()); break;
                    case 5: cellValue = String.valueOf(ord.getOrdAmount()); break;
                    case 6: cellValue = ord.getOrdCompleteTime().toLocalDate().toString(); break;
                }

                if (cellValue.toLowerCase().contains(filterText)) {
                    Object[] row = new Object[] {
                            ord.getOrdId(),
                            ord.getProductId(),
                            ord.getCategoryName(),
                            ord.getProductName(),
                            ord.getOrdPrice(),
                            ord.getOrdAmount(),
                            ord.getOrdCompleteTime().toLocalDate(),
                    };
                    filteredModel.addRow(row);
                }
            }
            table.setModel(filteredModel);  // Update table with filtered data
        });

        add(centerPanel, BorderLayout.CENTER);

        // 프레임 표시
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void addOutcomeElement() {
        // JTable에 표시할 데이터 준비
        model.setRowCount(0);

        // 테이블에 데이터 추가
        ordList = ordDAO.getCompletedOrd();
        for (OutgoingProductVO ord : ordList) {
            Vector<Object> v = new Vector<>();
            v.add(InOutManageService.encodeOutcomeId(ord.getOrdId()));
            v.add(ProductService.encodeProductId(ord.getProductId()));
            v.add(ord.getCategoryName());
            v.add(ord.getProductName());
            v.add(ord.getOrdPrice());
            v.add(ord.getOrdAmount());
            v.add(ord.getOrdCompleteTime().toLocalDate());
            model.addRow(v);
        }
    }
}