package com.ssginc.wms.product;

import com.ssginc.wms.frame.CustomerFrame;
import com.ssginc.wms.incomeApply.IncomeApplyDAO;
import com.ssginc.wms.incomeApply.IncomeApplyVO;
import com.ssginc.wms.ord.OrdDAO;
import com.ssginc.wms.ord.OrdVO;
import com.ssginc.wms.util.DecodeId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Vector;

public class CustomerProductUI extends CustomerFrame {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;

    public CustomerProductUI(String id) {
        super(id);
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
        tableModel = new DefaultTableModel(new String[]{"분류코드", "분류이름", "상품코드", "상품이름", "상품단가", "재고수량"}, 0);
        productTable = new JTable(tableModel);

        ArrayList<CustomerProductVO> proList = dao.listUserProduct();
        addElement(proList);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

            ArrayList<CustomerProductVO> filteredList = dao.searchProductByKeyword(keyword, searchColumn);
            addElement(filteredList);
        });

        orderButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                int productId = DecodeId.decodeId(productTable.getValueAt(selectedRow, 2).toString());
                String productName = tableModel.getValueAt(selectedRow, 3).toString();
                String message = "상품명: " + productName + "\n 주문 수량을 입력하세요:";
                String input = JOptionPane.showInputDialog(this, message,
                        "주문 하기", JOptionPane.QUESTION_MESSAGE);

                if (input == null || input.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "주문 수량을 입력해주세요.");
                }
                else {
                    try {
                        int amount = Integer.parseInt(input);
                        int inventoryAmount = Integer.parseInt(tableModel.getValueAt(selectedRow, 5).toString());

                        if (amount <= 0) {
                            JOptionPane.showMessageDialog(this, "0 이상의 숫자를 입력해주세요.");
                        }
                        else {
                            if (amount < inventoryAmount) {
                                OrdDAO ordDao = new OrdDAO();
                                OrdVO ordVo = new OrdVO();
                                ordVo.setProductId(productId);
                                ordVo.setOrderAmount(amount);
                                ordVo.setOrderTime(LocalDateTime.now());
                                ordVo.setUserId(id);
                                ordDao.insertOrder(ordVo);
                                JOptionPane.showMessageDialog(this, "주문이 접수되었습니다.");
                            }
                            else {
                                JOptionPane.showMessageDialog(this, "재고량이 부족하여 입고신청을 진행합니다.");
                                // 입고 신청 내역 insert 작성 필요
                                IncomeApplyDAO incomeApplyDAO = new IncomeApplyDAO();
                                IncomeApplyVO incomeApplyVO = new IncomeApplyVO();

                                incomeApplyVO.setProductId(productId); // 선택된 상품 ID
                                incomeApplyVO.setUserId(id);
                                incomeApplyVO.setApplyTime(LocalDateTime.now()); // 현재 시간

                                // 입고 신청 데이터를 삽입
                                incomeApplyDAO.insertIncomeApply(incomeApplyVO);

                                JOptionPane.showMessageDialog(this, "입고 신청이 완료되었습니다.");
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(this, "유효하지 않은 숫자입니다.");
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "주문하려는 상품이 없습니다.");
            }
        });

        // JFrame 표시
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void addElement(ArrayList<CustomerProductVO> list) {
        for (CustomerProductVO data: list) {
            Vector<Object> v = new Vector<>();
            v.add(ProductService.encodeCategoryId(data.getCategoryId()));
            v.add(data.getCategoryName());
            v.add(ProductService.encodeProductId(data.getProductId()));
            v.add(data.getProductName());
            v.add(data.getOrderPrice());
            v.add(data.getProductAmount());
            tableModel.addRow(v);
        }
    }
}
