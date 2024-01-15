package stocklogmanipulation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JToolBar;

public class StockInfo {
    static String userId; // 사용자 id 저장 변수 추가
    static String stockName; // 주식종목명 저장 변수 추가
    public StockInfo(String userId, String stockName){
        this.userId = userId;
        this.stockName = stockName;

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("주식 매매 관리 시스템");

            JDesktopPane desktopPane = new JDesktopPane();

            // 툴바 생성 및 설정
            JToolBar toolBar = createToolBar();
            Color customColor = new Color(85, 76, 223); // #554cdf의 RGB 값 // 툴바 색상
            toolBar.setBackground(customColor);
            frame.add(toolBar, BorderLayout.NORTH);

            // 하단 바 생성
            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(Color.GRAY);
            bottomPanel.setPreferredSize(new Dimension(800, 50));
            frame.add(bottomPanel, BorderLayout.SOUTH);

            // 전체 화면 size 가져오기
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            // width, height 설정
            int width = screenSize.width / 3;
            int height = (screenSize.height-145) / 2;
            int fullheight = screenSize.height-145;

            // innerframe1 생성, 추가
            JInternalFrame internalFrame1 = new JInternalFrame("증시 지수", true, true, true, true);
            JPanel internalPanel1 = createPanelWithBorder("증시 지수");
            internalFrame1.getContentPane().add(internalPanel1);
            internalFrame1.setSize(width, height);
            internalFrame1.setLocation(desktopPane.getAllFrames().length * width, desktopPane.getAllFrames().length * height);
            internalFrame1.setVisible(true);
            desktopPane.add(internalFrame1);

            // innerframe1 생성, 추가
            JInternalFrame internalFrame1_1 = new JInternalFrame("증시 지수", true, true, true, true);
            JPanel internalPanel1_1 = createPanelWithBorder("증시 지수");
            internalFrame1_1.getContentPane().add(internalPanel1_1);
            internalFrame1_1.setSize(width, height);
            internalFrame1_1.setLocation(0, 0);
            internalFrame1_1.setVisible(true);
            desktopPane.add(internalFrame1_1);

            // innerframe2 생성, 추가
            JInternalFrame internalFrame2 = new JInternalFrame("종목 차트", true, true, true, true);
            JPanel internalPanel2 = createPanelWithBorder("종목 차트");
            internalFrame2.getContentPane().add(internalPanel2);
            internalFrame2.setSize(width, height / 2);
            internalFrame2.setLocation(1 * width, 0);
            internalFrame2.setVisible(true);
            desktopPane.add(internalFrame2);

            // innerframe2_1 생성, 추가
            JInternalFrame internalFrame2_1 = new JInternalFrame("종목 차트2", true, true, true, true);
            JPanel internalPanel2_1 = createPanelWithBorder("종목 차트2");
            internalFrame2_1.getContentPane().add(internalPanel2_1);
            internalFrame2_1.setSize(width, height / 2);
            internalFrame2_1.setLocation(1 * width, height / 2);
            internalFrame2_1.setVisible(true);
            desktopPane.add(internalFrame2_1);

            // innerframe3 생성, 추가
            JInternalFrame internalFrame3 = new JInternalFrame("관심 주식", true, true, true, true);
            JPanel internalPanel3 = createPanelWithBorder("관심 주식");
            internalFrame3.getContentPane().add(internalPanel3);
            internalFrame3.setSize(width, height);
            internalFrame3.setLocation(0, height);
            internalFrame3.setVisible(true);
            desktopPane.add(internalFrame3);

            // innerframe4 생성, 추가
            JInternalFrame internalFrame4 = new JInternalFrame("최근 주가 차트", true, true, true, true);
            JPanel internalPanel4 = createPanelWithBorder("최근 주가 차트");
            internalFrame4.getContentPane().add(internalPanel4);
            internalFrame4.setSize(width, height);
            internalFrame4.setLocation(1 * width, height);
            internalFrame4.setVisible(true);
            desktopPane.add(internalFrame4);

            // innerframe5 생성, 추가
            JInternalFrame internalFrame5 = new JInternalFrame("관련 뉴스", true, true, true, true);
            JPanel internalPanel5 = createPanelWithBorder("관련 뉴스");
            internalFrame5.getContentPane().add(internalPanel5);
            internalFrame5.setSize(width, height/2);
            internalFrame5.setLocation(2 * width, 0);
            internalFrame5.setVisible(true);
            desktopPane.add(internalFrame5);

            // innerframe5_1 생성, 추가
            JInternalFrame internalFrame5_1 = new JInternalFrame("관련 뉴스", true, true, true, true);
            JPanel internalPanel5_1 = createPanelWithBorder("관련 뉴스");
            internalFrame5_1.getContentPane().add(internalPanel5_1);
            internalFrame5_1.setSize(width, height / 2);
            internalFrame5_1.setLocation(2 * width, height / 2);
            internalFrame5_1.setVisible(true);
            desktopPane.add(internalFrame5_1);

            // innerframe6 생성, 추가
            JInternalFrame internalFrame6 = new JInternalFrame("주식 매매 일지", true, true, true, true);
            JPanel internalPanel6 = createPanelWithBorder("주식 매매 일지");
            internalFrame6.getContentPane().add(internalPanel6);
            internalFrame6.setSize(width, height);
            internalFrame6.setLocation(2 * width, height);
            internalFrame6.setVisible(true);
            desktopPane.add(internalFrame6);

            // 패널에 기능 추가
            Panel1Action.addFunctionality(internalPanel1); // 패널 1에 기능 추가
            Panel1_1Action.addFunctionality(internalPanel1_1);
            SI_Panel2Action.addFunctionality(internalPanel2,stockName); // 패널 2에 기능 추가
            SI_Panel2_1Action.addFunctionality(internalPanel2_1,stockName); // 패널 2-1에 기능 추가
            SI_Panel3Action.addFunctionality(internalPanel3, userId); // 관심 주식 표시
            SI_Panel4Action.addFunctionality(internalPanel4, stockName); // 보유 주식 표시
            SI_Panel5Action.addFunctionality(internalPanel5, stockName); // 패널 5에 기능 추가
            SI_Panel5_1Action.addFunctionality(internalPanel5_1); // 패널5-1에 기능 추가
            SI_Panel6Action.addFunctionality(internalPanel6, userId, stockName); // 패널 6에 기능 추가
            Panel6Action.executeApiRequestAndDisplayInPanel(bottomPanel); // 하단 바에 기능 추가

            frame.add(desktopPane);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JPanel createPanelWithBorder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        // 테두리 스타일
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        return panel;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar("주식 매매 관리 시스템 툴바");

        // 툴바에 버튼 추가
        JButton button1 = new JButton("로그아웃");
        JButton button2 = new JButton("뒤로가기");

        // 각 버튼에 액션 추가
        JFrame closeframe = new JFrame("로그아웃");
        JLabel timerLabel = new JLabel("");


        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(button2, BorderLayout.CENTER);
        panel.add(timerLabel, BorderLayout.SOUTH);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeframe.dispose();
                closeframe.getContentPane().removeAll();
                closeframe.getContentPane().add(panel, BorderLayout.CENTER);


                Timer timer = new Timer(1000, new ActionListener() {
                    int count = 3;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (count > 0) {
                            timerLabel.setText("로그아웃 되기 " + count + "초 전");
                            count--;
                        } else {
                            ((Timer) e.getSource()).stop();
                            System.exit(0);
                        }
                    }
                });

                timer.start();

                closeframe.setSize(300, 150);

                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                closeframe.setLocation(dim.width / 2 - closeframe.getSize().width / 2, dim.height / 2 - closeframe.getSize().height / 2);

                // 프레임 내에 panel을 BorderLayout.CENTER에 배치
                closeframe.getContentPane().setLayout(new BorderLayout());
                closeframe.getContentPane().add(panel, BorderLayout.CENTER);

                // timerLabel 가운데 정렬 및 글자 크기 설정
                timerLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
                timerLabel.setHorizontalAlignment(JLabel.CENTER);

                closeframe.setVisible(true);
            }
        });


        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 버튼2 클릭 시 실행할 동작
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                currentFrame.dispose();  // 현재의 StockInfo_new 창 닫기
                // 새로운 Home_new 창 열기
            }
        });

        // 툴바에 버튼 추가
        toolBar.add(button1);
        toolBar.addSeparator(); // 구분선 추가
        toolBar.add(button2);

        return toolBar;
    }

    public static void main(String[] args) {
        DBconnection dbConnector = new DBconnection();
        SwingUtilities.invokeLater(() -> {
            StockInfo home = new StockInfo(userId, stockName);
        });

    }
}
