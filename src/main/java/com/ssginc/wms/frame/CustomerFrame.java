package com.ssginc.wms.frame;

import com.ssginc.wms.incomeApply.UserIncomeApplyHistoryUI;
import com.ssginc.wms.ord.OrdCustomerUI;
import com.ssginc.wms.product.ProductDAO;
import com.ssginc.wms.product.UserProductUI;
import com.ssginc.wms.user.LoginFrameUI;
import com.ssginc.wms.user.UserDAO;
import com.ssginc.wms.user.UserVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Random;

// 구매자 화면에서 공통부분을 클래스로 분리
public class CustomerFrame extends JFrame {
    private JLabel welcomeLabel;
    private CustomerFrame parentFrame;
    private JTextField idField, nameField, emailField, phoneField, addressField;
    private JPasswordField pwField, confirmPwField;
    private DefaultTableModel tableModel;
    private String loggedInUserId;
    private ProductDAO productDAO;
    private UserDAO userDAO;
    private JFrame editInfoFrame;
    Color color1 = new Color(0x5A5253);
    Color color2 = new Color(0x86878C);
    Color color3 = new Color(0x3E3B3B);
    Font fontT = new Font("맑은 고딕", Font.BOLD, 16);
    Font fontC = new Font("맑은 고딕", Font.BOLD, 12);

    public CustomerFrame(String userId) {
        this.loggedInUserId = userId;
        this.productDAO = new ProductDAO();
        this.userDAO = new UserDAO();

        // JFrame 설정
        setTitle("재고 관리 시스템_Customer");
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
        JMenuItem logout = new JMenuItem("로그아웃");
        JMenuItem editInfo = new JMenuItem("회원정보수정");
        JMenuItem deleteAccount = new JMenuItem("회원탈퇴");
        menu.add(logout);
        menu.add(editInfo);
        menu.add(deleteAccount);
        dropdownButton.addActionListener(e -> menu.show(dropdownButton, 0, dropdownButton.getHeight()));

        // 각 메뉴 항목에 동작 추가
        logout.addActionListener(e -> {
            dispose();
            new LoginFrameUI();
        });
        editInfo.addActionListener(e -> {
            // 회원정보 수정 프레임 호출
            UpdateUserFrame(loggedInUserId, this);
        });
        deleteAccount.addActionListener(e -> handleDeleteAccount());

        welcomeLabel = new JLabel("< " + loggedInUserId + " > 님 환영합니다.", SwingConstants.RIGHT);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

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

        invenUIButton.addActionListener(e -> {
            this.dispose();
            new UserProductUI(loggedInUserId);
        });
        ordHistoryButton.addActionListener(e -> {
            this.dispose();
            new OrdCustomerUI(loggedInUserId);
        });
        incomeHistoryButton.addActionListener(e -> {
            this.dispose();
            new UserIncomeApplyHistoryUI(loggedInUserId);
        });
    }

    public void UpdateUserFrame(String loggedInUserId, CustomerFrame parentFrame) {
        this.loggedInUserId = loggedInUserId; // 로그인된 사용자 ID 저장
        this.parentFrame = parentFrame; // 부모 프레임 저장

        editInfoFrame = new JFrame("회원정보 수정");
        editInfoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        editInfoFrame.setSize(520, 550);
        editInfoFrame.setLayout(null);

        // UI 요소 추가
        JLabel idLabel = new JLabel("I                D :");
        idLabel.setBounds(50, 50, 100, 30);
        idField = new JTextField();
        idField.setBounds(150, 50, 180, 30);

        JButton checkIdButton = new JButton("ID 중복 확인");
        checkIdButton.setBounds(350, 50, 120, 30);

        JLabel pwLabel = new JLabel("P               W :");
        pwLabel.setBounds(50, 100, 100, 30);
        pwField = new JPasswordField();
        pwField.setBounds(150, 100, 180, 30);

        JLabel confirmPwLabel = new JLabel("P   W   확   인:");
        confirmPwLabel.setBounds(50, 150, 100, 30);
        confirmPwField = new JPasswordField();
        confirmPwField.setBounds(150, 150, 180, 30);

        JButton checkPwButton = new JButton("PW 확인");
        checkPwButton.setBounds(350, 150, 120, 30);

        JLabel nameLabel = new JLabel("이               름 :");
        nameLabel.setBounds(50, 200, 100, 30);
        nameField = new JTextField();
        nameField.setBounds(150, 200, 180, 30);

        JLabel emailLabel = new JLabel("이      메      일:");
        emailLabel.setBounds(50, 250, 100, 30);
        emailField = new JTextField();
        emailField.setBounds(150, 250, 180, 30);

        JLabel phoneLabel = new JLabel("전  화  번  호 :");
        phoneLabel.setBounds(50, 300, 100, 30);
        phoneField = new JTextField();
        phoneField.setBounds(150, 300, 180, 30);

        JLabel addressLabel = new JLabel("집      주     소:");
        addressLabel.setBounds(50, 350, 100, 30);
        addressField = new JTextField();
        addressField.setBounds(150, 350, 180, 30);

        JButton editUserInfoButton = new JButton("정보 수정");
        editUserInfoButton.setBounds(90, 430, 130, 40);

        JButton exitButton = new JButton("창 닫기");
        exitButton.setBounds(270, 430, 130, 40);
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);

        // 프레임에 추가
        editInfoFrame.add(idLabel);
        editInfoFrame.add(idField);
        editInfoFrame.add(checkIdButton);
        editInfoFrame.add(pwLabel);
        editInfoFrame.add(pwField);
        editInfoFrame.add(confirmPwLabel);
        editInfoFrame.add(confirmPwField);
        editInfoFrame.add(checkPwButton);
        editInfoFrame.add(nameLabel);
        editInfoFrame.add(nameField);
        editInfoFrame.add(emailLabel);
        editInfoFrame.add(emailField);
        editInfoFrame.add(phoneLabel);
        editInfoFrame.add(phoneField);
        editInfoFrame.add(addressLabel);
        editInfoFrame.add(addressField);
        editInfoFrame.add(editUserInfoButton);
        editInfoFrame.add(exitButton);

        editInfoFrame.setLocationRelativeTo(null);
        editInfoFrame.setVisible(true);

        // 저장 버튼 클릭 이벤트 처리
        editUserInfoButton.addActionListener(e -> updateUser());

        // 종료 버튼 로직
        exitButton.addActionListener(e -> editInfoFrame.dispose());

        // ID 중복 확인 로직
        checkIdButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.length() < 5) {
                JOptionPane.showMessageDialog(editInfoFrame, "ID는 5글자 이상이어야 합니다.");
            } else if (userDAO.isIdDuplicate(id)) {
                JOptionPane.showMessageDialog(editInfoFrame, "중복되는 아이디입니다.");
            } else {
                JOptionPane.showMessageDialog(editInfoFrame, "사용 가능한 아이디입니다.");
            }
        });

        // 비밀번호 확인 로직
        checkPwButton.addActionListener(e -> {
            String pw = new String(pwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());
            if (pw.length() < 6) {
                JOptionPane.showMessageDialog(editInfoFrame, "비밀번호는 6자리 이상이어야 합니다.");
            } else if (!pw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(editInfoFrame, "비밀번호가 일치하지 않습니다.");
            } else {
                JOptionPane.showMessageDialog(editInfoFrame, "비밀번호가 일치합니다.");
            }
        });

        // 종료 버튼 로직
        exitButton.addActionListener(e -> {
            editInfoFrame.dispose();
        });

    }

    private void updateUser() {
        // 입력 필드 값 가져오기
        String newId = idField.getText(); // 새 ID
        String password = new String(pwField.getPassword());
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        // 필드 유효성 검사
        if (newId.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.");
            return;
        }

        // ID 변경 시 중복 체크
        if (!newId.equals(loggedInUserId) && userDAO.isIdDuplicate(newId)) {
            JOptionPane.showMessageDialog(this, "중복된 ID입니다. 다른 ID를 입력해주세요.");
            return;
        }

        // VO 객체 생성
        UserVO updatedUser = new UserVO(newId, password, name, null, email, phone, address, null);

        // DAO 호출
        boolean success = userDAO.updateUserInfo(updatedUser, loggedInUserId);
        if (success) {
            JOptionPane.showMessageDialog(this, "회원정보가 수정되었습니다.");

            // ID 변경 시 loggedInUserId 업데이트 및 부모 프레임 welcomeLabel 갱신
            loggedInUserId = newId; // 새 ID로 갱신
            parentFrame.setWelcomeLabelText(newId); // 부모 프레임 welcomeLabel 업데이트
            editInfoFrame.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "수정에 실패했습니다. 다시 시도해주세요.");
        }
    }

    public void setWelcomeLabelText(String newId) {
        welcomeLabel.setText("< " + newId + " > 님 환영합니다.     ");
    }

    private void handleDeleteAccount() { // 회원탈퇴 프로세스
        int confirm = JOptionPane.showConfirmDialog(this,
                "정말로 회원탈퇴를 진행하시겠습니까? 탈퇴 시 계정 복구가 불가능합니다.",
                "회원탈퇴 확인",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // "_deleted" ID 생성
            String newDeletedId = userDAO.generateUniqueDeletedId(loggedInUserId);

            // DAO 호출로 ID 업데이트
            boolean success = userDAO.markUserAsDeleted(loggedInUserId, newDeletedId);
            if (success) {
                JOptionPane.showMessageDialog(this, "회원탈퇴가 완료되었습니다. 감사합니다.");
                dispose(); // 현재 화면 종료
                new LoginFrameUI(); // 로그인 화면으로 이동
            } else {
                JOptionPane.showMessageDialog(this, "회원탈퇴에 실패했습니다. 다시 시도해주세요.");
            }
        }
    }

    private String generateUniqueDeletedId(String userId) {
        Random random = new Random();
        String newId;
        do {
            newId = userId + "_deleted" + random.nextInt(10000);
        } while (userDAO.checkUserIdExists(newId)); // 중복 체크
        return newId;
    }


}
