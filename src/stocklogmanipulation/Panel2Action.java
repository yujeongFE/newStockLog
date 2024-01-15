package stocklogmanipulation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// 패널 2에 대한 동작을 처리하는 클래스
public class Panel2Action { // 매도주식
    static Object[] row = new Object[11];
    // 데이터를 담을 테이블 모델 생성
    static String[] columnNames = {"증권사", "종목명", "매도일", "매도단가", "매도수량", "수익률", "총 수익", "매매비용", "매매시작일", "평균매수단가", "메모"};
    static DefaultTableModel tableModel = new DefaultTableModel(null, columnNames);

    public static void addFunctionality(JPanel panel, String userId) {
        // 데이터베이스 연결
        DBconnection dbConnector = new DBconnection();
        Connection connection = dbConnector.getConnection();

        String id = userId;
        // SQL 쿼리 실행
        String query = "SELECT sl.COMPANY, s.NAME, sl.S_DATE, sl.S_PRICE, sl.QTY, sl.RRATIO, sl.ALLB, sl.TAX, sl.B_DATE, sl.E_PRICE, sl.MEMO FROM stock s, sellstock sl WHERE s.CODE = sl.CODE AND U_ID = '" + id + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // 결과셋의 데이터를 테이블 모델에 추가
            String stockName = null; // 변수를 루프 바깥에 선언하고 초기화
            while (resultSet.next()) {
                row[0] = resultSet.getObject(1);
                row[1] = resultSet.getObject(2);
                row[2] = resultSet.getObject(3);
                row[3] = resultSet.getObject(4);
                row[4] = resultSet.getObject(5);
                row[5] = resultSet.getObject(6);
                row[6] = resultSet.getObject(7);
                row[7] = resultSet.getObject(8);
                row[8] = resultSet.getObject(9);
                row[9] = resultSet.getObject(10);
                row[10] = resultSet.getObject(11);
                tableModel.addRow(row);
            }

            // JLabel 생성 및 패널에 추가
            JLabel label = new JLabel("전체 수익률보기", SwingConstants.CENTER); // SwingConstants.CENTER로 가운데 정렬
            panel.add(label, BorderLayout.NORTH); // BorderLayout의 NORTH 위치에 추가

            // 테이블 생성 및 패널에 추가
            JTable table = new JTable(tableModel);
            JTableHeader header = table.getTableHeader();
            header.setFont(header.getFont().deriveFont(Font.BOLD, 17));

            // Set the font size for cell content
            table.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

            // 주식 클릭하면 Home2 화면으로 이동
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) { // 클릭 확인
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow();

                        // 여기서 선택된 행의 데이터를 얻을 수 있어요.
                        String stockName = (String) tableModel.getValueAt(row, 1); // 종목명은 두 번째 열(인덱스 1)

                        new StockInfo(userId, stockName); // 종목명을 이용해 페이지를 열거나 처리하는 함수 호출
                    }
                }
            });

            // 테이블 크기 조정
            table.setPreferredScrollableViewportSize(table.getPreferredSize());

            // JScrollPane으로 테이블을 감싸기
            JScrollPane scrollPane = new JScrollPane(table);

            // JScrollPane의 세로 크기를 조정하여 패널 세로 크기의 2/3로 설정
            Dimension panelSize = panel.getPreferredSize();
            int newScrollPaneHeight = (int) (panelSize.height * 0.66); // 2/3의 크기

            scrollPane.setPreferredSize(new Dimension(0, newScrollPaneHeight)); // 가로 크기는 자동으로 조정됨

            // 패널에 JScrollPane 추가
            panel.add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 연결 닫기
            dbConnector.closeConnection();
        }
    }
}