package com.example.Project1.Frame;

import com.example.Project1.DAO.DatabaseConnection;
import com.example.Project1.DAO.DatabaseConnection;
import com.example.Project1.Login.LoginUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class findId {

    Connection conn = DatabaseConnection.connect();
    JFrame frame = new JFrame();


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

        // 아이디 찾기 버튼 로직
        findIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String ssn = new String(ssnField.getText());
                String email = new String(emailField.getText());


                // SQL 쿼리 및 JDBC 연결
                String sql = "SELECT id FROM user WHERE name = ? AND ssn = ? AND email = ?";

                try {
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    // PreparedStatement로 파라미터 바인딩
                    stmt.setString(1, name);
                    stmt.setString(2, ssn);
                    stmt.setString(3, email);

                    // 쿼리 실행 및 결과 확인
                    // 쿼리 실행 및 결과 확인
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) { // 일치하는 데이터가 있는 경우
                        String findId = rs.getString("id"); // ID를 가져옴
                        JOptionPane.showMessageDialog(frame, "회원님의 ID는 \"" + findId + "\" 입니다.");
                        new LoginUI();
                        findIdFrame.dispose();
                    } else { // 일치하는 데이터가 없는 경우
                        JOptionPane.showMessageDialog(frame, "데이터를 다시 확인해주세요.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "데이터베이스 연결 오류: " + ex.getMessage());
                }
            }
        });

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
