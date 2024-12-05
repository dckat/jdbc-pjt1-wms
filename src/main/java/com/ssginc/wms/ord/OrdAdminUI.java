package com.ssginc.wms.ord;

import com.ssginc.wms.frame.AdminFrame;
import com.ssginc.wms.product.ProductDAO;
import com.ssginc.wms.product.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class OrdAdminUI extends AdminFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    Color color = new Color(0x615959);
    OrdDAO ordDao = new OrdDAO();
    ProductDAO productDao = new ProductDAO();

    public OrdAdminUI(String id) {
        // JFrame 설정
        super(id);
        setTitle("주문 관리");
        Font fontT  = new Font("맑은 고딕", Font.BOLD, 16);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton oneWeekButton = new JButton("최근 1주");
        JButton oneMonthButton = new JButton("최근 1개월");
        JButton threeMonthsButton = new JButton("최근 3개월");
        filterPanel.add(oneWeekButton);
        filterPanel.add(oneMonthButton);
        filterPanel.add(threeMonthsButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // 테이블 설정
        tableModel = new DefaultTableModel(new String[]{"주문코드", "상품코드", "상품이름", "주문단가", "주문수량",
                "총 가격", "주문일", "주문상태"}, 0);
        productTable = new JTable(tableModel);


        ArrayList<OrdProductVO> ordList = ordDao.getOrderList();
        addOrdElement(ordList);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel blabel = new JLabel("");
        JButton approveButton = new JButton("주문 승인");
        bottomPanel.add(blabel);
        bottomPanel.add(approveButton);
        add(bottomPanel, BorderLayout.SOUTH);

        oneWeekButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = ordDao.getAdminOrdListByPeriod("1week");
            addOrdElement(filteredList);
        });

        oneMonthButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = ordDao.getAdminOrdListByPeriod("1month");
            addOrdElement(filteredList);
        });

        threeMonthsButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = ordDao.getAdminOrdListByPeriod("3months");;
            addOrdElement(filteredList);
        });

        approveButton.addActionListener(e -> {
            int[] rows = productTable.getSelectedRows();
            TableModel data = productTable.getModel();

            // 이미 승인된 주문내역을 승인하는 경우
            if (!OrdService.checkOrdStatus(rows, data)) {
                JOptionPane.showMessageDialog(this, "이미 승인된 주문 내역이 존재합니다.");
                return;
            }

            int[] ordIds = OrdService.getOrdIds(rows, data);
            int[] ordAmounts = OrdService.getOrdAmounts(rows, data);
            int[] productIds = ProductService.getProductIds(rows, data);

            Map<Integer, Integer> ordMap = OrdService.getOrderInfo(productIds, ordAmounts);
            Map<Integer, Integer> proMap = productDao.getAmountById();

            if (!checkApprove(productIds, ordMap, proMap)) {
                JOptionPane.showMessageDialog(this, "재고량이 부족하여 승인할 수 없습니다.");
                return;
            }

            if (ordDao.updateOrdStatus(ordIds, ordAmounts) != null) {
                JOptionPane.showMessageDialog(this, "주문 승인이 완료되었습니다.");
            }
            else {
                JOptionPane.showMessageDialog(this, "승인 중 오류가 발생하였습니다.");
            }

            this.dispose();
            new OrdAdminUI(id);
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void addOrdElement(ArrayList<OrdProductVO> list) {
        for (OrdProductVO data: list) {
            Vector<Object> v = new Vector<>();
            v.add(OrdService.encodeOrdId(data.getOrderId()));
            v.add(ProductService.encodeProductId(data.getProductId()));
            v.add(data.getProductName());
            v.add(data.getOrderPrice());
            v.add(data.getOrderAmount());
            v.add(data.getTotalPrice());
            v.add(data.getOrderTime().toLocalDate());
            v.add(data.getOrderStatus());
            tableModel.addRow(v);
        }
    }

    public boolean checkApprove(int[] productIds, Map<Integer, Integer> ordMap, Map<Integer, Integer> proMap) {
        for (int i = 0; i < productIds.length; i++) {
            if (ordMap.get(productIds[i]) > proMap.get(productIds[i])) {
                return false;
            }
        }
        return true;
    }
}
