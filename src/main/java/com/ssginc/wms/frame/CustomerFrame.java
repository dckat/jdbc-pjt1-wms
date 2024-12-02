package com.ssginc.wms.frame;

import javax.swing.*;
import java.awt.*;

// 구매자 화면에서 공통부분을 클래스로 분리
public class CustomerFrame extends JFrame {
    public CustomerFrame() {
        // JFrame 설정
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

        // 각 메뉴 항목에 동작 추가
        editInfoItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "회원정보수정 선택됨"));
        deleteAccountItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "회원탈퇴 선택됨"));

        JLabel welcomeLabel = new JLabel("< " + "user1" + " > 님 환영합니다.     ", SwingConstants.RIGHT);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.setBackground(Color.GRAY);
        topPanel.add(dropdownButton, BorderLayout.EAST);
        dropdownButton.setBackground(Color.LIGHT_GRAY);
        add(topPanel, BorderLayout.NORTH);

        // Left Panel
        JPanel leftPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        JLabel llabel = new JLabel("");
        JButton invenUIButton = new JButton("재 고  현 황");
        JButton ordHistoryButton = new JButton("주 문  내 역");
        JButton incomeHistoryButton = new JButton("입고신청 내역");
        leftPanel.add(llabel);
        leftPanel.add(invenUIButton);
        leftPanel.add(ordHistoryButton);
        leftPanel.add(incomeHistoryButton);
        leftPanel.setPreferredSize(new Dimension(150, 600));
        add(leftPanel, BorderLayout.WEST);

        invenUIButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "재고현황 버튼 클릭됨"));
        ordHistoryButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "주문내역 버튼 클릭됨"));
        incomeHistoryButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "입고신청 내역 버튼 클릭됨"));
    }
}
