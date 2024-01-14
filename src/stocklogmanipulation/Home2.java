package stocklogmanipulation;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

// 패널 5

// 패널 5-1
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;

import javax.swing.JFrame;
import javax.swing.JLabel;

// 패널 1에 대한 동작을 처리하는 클래스

// 패널 2에 대한 동작을 처리하는 클래스
// H2_PanelAction2.java 에 있습니다.


//class PanelAction2_1 { // 매도주식
// H2_PanelAction2_1.java 에 있습니다.


// 패널 5에 대한 동작을 처리하는 클래스
// H2_PanelAction5.java 에 있습니다.



// 패널 5-1에 대한 동작을 처리하는 클래스
// H2_PanelAction5_1.java 에 있습니다.


// 하단 바에 대한 동작을 처리하는 클래스
class PanelAction7 { // 매도주식
    public static void addFunctionality(JPanel panel) {
        // 하단바에 추가할 기능 구현
    }
}

public class Home2 {
    static String userId; // 사용자 id 저장 변수 추가
    static String stockName; // 주식종목명 저장 변수 추가

    public Home2(String userId, String stockName) {
        this.userId = userId;
        this.stockName = stockName;

        JFrame frame = new JFrame("주식 매매 관리 시스템");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel topLeftPanel = createPanelWithBorder("1"); // 종목지수
        JPanel topLeftPanel2 = createPanelWithBorder("1-1"); // 코스닥 지수
        JPanel topRightPanel1 = createPanelWithBorder("2"); // 종목 차트 (분봉)
        JPanel topRightPanel2 = createPanelWithBorder("2-1"); // 종목 차트 (일봉)
        JPanel bottomLeftPanel = createPanelWithBorder("3"); // 관심주식
        JPanel bottomRightPanel = createPanelWithBorder("4"); // 보유주
        JPanel rightPanel1 = createPanelWithBorder("5"); // 투자자별 (기관, 외국인..)'
        JPanel rightPanel2 = createPanelWithBorder("5-1"); // 뉴스
        JPanel rightBottomPanel = createPanelWithBorder("6"); // 매도, 매수일지 (종목 하나에 관한)

        JPanel panel22_1 = new JPanel();
        panel22_1.setLayout(new GridLayout(2, 1));
        panel22_1.add(topRightPanel1);
        panel22_1.add(topRightPanel2);

        JPanel panel55_1 = new JPanel();
        panel55_1.setLayout(new GridLayout(2, 1));
        panel55_1.add(rightPanel1);
        panel55_1.add(rightPanel2);

        JPanel bottomPanel = new JPanel(); // 하단바
        bottomPanel.setBackground(Color.GRAY); // 배경색 회색
        bottomPanel.setPreferredSize(new Dimension(frame.getWidth(), 50)); // 높이 50px

        frame.add(bottomPanel, BorderLayout.SOUTH);


        // 패널에 기능 추가
        SI_Panel1Action.addFunctionality(topLeftPanel); // 패널 1에 기능 추가
        // H2_PanelAction2.addFunctionality(topRightPanel1,stockName); // 패널 2에 기능 추가
        SI_Panel1Action.addFunctionality(topLeftPanel); // 패널 1에 기능 추가
        PanelAction1_1.addFunctionality(topLeftPanel2); // 패널 1에 기능 추가
        // H2_PanelAction2_1.addFunctionality(topRightPanel2,stockName); // 패널 2-1에 기능 추가
        PanelAction3.addFunctionality(bottomLeftPanel, userId); // 관심 주식 표시
        PanelAction4.addFunctionality(bottomRightPanel, userId); // 보유 주식 표시
        SI_Panel5Action.addFunctionality(rightPanel1, stockName); // 패널 5에 기능 추가
        PanelAction6.addFunctionality(rightBottomPanel, userId); // 패널 6에 기능 추가
        PanelAction7.addFunctionality(bottomPanel); // 하단 바에 기능 추가

        // 패널 5-1에 기능 추가
        SI_Panel5_1Action.addFunctionality(rightPanel2);

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
        mainPanel.add(panel22_1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(bottomLeftPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(bottomRightPanel, gbc);

        // 패널 5와 패널 5-1 설정
        gbc.gridx = 2;
        gbc.gridy = 0;
        mainPanel.add(panel55_1, gbc);

        // 패널 6 설정
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 1; // 1 행만 차지
        gbc.weighty = 0.5; // 세로 방향으로 50% 차지
        mainPanel.add(rightBottomPanel, gbc);
        frame.add(mainPanel);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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
        SwingUtilities.invokeLater(() -> {
            try {
                Home2 home2 = new Home2(userId, stockName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
