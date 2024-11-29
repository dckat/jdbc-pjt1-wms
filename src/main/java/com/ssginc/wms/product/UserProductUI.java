package com.ssginc.wms.product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class UserProductUI extends JFrame {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;

    public UserProductUI() {
        ProductDAO dao = new ProductDAO();
        // JFrame 설정
        setTitle("재고 관리 시스템");
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

        JLabel welcomeLabel = new JLabel("< " + "user1" + " > 님 환영합니다.     ", SwingConstants.RIGHT);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.setBackground(Color.GRAY);
        topPanel.add(dropdownButton, BorderLayout.EAST);
        dropdownButton.setBackground(Color.LIGHT_GRAY);
        add(topPanel, BorderLayout.NORTH);

        // Left Panel
        JPanel leftPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JLabel llabel = new JLabel("");
        JButton invenUIButton = new JButton("재 고  현 황");
        JButton orderUIButton = new JButton("주 문  내 역");
        JButton incomeButton = new JButton("입고신청 내역");
        leftPanel.add(llabel);
        leftPanel.add(invenUIButton);
        leftPanel.add(orderUIButton);
        leftPanel.add(incomeButton);
        leftPanel.setPreferredSize(new Dimension(150, 600));
        add(leftPanel, BorderLayout.WEST);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        categoryComboBox = new JComboBox<>(new String[]{"전체", "상품코드", "상품이름", "분류코드", "분류이름", "상품가격"});
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");
        filterPanel.add(categoryComboBox);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // 테이블 설정
        tableModel = new DefaultTableModel(new String[]{"상품코드", "상품이름", "분류코드", "분류이름", "상품가격", "재고수량"}, 0);
        productTable = new JTable(tableModel);

        ArrayList<UserProductVO> proList = dao.listUserProduct();
        for (UserProductVO data: proList) {
            Vector<Object> v = new Vector<>();
            v.add(data.getProductId());
            v.add(data.getProductName());
            v.add(data.getCategoryCode());
            v.add(data.getCategoryName());
            v.add(data.getOrderPrice());
            v.add(data.getProductAmount());
            tableModel.addRow(v);
        }

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

        // JFrame 표시
        setVisible(true);
    }
}
