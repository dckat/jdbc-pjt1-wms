package com.example.dao;

import com.example.login.Login;


import javax.swing.*;

public class findId {
    public findId() {
        JFrame findIdFrame = new JFrame("아이디 찾기");
        findIdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        findIdFrame.setSize(500, 390);
        findIdFrame.setLayout(null);

        JLabel nameLabel = new JLabel("이                 름 :");
        nameLabel.setBounds(100, 50, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(200, 50, 180, 30);

        JLabel ssnLabel = new JLabel("주민등록번호:");
        ssnLabel.setBounds(100, 100, 100, 30);
        JTextField ssnField = new JTextField();
        ssnField.setBounds(200, 100, 180, 30);

        JLabel emailLabel = new JLabel("이      메      일:");
        emailLabel.setBounds(100, 150, 100, 30);
        JTextField emailField = new JTextField();
        emailField.setBounds(200, 150, 180, 30);

        JButton findIdButton = new JButton("아이디 찾기");
        findIdButton.setBounds(130, 230, 100, 40);

        JButton exitButton = new JButton("종료");
        exitButton.setBounds(270, 230, 100, 40);

        // 종료 버튼 로직
        findIdButton.addActionListener(e -> {new Login(); findIdFrame.setVisible(false);});
        exitButton.addActionListener(e -> System.exit(0));

        findIdFrame.add(nameLabel);
        findIdFrame.add(nameField);
        findIdFrame.add(ssnLabel);
        findIdFrame.add(ssnField);
        findIdFrame.add(emailLabel);
        findIdFrame.add(emailField);
        findIdFrame.add(findIdButton);
        findIdFrame.add(exitButton);

        // 화면 중앙 배치
        findIdFrame.setLocationRelativeTo(null);
        findIdFrame.setVisible(true);
    }
}
