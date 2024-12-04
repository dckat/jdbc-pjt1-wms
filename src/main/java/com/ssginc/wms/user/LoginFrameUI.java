package com.ssginc.wms.user;

import com.ssginc.wms.product.AdminProductUI;
import com.ssginc.wms.product.UserProductUI;
import com.ssginc.wms.user.UserVO;
import com.ssginc.wms.user.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrameUI {

    public LoginFrameUI() {
        initUI();
    }

    public void initUI() {
        // 메인 프레임 생성
        JFrame frame = new JFrame("재고 관리 시스템 _ 로그인");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLayout(null); // 절대 레이아웃 설정

        // 폰트 설정
        Font font1 = new Font("맑은 고딕", Font.PLAIN, 16);

        // ID Label 및 TextField
        JLabel idLabel = new JLabel("I         D :");
        idLabel.setBounds(50, 50, 80, 30);
        JTextField idField = new JTextField();
        idField.setBounds(130, 50, 200, 30);

        // PW Label 및 PasswordField
        JLabel pwLabel = new JLabel("P       W :");
        pwLabel.setBounds(50, 100, 80, 30);
        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(130, 100, 200, 30);

        // LOGIN 버튼
        JButton loginButton = new JButton("LOGIN");
        loginButton.setBackground(Color.blue);
        loginButton.setForeground(Color.white);
        loginButton.setBounds(350, 50, 80, 80);

        // 하단 버튼들
        JButton findIdButton = new JButton("아이디 찾기");
        findIdButton.setBounds(40, 180, 120, 30);

        JButton findPwButton = new JButton("비밀번호 찾기");
        findPwButton.setBounds(185, 180, 120, 30);

        JButton signupButton = new JButton("회원가입");
        signupButton.setBounds(330, 180, 120, 30);

        JButton exitButton = new JButton("종료");
        exitButton.setBounds(200, 250, 80, 30);

        // 로그인 버튼 로직
        loginButton.addActionListener((ActionEvent e) -> {
            String id = idField.getText();
            String password = new String(pwField.getPassword());

            UserDAO userDAO = new UserDAO();
            UserVO user = userDAO.authenticateUser(id, password);

            if (user != null) {
                String grade = user.getUserGrade();
                if ("admin".equals(grade)) {
                    new AdminProductUI(user.getUserId());
                } else if ("customer".equals(grade)) {
                    new UserProductUI(user.getUserId());
                } else {
                    JOptionPane.showMessageDialog(frame, "알 수 없는 사용자 등급입니다.");
                }
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "아이디 또는 비밀번호를 확인하세요.");
            }
        });

        // 하단 버튼 로직
        findIdButton.addActionListener(e -> FindIdFrame());
        findPwButton.addActionListener(e -> FindPwFrame());
        signupButton.addActionListener(e -> SignUpFrame());
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
        frame.setVisible(true);
    }

    public void FindIdFrame() {
        JFrame findIdFrame = new JFrame("아이디 찾기");
        findIdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        findIdFrame.setSize(500, 390);
        findIdFrame.setLayout(null);

        JLabel nameLabel = new JLabel("이               름 :");
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
        findIdButton.setBounds(90, 230, 130, 40);

        JButton exitButton = new JButton("처음 화면으로");
        exitButton.setBounds(270, 230, 130, 40);
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);

        // 아이디 찾기 버튼 로직
        findIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 입력값으로 UserVO 객체 생성
                UserVO user = new UserVO();
                user.setUserName(nameField.getText());
                user.setUserSsn(ssnField.getText());
                user.setUserEmail(emailField.getText());

                UserDAO userDAO = new UserDAO(); // DAO 인스턴스 생성
                String userId = userDAO.findUserId(user);

                if (userId != null) {
                    JOptionPane.showMessageDialog(findIdFrame, "회원님의 ID는 \"" + userId + "\" 입니다.");
                    new LoginFrameUI();
                    findIdFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(findIdFrame, "데이터를 다시 확인해주세요.");
                }
            }
        });

        exitButton.addActionListener(e -> {
            new LoginFrameUI();
            findIdFrame.dispose();
        });

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

    public void FindPwFrame() {
        JFrame findPwFrame = new JFrame("비밀번호 찾기");
        findPwFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        findPwFrame.setSize(500, 440);
        findPwFrame.setLayout(null);

        JLabel idLabel = new JLabel("I                  D :");
        idLabel.setBounds(100, 50, 100, 30);
        JTextField idField = new JTextField();
        idField.setBounds(200, 50, 180, 30);

        JLabel nameLabel = new JLabel("이               름 :");
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
        findPwButton.setBounds(80, 280, 130, 40);

        JButton exitButton = new JButton("처음 화면으로");
        exitButton.setBounds(270, 280, 130, 40);
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);

        // 비밀번호 찾기 버튼 로직
        findPwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 입력값으로 UserVO 객체 생성
                UserVO user = new UserVO();
                user.setUserId(idField.getText());
                user.setUserName(nameField.getText());
                user.setUserSsn(ssnField.getText());
                user.setUserEmail(emailField.getText());

                UserDAO userDAO = new UserDAO(); // DAO 인스턴스 생성
                String userPassword = userDAO.findUserPassword(user);

                if (userPassword != null) {
                    JOptionPane.showMessageDialog(findPwFrame, "회원님의 PW는 \"" + userPassword + "\" 입니다.");
                    new LoginFrameUI();
                    findPwFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(findPwFrame, "데이터를 다시 확인해주세요.");
                }
            }
        });

        // 종료 버튼 로직
        exitButton.addActionListener(e -> {
            new LoginFrameUI();
            findPwFrame.dispose();
        });

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

    public void SignUpFrame() {
        JFrame signUpFrame = new JFrame("회원가입");
        signUpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signUpFrame.setSize(520, 600);
        signUpFrame.setLayout(null);

        // UI 요소 추가
        JLabel idLabel = new JLabel("I                D :");
        idLabel.setBounds(50, 50, 100, 30);
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

        JButton checkPwButton = new JButton("PW 확인");
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
        signupButton.setBounds(90, 480, 130, 40);

        JButton exitButton = new JButton("처음 화면으로");
        exitButton.setBounds(270, 480, 130, 40);
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);

        UserDAO userDAO = new UserDAO(); // DAO 인스턴스 생성

        // ID 중복 확인 로직
        checkIdButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.length() < 5) {
                JOptionPane.showMessageDialog(signUpFrame, "ID는 5글자 이상이어야 합니다.");
            } else if (userDAO.isIdDuplicate(id)) {
                JOptionPane.showMessageDialog(signUpFrame, "중복되는 아이디입니다.");
            } else {
                JOptionPane.showMessageDialog(signUpFrame, "사용 가능한 아이디입니다.");
            }
        });

        // 비밀번호 확인 로직
        checkPwButton.addActionListener(e -> {
            String pw = new String(pwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());
            if (pw.length() < 6) {
                JOptionPane.showMessageDialog(signUpFrame, "비밀번호는 6자리 이상이어야 합니다.");
            } else if (!pw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(signUpFrame, "비밀번호가 일치하지 않습니다.");
            } else {
                JOptionPane.showMessageDialog(signUpFrame, "비밀번호가 일치합니다.");
            }
        });

        // 회원가입
        signupButton.addActionListener(e -> {
            UserVO user = new UserVO();
            user.setUserId(idField.getText());
            user.setUserPw(new String(pwField.getPassword()));
            user.setUserName(nameField.getText());
            user.setUserSsn(ssnField.getText());
            user.setUserEmail(emailField.getText());
            user.setUserPhone(phoneField.getText());
            user.setUserAddress(addressField.getText());

            if (user.getUserId().isEmpty() || user.getUserPw().isEmpty() || user.getUserName().isEmpty() ||
                    user.getUserSsn().isEmpty() || user.getUserEmail().isEmpty() || user.getUserPhone().isEmpty() ||
                    user.getUserAddress().isEmpty()) {
                JOptionPane.showMessageDialog(signUpFrame, "모든 필드를 입력해주세요.");
            } else if (userDAO.insertUser(user)) {
                JOptionPane.showMessageDialog(signUpFrame, "회원가입 성공!");
                new LoginFrameUI();
                signUpFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(signUpFrame, "회원가입 실패.");
            }
        });

        // 종료 버튼 로직
        exitButton.addActionListener(e -> {
            new LoginFrameUI();
            signUpFrame.dispose();
        });

        // UI 구성 요소 추가
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
