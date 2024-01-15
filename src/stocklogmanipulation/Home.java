package stocklogmanipulation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class Home {
    static String userId; // 사용자 id 저장 변수 추가
    private JFrame frame;
    public Home(String userId){
        this.userId = userId;

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
            JInternalFrame internalFrame2 = new JInternalFrame("전체 수익률보기", true, true, true, true);
            JPanel internalPanel2 = createPanelWithBorder("수익률보기");
            internalFrame2.getContentPane().add(internalPanel2);
            internalFrame2.setSize(width, height);
            internalFrame2.setLocation(1 * width, 0);
            internalFrame2.setVisible(true);
            desktopPane.add(internalFrame2);

            // innerframe3 생성, 추가
            JInternalFrame internalFrame3 = new JInternalFrame("관심 주식", true, true, true, true);
            JPanel internalPanel3 = createPanelWithBorder("관심 주식");
            internalFrame3.getContentPane().add(internalPanel3);
            internalFrame3.setSize(width, height);
            internalFrame3.setLocation(0, 1 * height);
            internalFrame3.setVisible(true);
            desktopPane.add(internalFrame3);

            // innerframe4 생성, 추가
            JInternalFrame internalFrame4 = new JInternalFrame("보유 주식", true, true, true, true);
            JPanel internalPanel4 = createPanelWithBorder("보유 주식");
            internalFrame4.getContentPane().add(internalPanel4);
            internalFrame4.setSize(width, height);
            internalFrame4.setLocation(1 * width, 1 * height);
            internalFrame4.setVisible(true);
            desktopPane.add(internalFrame4);

            // innerframe5 생성, 추가
            JInternalFrame internalFrame5 = new JInternalFrame("주식 매매 일지", true, true, true, true);
            JPanel internalPanel5 = createPanelWithBorder("주식 매매 일지");
            internalFrame5.getContentPane().add(internalPanel5);
            internalFrame5.setSize(width, fullheight);
            internalFrame5.setLocation(2 * width, 0);
            internalFrame5.setVisible(true);
            desktopPane.add(internalFrame5);

            // 패널에 기능 추가
            Panel1Action.addFunctionality(internalPanel1); // 패널 1에 기능 추가
            Panel1_1Action.addFunctionality(internalPanel1_1); // 패널 1_1
            Panel2Action.addFunctionality(internalPanel2, userId); // 패널 2에 기능 추가
            Panel3Action.addFunctionality(internalPanel3, userId); // 관심 주식 표시
            Panel4Action.addFunctionality(internalPanel4, userId); // 보유 주식 표시
            Panel5Action.addFunctionality(internalPanel5, userId); // 패널 5에 기능 추가
            Panel6Action.executeApiRequestAndDisplayInPanel(bottomPanel); // 하단 바에 기능 추가

            frame.add(desktopPane);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    public static void main(String[] args) {
        DBconnection dbConnector = new DBconnection();
        SwingUtilities.invokeLater(() -> {
            Home home = new Home(userId);
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
    private void refreshHome() {
        JOptionPane optionPane = new JOptionPane("새로고침됩니다", JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog("새로고침");
        dialog.setModal(false); // Set it non-modal to allow the timer to close it
        dialog.setVisible(true);

        // Schedule a task to dispose of the dialog after 3 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dispose();

                // Dispose of the existing frame
                frame.dispose();

                // Reinitialize the home frame
                new Home(userId);
            }
        }, 3000);
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar("주식 매매 관리 시스템 툴바");
        //툴바에 버튼 추가
        JButton button2 = new JButton("로그아웃");

        JFrame closeframe = new JFrame("로그아웃");
        JLabel timerLabel = new JLabel("");


        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(button2, BorderLayout.CENTER);
        panel.add(timerLabel, BorderLayout.SOUTH);

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeframe.dispose();
                closeframe.getContentPane().removeAll();
                closeframe.getContentPane().add(panel, BorderLayout.CENTER);


                javax.swing.Timer timer = new javax.swing.Timer(1000, new ActionListener() {
                    int count = 3;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (count > 0) {
                            timerLabel.setText("로그아웃 되기 " + count + "초 전");
                            count--;
                        } else {
                            ((javax.swing.Timer) e.getSource()).stop();
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

        // 툴바에 버튼 추가
        toolBar.add(button2);

        return toolBar;
    }
    private JDesktopPane createDesktopPane() {
        JDesktopPane desktopPane = new JDesktopPane();

        // innerframe1, innerframe1_1, innerframe2, innerframe3, innerframe4, innerframe5 생성 및 추가 로직은 그대로 유지

        return desktopPane;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.GRAY);
        bottomPanel.setPreferredSize(new Dimension(800, 50));

        // 패널에 기능 추가
        Panel6Action.executeApiRequestAndDisplayInPanel(bottomPanel);

        return bottomPanel;
    }

}
