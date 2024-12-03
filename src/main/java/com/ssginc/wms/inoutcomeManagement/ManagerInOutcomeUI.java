package com.ssginc.wms.inoutcomeManagement;

import com.ssginc.wms.frame.CustomerFrame;
import com.ssginc.wms.supply.SupplyDAO;
import com.ssginc.wms.supply.SupplyProductVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class ManagerInOutcomeUI extends CustomerFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel inputPanel;
    private JPanel outputPanel;
    private JComboBox<String> inputSearchCriteriaComboBox;
    private JComboBox<String> outputSearchCriteriaComboBox;
    private JTextField inputSearchField;
    private JTextField outputSearchField;

    public ManagerInOutcomeUI(List<SupplyProductVO> supplyProducts, List<OutComeProductVO> orderProducts) {
        super();
        setTitle("입고 및 출고 현황");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 입고 현황 테이블 패널 생성
        inputPanel = new JPanel(new BorderLayout());

        inputSearchCriteriaComboBox = new JComboBox<>(new String[]{"입고 코드", "상품 이름", "카테고리 이름"});
        inputSearchCriteriaComboBox.addActionListener(e -> updateInputTable(supplyProducts));

        inputSearchField = new JTextField();
        inputSearchField.setPreferredSize(new Dimension(150, 30));
        inputSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateInputTable(supplyProducts);
            }
        });

        String[] inputColumnNames = {"입고 코드", "상품 코드", "상품 이름", "입고 단가", "입고량", "총 금액", "카테고리 이름", "입고일"};
        DefaultTableModel inputTableModel = new DefaultTableModel(inputColumnNames, 0);
        JTable inputTable = new JTable(inputTableModel);
        JScrollPane inputScrollPane = new JScrollPane(inputTable);

        JPanel inputTopPanel = new JPanel(new BorderLayout());
        JPanel inputSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        inputSearchPanel.add(new JLabel("검색 조건:"));
        inputSearchPanel.add(inputSearchCriteriaComboBox);
        inputSearchPanel.add(new JLabel("검색어:"));
        inputSearchPanel.add(inputSearchField);

        inputTopPanel.add(inputSearchPanel, BorderLayout.CENTER);

        inputPanel.add(inputTopPanel, BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        // 출고 현황 테이블 패널 생성
        outputPanel = new JPanel(new BorderLayout());

        outputSearchCriteriaComboBox = new JComboBox<>(new String[]{"출고 코드", "상품 이름", "카테고리 이름"});
        outputSearchCriteriaComboBox.addActionListener(e -> updateOutputTable(orderProducts));

        outputSearchField = new JTextField();
        outputSearchField.setPreferredSize(new Dimension(150, 30));
        outputSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateOutputTable(orderProducts);
            }
        });

        String[] outputColumnNames = {"출고 코드", "상품 코드", "상품 이름", "출고 단가", "카테고리 이름", "출고량", "출고일"};
        DefaultTableModel outputTableModel = new DefaultTableModel(outputColumnNames, 0);
        JTable outputTable = new JTable(outputTableModel);
        JScrollPane outputScrollPane = new JScrollPane(outputTable);

        JPanel outputTopPanel = new JPanel(new BorderLayout());
        JPanel outputSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        outputSearchPanel.add(new JLabel("검색 조건:"));
        outputSearchPanel.add(outputSearchCriteriaComboBox);
        outputSearchPanel.add(new JLabel("검색어:"));
        outputSearchPanel.add(outputSearchField);

        outputTopPanel.add(outputSearchPanel, BorderLayout.CENTER);

        outputPanel.add(outputTopPanel, BorderLayout.NORTH);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        // 버튼을 클릭하여 테이블을 전환
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton inputButton = new JButton("입고 현황");
        JButton outputButton = new JButton("출고 현황");

        inputButton.setPreferredSize(new Dimension(100, 30));
        outputButton.setPreferredSize(new Dimension(100, 30));
        inputButton.setBackground(new Color(0xADD8E6));
        outputButton.setBackground(new Color(0xADD8E6));

        inputButton.addActionListener(e -> cardLayout.show(cardPanel, "입고"));
        outputButton.addActionListener(e -> cardLayout.show(cardPanel, "출고"));

        buttonPanel.add(inputButton);
        buttonPanel.add(outputButton);

        cardPanel.add(inputPanel, "입고");
        cardPanel.add(outputPanel, "출고");

        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);  // 카드 패널을 중앙에 배치
        add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널을 하단에 배치

        updateInputTable(supplyProducts);
        updateOutputTable(orderProducts);
    }

    private void updateInputTable(List<SupplyProductVO> supplyProducts) {
        String selectedSearchCriteria = (String) inputSearchCriteriaComboBox.getSelectedItem();
        String searchKeyword = inputSearchField.getText().toLowerCase();

        List<SupplyProductVO> filteredSupplies = supplyProducts.stream()
                .filter(vo -> (selectedSearchCriteria.equals("입고 코드") && Integer.toString(vo.getSupplyId()).contains(searchKeyword) ||
                        selectedSearchCriteria.equals("상품 이름") && vo.getProductName().toLowerCase().contains(searchKeyword) ||
                        selectedSearchCriteria.equals("카테고리 이름") && vo.getCategoryName().toLowerCase().contains(searchKeyword)))
                .collect(Collectors.toList());

        DefaultTableModel inputTableModel = (DefaultTableModel) ((JTable) ((JScrollPane) inputPanel.getComponent(1)).getViewport().getView()).getModel();
        inputTableModel.setRowCount(0);

        for (SupplyProductVO vo : filteredSupplies) {
            inputTableModel.addRow(new Object[]{
                    vo.getSupplyId(),
                    vo.getProductId(),
                    vo.getProductName(),
                    vo.getSupplyPrice(),
                    vo.getSupplyAmount(),
                    vo.getTotalPrice(), // 추가된 필드
                    vo.getCategoryName(),
                    vo.getSupplyTime() != null ? vo.getSupplyTime().toLocalDate().toString() : "N/A"
            });
        }
    }

    private void updateOutputTable(List<OutComeProductVO> orderProducts) {
        String selectedSearchCriteria = (String) outputSearchCriteriaComboBox.getSelectedItem();
        String searchKeyword = outputSearchField.getText().toLowerCase();

        List<OutComeProductVO> filteredOrders = orderProducts.stream()
                .filter(vo -> (selectedSearchCriteria.equals("출고 코드") && Integer.toString(vo.getOrdId()).contains(searchKeyword) ||
                        selectedSearchCriteria.equals("상품 이름") && vo.getProductName().toLowerCase().contains(searchKeyword) ||
                        selectedSearchCriteria.equals("카테고리 이름") && vo.getCategoryName().toLowerCase().contains(searchKeyword)))
                .collect(Collectors.toList());

        DefaultTableModel outputTableModel = (DefaultTableModel) ((JTable) ((JScrollPane) outputPanel.getComponent(1)).getViewport().getView()).getModel();
        outputTableModel.setRowCount(0);

        for (OutComeProductVO vo : filteredOrders) {
            outputTableModel.addRow(new Object[]{
                    vo.getOrdId(),
                    vo.getProductId(),
                    vo.getProductName(),
                    vo.getOrdPrice(),
                    vo.getCategoryName(),
                    vo.getOrdAmount(),
                    vo.getOrdCompleteTime() != null ? vo.getOrdCompleteTime().toLocalDate().toString() : "N/A"
            });
        }
    }
}
