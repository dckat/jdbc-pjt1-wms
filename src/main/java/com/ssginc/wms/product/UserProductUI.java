package com.ssginc.wms.product;

import com.ssginc.wms.frame.CustomerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class UserProductUI extends CustomerFrame {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;

    public UserProductUI() {
        super();
        ProductDAO dao = new ProductDAO();
        // JFrame 설정
        setTitle("구매자 재고현황");

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        categoryComboBox = new JComboBox<>(new String[]{"상품이름", "분류이름"});
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");
        filterPanel.add(categoryComboBox);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // 테이블 설정
        tableModel = new DefaultTableModel(new String[]{"상품코드", "상품이름", "분류코드", "분류이름", "상품단가", "재고수량"}, 0);
        productTable = new JTable(tableModel);

        ArrayList<UserProductVO> proList = dao.listUserProduct();
        addElement(proList);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel blabel = new JLabel("");
        JButton orderButton = new JButton("주문 하기");
        bottomPanel.add(blabel);
        bottomPanel.add(orderButton);
        add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            String selectedColumn = (String) categoryComboBox.getSelectedItem(); // 선택된 컬럼
            String keyword = searchField.getText();

            String searchColumn = switch (selectedColumn) {
                case "상품이름" -> "product_name";
                case "분류이름" -> "category_name";
                default -> null;
            };

            ArrayList<UserProductVO> filteredList = dao.searchProductByKeyword(keyword, searchColumn);
            addElement(filteredList);
        });

        // JFrame 표시
        setVisible(true);
    }

    public void addElement(ArrayList<UserProductVO> list) {
        for (UserProductVO data: list) {
            Vector<Object> v = new Vector<>();
            v.add(data.getProductId());
            v.add(data.getProductName());
            v.add(data.getCategoryCode());
            v.add(data.getCategoryName());
            v.add(data.getOrderPrice());
            v.add(data.getProductAmount());
            tableModel.addRow(v);
        }
    }
}
