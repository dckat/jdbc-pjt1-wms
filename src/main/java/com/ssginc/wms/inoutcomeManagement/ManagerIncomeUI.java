package com.ssginc.wms.inoutcomeManagement;
import com.ssginc.wms.inoutcomeManagement.SupplyDAO;
import com.ssginc.wms.supply.SupplyProductVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ManagerIncomeUI extends JFrame {
    private JTable table;
    private SupplyDAO supplyDAO;
    Color color = new Color(0x615959);
    private DefaultTableModel model;  // 테이블 모델을 클래스 변수로 선언
    private List<SupplyProductVO> supplyList;  // 원본 데이터를 저장할 리스트

    public ManagerIncomeUI() {
        supplyDAO = new SupplyDAO();
        setBounds(100, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        Font fontT = new Font("맑은 고딕", Font.BOLD, 16);

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

        JLabel welcomeLabel = new JLabel("< " + "admin" + " > 님 환영합니다.     ", SwingConstants.RIGHT);
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
        invenUIButton.setFont(fontT);
        JButton ioUIButton = new JButton("입출고 현황");
        ioUIButton.setBackground(color);
        ioUIButton.setFont(fontT);
        JButton incomeButton = new JButton("입고신청 관리");
        incomeButton.setBackground(color);
        incomeButton.setFont(fontT);
        JButton ordButton = new JButton("주 문  관 리");
        ordButton.setBackground(color);
        ordButton.setFont(fontT);
        JButton purordButton = new JButton("발 주  관 리");
        purordButton.setBackground(color);
        purordButton.setFont(fontT);
        leftPanel.add(llabel);
        leftPanel.add(invenUIButton);
        leftPanel.add(ioUIButton);
        leftPanel.add(incomeButton);
        leftPanel.add(ordButton);
        leftPanel.add(purordButton);
        leftPanel.setPreferredSize(new Dimension(150, 600));
        add(leftPanel, BorderLayout.WEST);

        // Center Panel (with column dropdown and filter button)
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton oneWeekButton = new JButton("최근 1주");
        JButton oneMonthButton = new JButton("최근 1개월");
        JButton threeMonthsButton = new JButton("최근 3개월");
        filterPanel.add(oneWeekButton);
        filterPanel.add(oneMonthButton);
        filterPanel.add(threeMonthsButton);

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

        // JTable에 표시할 데이터 준비
        supplyList = supplyDAO.getSuppliesWithProductAndCategory();

        // 테이블 모델 생성
        model = new DefaultTableModel(columnNames, 0);

        // 테이블에 데이터 추가
        for (SupplyProductVO supply : supplyList) {
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
            model.addRow(row);
        }

        // JTable 생성
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // 하단에 출고 현황 버튼과 새로고침 버튼을 추가하는 부분
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // 버튼을 중앙에 배치
        JButton outStockButton = new JButton("출고 현황");
        bottomPanel.add(outStockButton);  // 출고 현황 버튼 추가
        bottomPanel.add(refreshButton);  // 새로고침 버튼 추가
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);  // centerPanel의 하단에 버튼을 배치

        outStockButton.addActionListener(e -> {
            dispose();
            new ManagerOutcomeUI();
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

        // 새로고침 버튼 클릭 이벤트 처리
        refreshButton.addActionListener(e -> {
            // 테이블 모델을 원본 데이터로 갱신
            model.setRowCount(0);  // 기존 데이터 제거
            for (SupplyProductVO supply : supplyList) {
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
                model.addRow(row);
            }
        });

        add(centerPanel, BorderLayout.CENTER);

        // 프레임 표시
        setVisible(true);
    }
}
