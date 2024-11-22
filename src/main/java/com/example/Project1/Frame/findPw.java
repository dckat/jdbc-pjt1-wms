package com.example.Project1.Frame;

import com.example.Project1.DAO.DatabaseConnection;
import com.example.Project1.Login.LoginUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class findPw {
    Connection conn = DatabaseConnection.connect();
    JFrame frame = new JFrame();

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

        // 비밀번호 찾기 버튼 로직
        findPwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String name = nameField.getText();
                String ssn = ssnField.getText();
                String email = emailField.getText();


                // SQL 쿼리 및 JDBC 연결
                String sql = "SELECT pw FROM user WHERE id = ? AND name = ? AND ssn = ? AND email = ?";

                try {
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    // PreparedStatement로 파라미터 바인딩
                    stmt.setString(1, id);
                    stmt.setString(2, name);
                    stmt.setString(3, ssn);
                    stmt.setString(4, email);

                    // 쿼리 실행 및 결과 확인
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) { // 일치하는 데이터가 있는 경우
                        String findPw = rs.getString("pw"); // PW를 가져옴
                        JOptionPane.showMessageDialog(frame, "회원님의 PW는 " + findPw + " 입니다.");
                        new LoginUI();
                        findPwFrame.dispose();
                    } else { // 일치하는 데이터가 없는 경우
                        JOptionPane.showMessageDialog(frame, "데이터를 다시 확인해주세요.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "데이터베이스 연결 오류: " + ex.getMessage());
                }
            }
        });

        // 종료 버튼 로직
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

