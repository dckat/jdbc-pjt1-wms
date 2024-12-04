package com.ssginc.wms.frame;

import com.ssginc.wms.inoutManage.AdminIncomeUI;
import com.ssginc.wms.ord.OrdAdminUI;
import com.ssginc.wms.product.AdminProductUI;
import com.ssginc.wms.product.ProductDAO;
import com.ssginc.wms.incomeApply.AdminIncomeApplyUI;
import com.ssginc.wms.supply.ListSupplyUI;
import com.ssginc.wms.user.LoginFrameUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminFrame extends JFrame {
    private JTable productTable;
    private JTextField searchField;
    private JLabel welcomeLabel;
    private JComboBox<String> categoryComboBox;
    private DefaultTableModel tableModel;
    private ProductDAO productDAO;
    private String loggedInAdminId;
    Color color = new Color(0x000000);
    Color color2 = new Color(0xFCEA8B9B, true);
    Color color3 = new Color(0xD0C86B);

    public AdminFrame(String userId) {
        this.loggedInAdminId = userId;

        setTitle("재고 관리 시스템_Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());
        Font fontT = new Font("맑은 고딕", Font.BOLD, 16);


        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new

                Dimension(1200, 30));

        add(topPanel, BorderLayout.NORTH);

        welcomeLabel = new

                JLabel("< " + loggedInAdminId + " > 님 환영합니다.", SwingConstants.RIGHT);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        JButton dropdownButton = new JButton("≡");
        topPanel.add(dropdownButton, BorderLayout.EAST);
        dropdownButton.setBackground(Color.gray);
        topPanel.setBackground(color2);
        JLabel notice = new JLabel("   관리자 모드입니다.");
        topPanel.add(notice, BorderLayout.WEST);
        dropdownButton.setSize(30, 30);

        // JPopupMenu 생성
        JPopupMenu menu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("로그아웃");
        menu.add(logoutItem);
        dropdownButton.addActionListener(e -> menu.show(dropdownButton, 0, dropdownButton.getHeight()));

        // 각 메뉴 항목에 동작 추가
        logoutItem.addActionListener(e ->

        {
            dispose();
            new LoginFrameUI();
        });

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
        leftPanel.setPreferredSize(new

                Dimension(150, 600));

        add(leftPanel, BorderLayout.WEST);

        invenUIButton.addActionListener(e -> {
            this.dispose();
            new AdminProductUI(loggedInAdminId);
        });

        ioUIButton.addActionListener(e -> {
            this.dispose();
            new AdminIncomeUI(loggedInAdminId);
        });

        incomeButton.addActionListener(e -> {
            this.dispose();
            new AdminIncomeApplyUI(loggedInAdminId);
        });

        ordButton.addActionListener(e -> {
            this.dispose();
            new OrdAdminUI(loggedInAdminId);
        });

        purordButton.addActionListener(e -> {
            this.dispose();
            new ListSupplyUI(loggedInAdminId);
        });
    }
}
