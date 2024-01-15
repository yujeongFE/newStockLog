package stocklogmanipulation;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginSignin {
    // phone number(tel) format
    static String id; // id static
    private static MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
            formatter.setPlaceholderCharacter('_'); // Optional: Use '_' as a placeholder
        } catch (java.text.ParseException exc) {
            System.err.println("Formatter is invalid: " + exc.getMessage());
        }
        return formatter;
    }

    public static void main(String[] args) {
        DBconnection dbConnector = new DBconnection(); // DB 연결 객체 생성

        JFrame loginFrame = new JFrame("로그인창");
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel l1 = new JLabel("ID"); // id
        l1.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JTextField text = new JTextField(15);
        text.setFont(new Font("맑은 고딕", Font.PLAIN, 25));


        JLabel l2 = new JLabel("passwd"); // ps
        l2.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JPasswordField value = new JPasswordField(15);
        value.setFont(new Font("맑은 고딕", Font.PLAIN, 25));


        // id
        ((AbstractDocument) text.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("[a-zA-Z]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });


        // ps
        ((AbstractDocument) value.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("[a-zA-Z]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JButton loginButton = new JButton("로그인");
        loginButton.setFont(new Font("맑은 고딕", Font.PLAIN, 25));


        JLabel notMemberLabel = new JLabel("아직 회원이 아니신가요?");
        notMemberLabel.setForeground(Color.GRAY);
        notMemberLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 17));


        JLabel signUpLabel = new JLabel("회원가입");
        signUpLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        signUpLabel.setForeground(Color.BLACK);
        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame signUpFrame = new JFrame("회원가입");
                JPanel signUpPanel = new JPanel(new GridBagLayout());



                JLabel l11 = new JLabel("ID");
                l11.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
                JTextField signUpID = new JTextField(15);
                signUpID.setFont(new Font("맑은 고딕", Font.PLAIN, 25));



                JLabel l12 = new JLabel("passwd");
                l12.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
                JPasswordField signUpPasswd = new JPasswordField(15);
                signUpPasswd.setFont(new Font("맑은 고딕", Font.PLAIN, 25));


                JLabel l13 = new JLabel("passwd 확인");
                l13.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
                JPasswordField signUpPasswdConfirm = new JPasswordField(15);
                signUpPasswdConfirm.setFont(new Font("맑은 고딕", Font.PLAIN, 25));

                JLabel l14 = new JLabel("이름");
                l14.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
                JTextField signUpName = new JTextField(15);
                signUpName.setFont(new Font("맑은 고딕", Font.PLAIN, 25));

                JLabel l15 = new JLabel("전화번호");
                l15.setFont(new Font("맑은 고딕맑은 고딕", Font.PLAIN, 25));
                JFormattedTextField signUpTel = new JFormattedTextField(createFormatter("###-####-####"));
                signUpTel.setFont(new Font("Arial", Font.PLAIN, 25));

                // id
                ((AbstractDocument) signUpID.getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                            throws BadLocationException {
                        if (text.matches("[a-zA-Z]*")) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });

                // ps
                ((AbstractDocument) signUpPasswd.getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                            throws BadLocationException {
                        if (text.matches("[a-zA-Z]*")) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });

                // ps 확인
                ((AbstractDocument) signUpPasswdConfirm.getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                            throws BadLocationException {
                        if (text.matches("[a-zA-Z]*")) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });

                // 이름
                ((AbstractDocument) signUpName.getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                            throws BadLocationException {
                        if (text.matches("[a-zA-Z]*")) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });

                // 전화번호
                ((AbstractDocument) signUpTel.getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                            throws BadLocationException {
                        if (text.matches("[0-9]*")) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });

                // 회원가입
                JButton signUpButton = new JButton("회원가입");
                signUpButton.setFont(new Font("Arial", Font.PLAIN, 25));

                signUpButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String id = signUpID.getText();
                        String passwd = new String(signUpPasswd.getPassword());
                        String confirmPasswd = new String(signUpPasswdConfirm.getPassword());
                        String name = signUpName.getText();
                        String tel = signUpTel.getText().replace("-", ""); // 하이픈 제거
                        tel = tel.replace("010", ""); // 010 제거

                        if (id.isEmpty() || passwd.isEmpty() || confirmPasswd.isEmpty() || name.isEmpty() || tel.isEmpty()) {
                            JOptionPane.showMessageDialog(signUpFrame, "모든 입력란을 채워주세요.");
                        } else if (!passwd.equals(confirmPasswd)) {
                            JOptionPane.showMessageDialog(signUpFrame, "입력한 passwd 값이 다릅니다.");
                        } else {
                            try {
                                Statement statement = dbConnector.getConnection().createStatement();
                                String query = "INSERT INTO person(U_ID, U_PS, U_NAME, U_TEL) VALUES('" + id + "', '" + passwd + "', '" + name + "', '" + tel + "')";
                                // String query = "INSERT INTO person(U_ID, U_PS, U_NAME, U_TEL) VALUES('id', 'passwd', 'name', 'tel')";

                                statement.executeUpdate(query);
                                statement.close();

                                String data = id + "님 회원가입에 성공하셨습니다.";
                                JOptionPane.showMessageDialog(signUpFrame, data);
                                signUpFrame.dispose();
                                loginFrame.setVisible(true);
                            } catch (SQLException ex) {
                                // 해야 할 것 - Id가 겹칠 경우 에러 찰;
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(signUpFrame, "회원가입에 실패하였습니다.");
                            }
                        }
                    }
                });

                GridBagConstraints signUpGbc = new GridBagConstraints();
                signUpGbc.insets = new Insets(5, 5, 5, 5);

                signUpGbc.gridx = 0;
                signUpGbc.gridy = 0;
                signUpPanel.add(l11, signUpGbc);

                signUpGbc.gridx = 1;
                signUpGbc.gridy = 0;
                signUpPanel.add(signUpID, signUpGbc);

                signUpGbc.gridx = 0;
                signUpGbc.gridy = 1;
                signUpPanel.add(l12, signUpGbc);

                signUpGbc.gridx = 1;
                signUpGbc.gridy = 1;
                signUpPanel.add(signUpPasswd, signUpGbc);

                signUpGbc.gridx = 0;
                signUpGbc.gridy = 2;
                signUpPanel.add(l13, signUpGbc);

                signUpGbc.gridx = 1;
                signUpGbc.gridy = 2;
                signUpPanel.add(signUpPasswdConfirm, signUpGbc);

                signUpGbc.gridx = 0;
                signUpGbc.gridy = 3;
                signUpPanel.add(l14, signUpGbc);

                signUpGbc.gridx = 1;
                signUpGbc.gridy = 3;
                signUpPanel.add(signUpName, signUpGbc);

                signUpGbc.gridx = 0;
                signUpGbc.gridy = 4;
                signUpPanel.add(l15, signUpGbc);

                signUpGbc.gridx = 1;
                signUpGbc.gridy = 4;
                signUpPanel.add(signUpTel, signUpGbc);

                signUpGbc.gridx = 1;
                signUpGbc.gridy = 5;
                signUpPanel.add(signUpButton, signUpGbc);

                signUpFrame.add(signUpPanel);
                signUpFrame.setSize(700, 700);
                signUpFrame.setLocationRelativeTo(loginFrame);
                signUpFrame.setVisible(true);
                signUpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(l1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(text, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(l2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(value, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(notMemberLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(signUpLabel, gbc);

        loginFrame.add(panel);

        loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = text.getText();
                String passwd = new String(value.getPassword());

                if (id.isEmpty() || passwd.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "로그인에 실패했습니다. ID와 비밀번호를 모두 입력하세요.");
                } else {
                    try {
                        Statement statement = dbConnector.getConnection().createStatement();
                        String query = "SELECT * FROM person WHERE U_ID='" + id + "' AND U_PS='" + passwd + "'";
                        ResultSet resultSet = statement.executeQuery(query);

                        if (resultSet.next()) {
                            String userId = id; // 이 부분에서 아이디 저장
                            JOptionPane.showMessageDialog(loginFrame, id + "님 로그인에 성공하셨습니다.");

                            loginFrame.dispose(); // 로그인 창 닫기
                            SwingUtilities.invokeLater(() -> {
                                new Home(userId); // 홈 화면 열기
                            });
                        } else {
                            JOptionPane.showMessageDialog(loginFrame, "로그인에 실패했습니다. 회원이 아니시면 회원가입을 진행해주세요.");
                        }

                        resultSet.close();
                        statement.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(loginFrame, "데이터베이스 연결 실패");
                    }
                }
            }
        });

        loginFrame.setVisible(true);
    }
}
