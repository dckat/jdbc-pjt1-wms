package com.ssginc.wms.supply;

import com.ssginc.wms.incomeApply.IncomeApplyService;
import com.ssginc.wms.product.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AdminIncomeApplyUI extends JFrame {
    private JTable applicationTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;
    private IncomeApplyDAO incomeApplyDAO;
    Color color = new Color(0x615959);

    public AdminIncomeApplyUI(String id) {
        incomeApplyDAO = new IncomeApplyDAO();

        // JFrame 설정
        setTitle("입고 신청 내역 시스템_Admin");
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
        JButton allApplyButton = new JButton("전체 내역");
        JButton yetApplyButton = new JButton("미승인 내역");

        // 버튼 스타일 설정
        allApplyButton.setFont(fontC);
        allApplyButton.setBackground(color);
        allApplyButton.setForeground(Color.WHITE);
        yetApplyButton.setFont(fontC);
        yetApplyButton.setBackground(color);
        yetApplyButton.setForeground(Color.WHITE);
        leftButtonPanel.add(allApplyButton);
        leftButtonPanel.add(yetApplyButton);

        // 오른쪽에 위치할 검색 패널
        JPanel rightSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        categoryComboBox = new JComboBox<>(new String[]{"전체", "상품 코드", "상품 이름", "주문 가격", "공급 가격", "재고 수량", "카테고리 코드", "카테고리명"});
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
        tableModel = new DefaultTableModel(
                new String[]{"신청 코드", "상품 코드", "상품명", "카테고리", "신청자 ID", "신청 시간", "승인 상태"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: // 신청 ID
                    case 1: // 상품 ID
                        return Integer.class;
                    case 5: // 신청 시간
                        return LocalDateTime.class;
                    default:
                        return String.class;
                }
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 모든 셀을 편집 불가능하게 설정
            }
        };
        applicationTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(applicationTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel blabel = new JLabel("");
        JButton approveApplyButton = new JButton("신청 승인(발주)");
        bottomPanel.add(blabel);
        bottomPanel.add(approveApplyButton);
        approveApplyButton.setFont(fontC);
        add(bottomPanel, BorderLayout.SOUTH);

        // 이벤트 리스너 추가
        searchButton.addActionListener(e -> {
            String selectedColumn = (String) categoryComboBox.getSelectedItem(); // 선택된 컬럼
            String searchKeyword = searchField.getText(); // 검색어
            loadProductData(selectedColumn, searchKeyword);
        });

        allApplyButton.addActionListener(e -> {
            tableModel.setRowCount(0); // 기존 데이터 삭제
            List<ProductIncomeApplyVO> applications = incomeApplyDAO.listIncomeApply(null, null);
            for (ProductIncomeApplyVO application : applications) {
                tableModel.addRow(new Object[]{
                        IncomeApplyService.encodeApplyId(application.getApplyId()),
                        ProductService.encodeProductId(application.getProductId()),
                        application.getProductName(),
                        application.getCategoryName(),
                        application.getUserId(),
                        application.getApplyTime(),
                        application.getApplyStatus()
                });
            }
        });
        yetApplyButton.addActionListener(e -> {
            tableModel.setRowCount(0); // 기존 데이터 삭제
            List<ProductIncomeApplyVO> applications = incomeApplyDAO.listPendingIncomeApplies();
            for (ProductIncomeApplyVO application : applications) {
                tableModel.addRow(new Object[]{
                        IncomeApplyService.encodeApplyId(application.getApplyId()),
                        ProductService.encodeProductId(application.getProductId()),
                        application.getProductName(),
                        application.getCategoryName(),
                        application.getUserId(),
                        application.getApplyTime(),
                        application.getApplyStatus()
                });
            }
        });

        ioUIButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "입출고 관리 버튼 클릭됨"));
        incomeButton.addActionListener(e -> {
            new AdminIncomeApplyUI(id);  // 새로운 UI 열기
            this.dispose();        // 현재 창 닫기
        });
        ordButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "주문 관리 버튼 클릭됨"));
        purordButton.addActionListener(e -> {
            new ListSupplyUI(id);  // 새로운 UI 열기
            this.dispose();        // 현재 창 닫기
        });

        approveApplyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supplySelectedRows();
            }
        });
    }

    public void loadProductData(String selectedColumn, String searchKeyword) {
        tableModel.setRowCount(0); // 기존 데이터 삭제

        List<ProductIncomeApplyVO> applications = incomeApplyDAO.listIncomeApply(selectedColumn, searchKeyword);

        for (ProductIncomeApplyVO application : applications) {
            tableModel.addRow(new Object[]{
                    IncomeApplyService.encodeApplyId(application.getApplyId()),
                    ProductService.encodeProductId(application.getProductId()),
                    application.getProductName(),
                    application.getCategoryName(),
                    application.getUserId(),
                    application.getApplyTime(),
                    application.getApplyStatus()
            });
        }
    }

    private void supplySelectedRows() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "승인할 신청을 선택해주세요.");
            return;
        }

        // 승인 상태 조건 확인 기능
        String applyStatus = tableModel.getValueAt(selectedRow, 6).toString();
        if (applyStatus.equals("completed")) {
            JOptionPane.showMessageDialog(this, "이미 승인된 요청입니다.");
            return;
        }

        // 선택된 행에서 상품 ID를 가져옵니다.
        int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString()); // 상품 ID가 있는 열의 인덱스를 확인하세요.
        String productName = tableModel.getValueAt(selectedRow, 2).toString(); // 상품 이름

        // 사용자에게 발주 수량을 입력받습니다.
        String input = JOptionPane.showInputDialog(this,
                "상품명: " + productName + "\n발주 수량을 입력하세요:",
                "발주 승인",
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
            incomeApplyDAO.registerApply(supplyVO);

            JOptionPane.showMessageDialog(this, "입고 신청이 성공적으로 승인되었습니다.");
            loadProductData("전체", ""); // 테이블 데이터를 새로고침합니다.

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 숫자를 입력해주세요.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
        }
    }
}
