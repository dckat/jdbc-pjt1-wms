package com.example.Project1.Login;

import com.example.Project1.DAO.*;
import com.example.Project1.Frame.Inventory_admin;
import com.example.Project1.Frame.findId;
import com.example.Project1.Frame.findPw;
import com.example.Project1.Frame.signUp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginUI {
    private JLabel label;
    private JTextField textField;
    private JPasswordField passwordField;
    private JButton button;
    Connection conn = DatabaseConnection.connect();

    public LoginUI() {
        LoginUI();
    }

    public void LoginUI() {
        // 메인 프레임 생성
        JFrame frame = new JFrame("Login Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLayout(null); // 절대 레이아웃 설정

        // ID Label 및 TextField
        JLabel idLabel = new JLabel("I D :");
        idLabel.setBounds(50, 50, 80, 30);
        JTextField idField = new JTextField();
        idField.setBounds(120, 50, 200, 30);

        // PW Label 및 PasswordField
        JLabel pwLabel = new JLabel("P W :");
        pwLabel.setBounds(50, 100, 80, 30);
        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(120, 100, 200, 30);

        // LOGIN 버튼
        JButton loginButton = new JButton("LOGIN");
        loginButton.setBounds(330, 50, 80, 80);

        // 하단 버튼들
        JButton findIdButton = new JButton("아이디 찾기");
        findIdButton.setBounds(50, 180, 100, 30);

        JButton findPwButton = new JButton("비밀번호 찾기");
        findPwButton.setBounds(160, 180, 120, 30);

        JButton signupButton = new JButton("회원가입");
        signupButton.setBounds(290, 180, 100, 30);

        JButton exitButton = new JButton("종료");
        exitButton.setBounds(160, 220, 120, 30);

        // 버튼 액션 추가
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(pwField.getPassword());


                // SQL 쿼리 및 JDBC 연결
                String sql = "SELECT COUNT(*) FROM user WHERE id = ? AND pw = ?";

                try {
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    // PreparedStatement로 파라미터 바인딩
                    stmt.setString(1, id);
                    stmt.setString(2, password);

                    // 쿼리 실행 및 결과 확인
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 1) {
                        JOptionPane.showMessageDialog(frame, "로그인 성공!");
                        new Inventory_admin();
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "아이디 또는 비밀번호를 확인하세요.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "데이터베이스 연결 오류: " + ex.getMessage());
                }
            }
        });

        findIdButton.addActionListener(e -> {
            new findId();
            frame.setVisible(false);
        });
        findPwButton.addActionListener(e -> {
            new findPw();
            frame.setVisible(false);
        });
        signupButton.addActionListener(e -> {
            new signUp();
            frame.setVisible(false);
        });
        exitButton.addActionListener(e -> System.exit(0));

        // 프레임에 컴포넌트 추가
        frame.add(idLabel);
        frame.add(idField);
        frame.add(pwLabel);
        frame.add(pwField);
        frame.add(loginButton);
        frame.add(findIdButton);
        frame.add(findPwButton);
        frame.add(signupButton);
        frame.add(exitButton);

        // JFrame 화면 중앙에 배치
        frame.setLocationRelativeTo(null);

        // 화면 표시
        frame.setVisible(true);
    }
}