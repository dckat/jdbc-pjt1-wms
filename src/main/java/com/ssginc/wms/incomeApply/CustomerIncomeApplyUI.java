package com.ssginc.wms.incomeApply;

import com.ssginc.wms.frame.CustomerFrame;
import com.ssginc.wms.product.ProductService;
import com.ssginc.wms.util.DecodeId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerIncomeApplyUI extends CustomerFrame {
    private JTable incomeTable;
    private DefaultTableModel tableModel;
    private final ProductIncomeService productIncomeService;
    private IncomeApplyDAO dao;
    private JButton yearButton, monthButton, weekButton;
    private String loggedId;

    public CustomerIncomeApplyUI(String id) {
        super(id);
        loggedId = id;
        this.productIncomeService = new ProductIncomeService();
        this.dao = new IncomeApplyDAO();

        setTitle("입고 신청 내역");

        // JScrollPane 추가
        JScrollPane scrollPane = new JScrollPane(incomeTable);
        add(scrollPane, BorderLayout.CENTER);

        // 상단 패널 (검색 필터 및 버튼)
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        yearButton = new JButton("최근 1년");
        monthButton = new JButton("최근 1개월");
        weekButton = new JButton("최근 1주일"); // 드롭다운 (컬럼 선택)

        filterPanel.add(weekButton);
        filterPanel.add(monthButton);
        filterPanel.add(yearButton);

        centerPanel.add(filterPanel, BorderLayout.NORTH);


        // 테이블 초기화 (체크박스를 추가)
        tableModel = new DefaultTableModel(new String[]{
                "신청코드", "상품코드", "분류이름", "상품이름", "신청일", "신청상태"
        }, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return super.getColumnClass(columnIndex);
            }
        };
        incomeTable = new JTable(tableModel);

        JScrollPane incomeScrollPane = new JScrollPane(incomeTable);
        centerPanel.add(incomeScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 날짜 필터 버튼 추가
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 삭제 버튼
        JButton deleteButton = new JButton("입고신청 취소");
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // 삭제 버튼 이벤트
        deleteButton.addActionListener(e -> deleteSelectedRows());

        // 날짜 버튼 이벤트
        yearButton.addActionListener(e -> filterByDate(365)); // 1년
        monthButton.addActionListener(e -> filterByDate(30));  // 1달
        weekButton.addActionListener(e -> filterByDate(7));    // 1주

        // 초기 데이터 로드
        loadIncomeApplyData();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 검색된 데이터 로드
    private void loadIncomeApplyDataWithSearch(String column, String searchText) {
        List<ProductIncomeApplyVO> incomeApplies = dao.searchIncomeApplies(loggedId, column, searchText);

        // 테이블 데이터 모델 업데이트
        tableModel.setRowCount(0);  // 기존 데이터 지우기
        for (ProductIncomeApplyVO apply : incomeApplies) {
            Object[] rowData = new Object[6];
            rowData[0] = IncomeApplyService.encodeApplyId(apply.getApplyId());
            rowData[1] = ProductService.encodeProductId(apply.getProductId());
            rowData[2] = apply.getCategoryName();
            rowData[3] = apply.getProductName();
            rowData[4] = apply.getApplyTime().toLocalDate();
            rowData[5] = productIncomeService.getApplyStatusAsString(apply);  // Service를 사용해 변환

            tableModel.addRow(rowData);
        }
    }

    private void filterByDate(int days) {
        IncomeApplyDAO dao = new IncomeApplyDAO();
        List<com.ssginc.wms.incomeApply.ProductIncomeApplyVO> filteredData =
                dao.getIncomeApplyWithinPeriod(loggedId, days);

        // 테이블 데이터 모델 업데이트
        tableModel.setRowCount(0);  // 기존 데이터 지우기
        for (com.ssginc.wms.incomeApply.ProductIncomeApplyVO apply : filteredData) {
            Object[] rowData = new Object[6];
            rowData[0] = IncomeApplyService.encodeApplyId(apply.getApplyId());
            rowData[1] = ProductService.encodeProductId(apply.getProductId());
            rowData[2] = apply.getCategoryName();
            rowData[3] = apply.getProductName();
            rowData[4] = apply.getApplyTime().toLocalDate();
            rowData[5] = productIncomeService.getApplyStatusAsString(apply);  // Process -> String 변환

            tableModel.addRow(rowData);
        }
    }

    private void loadIncomeApplyData() {
        List<ProductIncomeApplyVO> incomeApplies = dao.getAllIncomeApplyById(loggedId);

        // 테이블 데이터 모델 업데이트
        tableModel.setRowCount(0);  // 기존 데이터 지우기
        for (ProductIncomeApplyVO apply : incomeApplies) {
            Object[] rowData = new Object[6];
            rowData[0] = IncomeApplyService.encodeApplyId(apply.getApplyId());
            rowData[1] = ProductService.encodeProductId(apply.getProductId());
            rowData[2] = apply.getCategoryName();
            rowData[3] = apply.getProductName();
            rowData[4] = apply.getApplyTime().toLocalDate();
            rowData[5] = productIncomeService.getApplyStatusAsString(apply);  // Service를 사용해 변환

            tableModel.addRow(rowData);
        }
    }

    private void deleteSelectedRows() {
        int[] selectedRows = incomeTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "취소할 항목을 선택하세요.");
            return;
        }

        List<Integer> applyIdsToDelete = new ArrayList<>();
        for (int row : selectedRows) {
            int applyId = DecodeId.decodeId(tableModel.getValueAt(row, 0).toString()); // 신청 ID 가져오기
            String status = (String) tableModel.getValueAt(row, 5);  // 상태 가져오기

            if ("completed".equals(status)) {
                // "completed" 상태인 경우 삭제 불가 메시지 표시
                JOptionPane.showMessageDialog(this, "이미 입고가 완료되었습니다. 취소할 수 없습니다.");
                return;
            }

            applyIdsToDelete.add(applyId);
        }

        if (applyIdsToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "입고 대기 상태인 항목을 선택해주세요.");
            return;
        }

        // DAO 호출하여 삭제 수행
        IncomeApplyDAO dao = new IncomeApplyDAO();
        boolean success = dao.deletePendingIncomeApplies(applyIdsToDelete);

        if (success) {
            JOptionPane.showMessageDialog(this, "선택된 항목이 취소되었습니다.");
            loadIncomeApplyData(); // 데이터 갱신
        } else {
            JOptionPane.showMessageDialog(this, "취소 중 오류가 발생했습니다.");
        }
    }
}