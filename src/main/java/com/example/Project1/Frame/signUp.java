package com.example.Project1.Frame;

import com.example.Project1.DAO.DatabaseConnection;
import com.example.Project1.DAO.DatabaseConnection;
import com.example.Project1.Login.LoginUI;

import javax.swing.*;
import java.sql.*;

public class signUp {
    JFrame frame = new JFrame();
    Connection conn = DatabaseConnection.connect();

    public static void run() {
        DatabaseConnection.connect();

    }


    public signUp() {

        // 메인 프레임 생성
        JFrame signUpFrame = new JFrame("회원가입");
        signUpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signUpFrame.setSize(520, 600);
        signUpFrame.setLayout(null);

        // UI 요소 추가
        JLabel idLabel = new JLabel("I                 D :");
        idLabel.setBounds(50, 40, 100, 30);
        JTextField idField = new JTextField();
        idField.setBounds(150, 50, 180, 30);

        JButton checkIdButton = new JButton("ID 중복 확인");
        checkIdButton.setBounds(350, 50, 120, 30);

        JLabel pwLabel = new JLabel("P               W :");
        pwLabel.setBounds(50, 100, 100, 30);
        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(150, 100, 180, 30);

        JLabel confirmPwLabel = new JLabel("P   W   확   인:");
        confirmPwLabel.setBounds(50, 150, 100, 30);
        JPasswordField confirmPwField = new JPasswordField();
        confirmPwField.setBounds(150, 150, 180, 30);

        JButton checkPwButton = new JButton("PW 중복 확인");
        checkPwButton.setBounds(350, 150, 120, 30);

        JLabel nameLabel = new JLabel("이               름 :");
        nameLabel.setBounds(50, 200, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 200, 180, 30);

        JLabel ssnLabel = new JLabel("주민등록번호:");
        ssnLabel.setBounds(50, 250, 100, 30);
        JTextField ssnField = new JTextField();
        ssnField.setBounds(150, 250, 180, 30);

        JLabel emailLabel = new JLabel("이      메      일:");
        emailLabel.setBounds(50, 300, 100, 30);
        JTextField emailField = new JTextField();
        emailField.setBounds(150, 300, 180, 30);

        JLabel phoneLabel = new JLabel("전  화  번  호 :");
        phoneLabel.setBounds(50, 350, 100, 30);
        JTextField phoneField = new JTextField();
        phoneField.setBounds(150, 350, 180, 30);

        JLabel addressLabel = new JLabel("집      주     소:");
        addressLabel.setBounds(50, 400, 100, 30);
        JTextField addressField = new JTextField();
        addressField.setBounds(150, 400, 180, 30);

        JButton signupButton = new JButton("회원가입");
        signupButton.setBounds(150, 450, 100, 40);

        JButton exitButton = new JButton("종료");
        exitButton.setBounds(270, 450, 100, 40);

        // 중복 확인 로직
        checkIdButton.addActionListener(e -> {
            String id = idField.getText();
            run();
            String sql = "SELECT COUNT(*) FROM user WHERE id = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                // PreparedStatement로 파라미터 바인딩
                stmt.setString(1, id);

                // 쿼리 실행 및 결과 확인
                ResultSet rs = stmt.executeQuery();
                if (id.length() < 5) {
                    JOptionPane.showMessageDialog(signUpFrame, "ID는 5글자 이상이어야 합니다.");
                } else if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(signUpFrame, "중복되는 아이디입니다.");
                } else {
                    JOptionPane.showMessageDialog(signUpFrame, "사용 가능한 아이디입니다.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(signUpFrame, "데이터베이스 연결 오류: " + ex.getMessage());
            }
        });

        checkPwButton.addActionListener(e -> { //비밀번호 6자리 이상 구현 (+ 영문 숫자 혼합 구별 가능한가요...?)
            String pw = pwField.getText();
            String confirmPw = confirmPwField.getText();

            if (pw.length() <= 5) {
                JOptionPane.showMessageDialog(signUpFrame, "비밀번호는 6자리 이상 입력해야 합니다..");
            } else if (pw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(signUpFrame, "<html><span style='color:green;'>비밀번호가 일치합니다.</span></html>");

            } else if (!pw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(signUpFrame, "<html><span style='color:red;'>비밀번호가 불일치합니다.<br>비밀번호를 다시 확인해주세요.</span></html>");
            }
        });

        // 회원가입 버튼 로직
        signupButton.addActionListener(e -> {
                    String id = idField.getText();
                    String password = new String(pwField.getPassword());
                    String confirmPassword = new String(confirmPwField.getPassword());
                    String name = new String(nameField.getText());
                    String ssn = ssnField.getText();
                    String email = emailField.getText();
                    String phone = phoneField.getText();
                    String address = addressField.getText();

                    if (id.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || ssn.isEmpty() ||
                            email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                        JOptionPane.showMessageDialog(signUpFrame, "모든 필드를 입력해주세요.");
                    } else if (!password.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(signUpFrame, "비밀번호가 일치하지 않습니다.");
                    } else {
                        run(); // db연동
                        boolean isInserted = DatabaseConnection.insertUser(id, password, name, ssn, email, phone, address);
                        if (isInserted) {
                            JOptionPane.showMessageDialog(signUpFrame, "회원가입 성공!");
                            new LoginUI();
                            signUpFrame.setVisible(false);
                        } else {
                            JOptionPane.showMessageDialog(signUpFrame, "회원가입 실패. 중복확인을 눌러 중복을 확인해주세요.");
                        }
                    }


                }

        );

        // 종료 버튼 로직
        exitButton.addActionListener(e -> System.exit(0));

        // 프레임에 추가
        signUpFrame.add(idLabel);
        signUpFrame.add(idField);
        signUpFrame.add(checkIdButton);
        signUpFrame.add(pwLabel);
        signUpFrame.add(pwField);
        signUpFrame.add(confirmPwLabel);
        signUpFrame.add(confirmPwField);
        signUpFrame.add(checkPwButton);
        signUpFrame.add(nameLabel);
        signUpFrame.add(nameField);
        signUpFrame.add(ssnLabel);
        signUpFrame.add(ssnField);
        signUpFrame.add(emailLabel);
        signUpFrame.add(emailField);
        signUpFrame.add(phoneLabel);
        signUpFrame.add(phoneField);
        signUpFrame.add(addressLabel);
        signUpFrame.add(addressField);
        signUpFrame.add(signupButton);
        signUpFrame.add(exitButton);

        // 화면 중앙 배치
        signUpFrame.setLocationRelativeTo(null);
        signUpFrame.setVisible(true);
    }
}
