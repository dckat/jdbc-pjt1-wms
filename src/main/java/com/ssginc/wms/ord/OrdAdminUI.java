package com.ssginc.wms.ord;

import com.ssginc.wms.product.ProductDAO;
import com.ssginc.wms.product.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class OrdAdminUI extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    Color color = new Color(0x615959);
    OrdDAO ordDao = new OrdDAO();
    ProductDAO productDao = new ProductDAO();

    public OrdAdminUI() {
        // JFrame 설정
        setTitle("주문 관리");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLayout(new BorderLayout()); // 기본 레이아웃 설정
        Font fontT  = new Font("맑은 고딕", Font.BOLD, 16);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(1200, 30));
        JButton dropdownButton = new JButton("≡");
        dropdownButton.setSize(30, 30);

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
        // invenUIButton.setForeground(Color.white);
        invenUIButton.setFont(fontT);
        JButton ioUIButton = new JButton("입출고 현황");
        ioUIButton.setBackground(color);
        // ioUIButton.setForeground(Color.white);
        ioUIButton.setFont(fontT);
        JButton incomeButton = new JButton("입고신청 관리");
        incomeButton.setBackground(color);
        // incomeButton.setForeground(Color.white);
        incomeButton.setFont(fontT);
        JButton ordButton = new JButton("주 문  관 리");
        ordButton.setBackground(color);
        // ordButton.setForeground(Color.white);
        ordButton.setFont(fontT);
        JButton purordButton = new JButton("발 주  관 리");
        purordButton.setBackground(color);
        // purordButton.setForeground(Color.white);
        purordButton.setFont(fontT);
        //leftPanel.add(imageLabel);
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
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton oneWeekButton = new JButton("최근 1주");
        JButton oneMonthButton = new JButton("최근 1개월");
        JButton threeMonthsButton = new JButton("최근 3개월");
        filterPanel.add(oneWeekButton);
        filterPanel.add(oneMonthButton);
        filterPanel.add(threeMonthsButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // 테이블 설정
        tableModel = new DefaultTableModel(new String[]{"주문코드", "상품코드", "상품이름", "주문가격", "주문수량",
                "총 가격", "주문일", "주문 상태"}, 0);
        productTable = new JTable(tableModel);
        productTable.setFont(fontT);

        ArrayList<OrdProductVO> ordList = ordDao.listOrder();
        addOrdElement(ordList);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel blabel = new JLabel("");
        JButton approveButton = new JButton("주문 승인");
        bottomPanel.add(blabel);
        bottomPanel.add(approveButton);
        add(bottomPanel, BorderLayout.SOUTH);

        oneWeekButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = ordDao.getAdminListByPeriod("1week");
            addOrdElement(filteredList);
        });

        oneMonthButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = ordDao.getAdminListByPeriod("1month");
            addOrdElement(filteredList);
        });

        threeMonthsButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            ArrayList<OrdProductVO> filteredList = ordDao.getAdminListByPeriod("3months");;
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
            ordDao.updateOrderStatus(ordIds, ordAmounts);

            this.dispose();
            new OrdAdminUI();
        });

        setVisible(true);
    }

    public void addOrdElement(ArrayList<OrdProductVO> list) {
        for (OrdProductVO data: list) {
            Vector<Object> v = new Vector<>();
            v.add(data.getOrderId());
            v.add(data.getProductId());
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
