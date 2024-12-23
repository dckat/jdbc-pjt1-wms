package com.ssginc.wms.incomeApply;

import com.ssginc.wms.frame.AdminFrame;
import com.ssginc.wms.product.ProductService;
import com.ssginc.wms.supply.SupplyVO;
import com.ssginc.wms.util.DecodeId;
import com.ssginc.wms.incomeApply.ProductIncomeApplyVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AdminIncomeApplyUI extends AdminFrame {
    private JTable applicationTable;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;
    private IncomeApplyDAO incomeApplyDAO;
    Color color = new Color(0x615959);

    public AdminIncomeApplyUI(String id) {
        // JFrame 설정
        super(id);
        setTitle("입고 신청 내역 시스템_Admin");
        setupUI(id);

        // 초기 데이터 로드
        incomeApplyDAO = new IncomeApplyDAO();
        loadProductData("전체", "");

        // JFrame 표시
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI(String id) {
        Font fontC = new Font("맑은 고딕", Font.BOLD,12);

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
        categoryComboBox = new JComboBox<>(new String[]{"상품이름", "분류이름"});
        JTextField searchField = new JTextField(15);

        // 왼쪽과 오른쪽 패널을 필터 패널에 배치
        filterPanel.add(leftButtonPanel, BorderLayout.WEST);
        filterPanel.add(rightSearchPanel, BorderLayout.EAST);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // 테이블 설정
        tableModel = new DefaultTableModel(
                new String[]{"신청코드", "상품코드", "분류이름", "상품이름", "신청자 ID", "신청일", "승인상태"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: // 신청 코드
                    case 1: // 상품 코드
                        return Integer.class;
                    case 5: // 신청 시간
                        return LocalDate.class;
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
        JButton approveApplyButton = new JButton("신청 승인");
        bottomPanel.add(blabel);
        bottomPanel.add(approveApplyButton);
        approveApplyButton.setFont(fontC);
        add(bottomPanel, BorderLayout.SOUTH);

        allApplyButton.addActionListener(e -> {
            tableModel.setRowCount(0); // 기존 데이터 삭제
            List<ProductIncomeApplyVO> applications = incomeApplyDAO.listIncomeApply(null, null);
            for (ProductIncomeApplyVO application : applications) {
                tableModel.addRow(new Object[]{
                        IncomeApplyService.encodeApplyId(application.getApplyId()),
                        ProductService.encodeProductId(application.getProductId()),
                        application.getCategoryName(),
                        application.getProductName(),
                        application.getUserId(),
                        application.getApplyTime().toLocalDate(),
                        application.getApplyStatus()
                });
            }
        });
        yetApplyButton.addActionListener(e -> {
            tableModel.setRowCount(0); // 기존 데이터 삭제

            String selectedColumn = (String) categoryComboBox.getSelectedItem(); // 선택된 컬럼
            String searchKeyword = searchField.getText().trim(); // 검색어

            String columnName = switch (selectedColumn) {
                case "상품이름" -> "p.product_name";
                case "분류이름" -> "pc.category_name";
                default -> null;
            };

            List<ProductIncomeApplyVO> applications = incomeApplyDAO.listPendingIncomeApplies(columnName, searchKeyword);
            for (ProductIncomeApplyVO application : applications) {
                tableModel.addRow(new Object[]{
                        IncomeApplyService.encodeApplyId(application.getApplyId()),
                        ProductService.encodeProductId(application.getProductId()),
                        application.getCategoryName(),
                        application.getProductName(),
                        application.getUserId(),
                        application.getApplyTime().toLocalDate(),
                        application.getApplyStatus()
                });
            }
        });

        approveApplyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supplySelectedRows();
            }
        });
    }

    public void loadProductData(String columnName, String searchKeyword) {
        tableModel.setRowCount(0); // 기존 데이터 삭제

        List<ProductIncomeApplyVO> applications = incomeApplyDAO.listIncomeApply(columnName, searchKeyword);
        for (ProductIncomeApplyVO application : applications) {
            tableModel.addRow(new Object[]{
                    IncomeApplyService.encodeApplyId(application.getApplyId()),
                    ProductService.encodeProductId(application.getProductId()),
                    application.getCategoryName(),
                    application.getProductName(),
                    application.getUserId(),
                    application.getApplyTime().toLocalDate(),
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
        int productId = DecodeId.decodeId(tableModel.getValueAt(selectedRow, 1).toString()); // 상품 ID가 있는 열의 인덱스를 확인하세요.
        String productName = tableModel.getValueAt(selectedRow, 2).toString(); // 상품 이름

        // 사용자에게 발주 수량을 입력받습니다.
        String input = JOptionPane.showInputDialog(this,
                "상품 이름: " + productName + "\n발주 수량을 입력하세요:",
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
