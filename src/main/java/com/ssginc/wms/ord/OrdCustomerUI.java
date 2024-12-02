package com.ssginc.wms.ord;

import com.ssginc.wms.frame.CustomerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class OrdCustomerUI extends CustomerFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;

    public OrdCustomerUI(String userId) {
        super();
        setTitle("주문 내역");

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton oneWeekButton = new JButton("최근 1주");
        JButton oneMonthButton = new JButton("최근 1개월");
        JButton threeMonthsButton = new JButton("최근 3개월");
        filterPanel.add(oneWeekButton);
        filterPanel.add(oneMonthButton);
        filterPanel.add(threeMonthsButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // 테이블 설정
        tableModel = new DefaultTableModel(new String[]{"주문 코드", "상품 코드", "상품 이름",
                "주문 단가", "주문 수량", "총 가격", "주문일", "주문 상태"}, 0);
        productTable = new JTable(tableModel);

        OrdDAO dao = new OrdDAO();
        ArrayList<OrdProductVO> ordList = dao.listByUserId(userId);
        addOrdElement(ordList);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel blabel = new JLabel("");
        JButton cancelButton = new JButton("주문 취소");
        bottomPanel.add(blabel);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);

        oneWeekButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = dao.listByPeriod(userId, "1week");
            addOrdElement(filteredList);
        });

        oneMonthButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = dao.listByPeriod(userId, "1month");
            addOrdElement(filteredList);
        });

        threeMonthsButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = dao.listByPeriod(userId, "3months");
            addOrdElement(filteredList);
        });

        cancelButton.addActionListener(e -> {
            int[] rows = productTable.getSelectedRows();
            TableModel data = productTable.getModel();

            if (!OrdService.checkStatus(rows, data)) {
                JOptionPane.showMessageDialog(this, "승인된 주문내역은 취소가 불가능합니다.");
                return;
            }

            int[] ordIds = OrdService.getOrdIds(rows, data);
            int row = dao.deleteOrder(ordIds);

            if (row > 0) {
                JOptionPane.showMessageDialog(this, "주문 내역이 삭제되었습니다.");
            }

            this.dispose();
            new OrdCustomerUI(userId);
        });

        setVisible(true);
    }

    public void addOrdElement(ArrayList<OrdProductVO> list) {
        for (OrdProductVO data: list) {
            Vector<Object> v = new Vector<>();
            String ordId = OrdService.encodeOrderId(data.getOrderId());
            v.add(ordId);
            v.add(data.getProductId());
            v.add(data.getProductName());
            v.add(data.getOrderPrice());
            v.add(data.getOrderAmount());
            v.add(data.getTotalPrice());
            v.add(data.getOrderTime());
            v.add(data.getOrderStatus());
            tableModel.addRow(v);
        }
    }
}
