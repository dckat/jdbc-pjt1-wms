package com.ssginc.wms.supply;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Vector;


public class ListSupplyUI extends JFrame {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;
    Color color = new Color(0x615959);


    public ListSupplyUI(String id) {
        // JFrame 설정
        setTitle("발주 내역 시스템_Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout()); // 기본 레이아웃 설정

        setupUI(id);

        // 초기 데이터 로드
        loadProductData("전체", "");

        // JFrame 표시
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI(String id) {
        Font fontT = new Font("맑은 고딕", Font.BOLD, 16);
        Font fontC = new Font("맑은 고딕", Font.BOLD, 12);

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
        categoryComboBox = new JComboBox<>(new String[]{"전체", "발주 코드", "상품 코드", "상품 이름", "카테고리", "공급 가격", "발주 수량", "발주일"});
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
        ioUIButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "입출고 관리 버튼 클릭됨"));
        incomeButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "입고신청 관리 버튼 클릭됨"));
        ordButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "주문 관리 버튼 클릭됨"));
        purordButton.addActionListener(e -> {
            new ListSupplyUI(id);  // 새로운 UI 열기
            this.dispose();        // 현재 창 닫기
        });
    }

    public void loadProductData(String selectedColumn, String searchKeyword) {
        SupplyDAO dao = new SupplyDAO();
        tableModel.setRowCount(0); // 기존 데이터 삭제
        ArrayList<SupplyProductVO> list = dao.listSupply(selectedColumn, searchKeyword);
        addelement(list);
    }

    public void addelement(ArrayList<SupplyProductVO> list) {
        for (SupplyProductVO productVO : list) {
            Vector<Object> v = new Vector<>();
            v.add(SupplyService.encodeSupplyId(productVO.getSupply_id()));
            v.add(productVO.getProduct_id());
            v.add(productVO.getProduct_name());
            v.add(productVO.getCategory_name());
            v.add(productVO.getSupply_price());
            v.add(productVO.getSupply_amount());
            v.add(productVO.getSupply_time());
            tableModel.addRow(v);
        }
    }
}
