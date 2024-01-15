/*
package stocklogmanipulation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Home {
    static String userId; // 사용자 id 저장 변수 추가

    public Home(String userId) {
        this.userId = userId;

        JFrame frame = new JFrame("주식 매매 관리 시스템");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel topLeftPanel = createPanelWithBorder("1"); // 코스피 지수
        JPanel topLeftPanel2 = createPanelWithBorder("1-1"); // 코스닥 지수
        JPanel topRightPanel = createPanelWithBorder("2"); // 매도주식
        JPanel bottomLeftPanel = createPanelWithBorder("3"); // 관심주식
        JPanel bottomRightPanel = createPanelWithBorder("4"); // 보유주식
        JPanel rightPanel = createPanelWithBorder("5");

        JPanel bottomPanel = new JPanel(); // 하단바
        bottomPanel.setBackground(Color.GRAY); // 배경색 회색
        bottomPanel.setPreferredSize(new Dimension(frame.getWidth(), 50)); // 높이 50px

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // 패널에 기능 추가
        Panel1Action.addFunctionality(topLeftPanel); // 패널 1에 기능 추가
        Panel1_1Action.addFunctionality(topLeftPanel2);
        Panel2Action.addFunctionality(topRightPanel, userId); // 패널 2에 기능 추가
        Panel3Action.addFunctionality(bottomLeftPanel, userId); // 관심 주식 표시
        Panel4Action.addFunctionality(bottomRightPanel, userId); // 보유 주식 표시
        Panel5Action.addFunctionality(rightPanel, userId); // 패널 5에 기능 추가

        JPanel panel11_1 = new JPanel();
        panel11_1.setLayout(new GridLayout(1, 2));
        panel11_1.add(topLeftPanel);
        panel11_1.add(topLeftPanel2);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel11_1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(topRightPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(bottomLeftPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(bottomRightPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(rightPanel, gbc);

        frame.add(mainPanel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Panel6Action 객체 생성
        Panel6Action panel6Action = new Panel6Action();

        // executeApiRequestAndDisplayInPanel 메서드 호출
        panel6Action.executeApiRequestAndDisplayInPanel(bottomPanel);
    }

    private JPanel createPanelWithBorder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        // 테두리 스타일 지정
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        return panel;
    }

    public static void main(String[] args) {
        DBconnection dbConnector = new DBconnection();


        SwingUtilities.invokeLater(() -> {
            // Home 객체 생성
            Home home = new Home(userId);
        });
    }
}
*/
