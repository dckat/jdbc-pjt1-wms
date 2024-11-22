package com.example.dao;

import com.example.login.Login;


import javax.swing.*;

public class findPw {
    public findPw() {
        JFrame findPwFrame = new JFrame("회원가입");
        findPwFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        findPwFrame.setSize(500, 500);
        findPwFrame.setLayout(null);

        JLabel idLabel = new JLabel("I             D :");
        idLabel.setBounds(100, 50, 100, 30);
        JTextField idField = new JTextField();
        idField.setBounds(200, 50, 180, 30);

        JLabel nameLabel = new JLabel("이                 름 :");
        nameLabel.setBounds(100, 100, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(200, 100, 180, 30);

        JLabel ssnLabel = new JLabel("주민등록번호:");
        ssnLabel.setBounds(100, 150, 100, 30);
        JTextField ssnField = new JTextField();
        ssnField.setBounds(200, 150, 180, 30);

        JLabel emailLabel = new JLabel("이      메      일:");
        emailLabel.setBounds(100, 200, 100, 30);
        JTextField emailField = new JTextField();
        emailField.setBounds(200, 200, 180, 30);

        JButton findPwButton = new JButton("비밀번호 찾기");
        findPwButton.setBounds(100, 280, 130, 40);

        JButton exitButton = new JButton("종료");
        exitButton.setBounds(270, 280, 130, 40);

        // 종료 버튼 로직
        findPwButton.addActionListener(e -> {new Login(); findPwFrame.setVisible(false);});
        exitButton.addActionListener(e -> System.exit(0));

        findPwFrame.add(idLabel);
        findPwFrame.add(idField);
        findPwFrame.add(nameLabel);
        findPwFrame.add(nameField);
        findPwFrame.add(ssnLabel);
        findPwFrame.add(ssnField);
        findPwFrame.add(emailLabel);
        findPwFrame.add(emailField);
        findPwFrame.add(findPwButton);
        findPwFrame.add(exitButton);

        // 화면 중앙 배치
        findPwFrame.setLocationRelativeTo(null);
        findPwFrame.setVisible(true);
    }
}
