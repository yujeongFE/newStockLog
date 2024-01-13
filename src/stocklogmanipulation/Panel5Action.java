package stocklogmanipulation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Panel5Action { // 주식 매매 기록
    static Object[] row = new Object[8];
    static DefaultTableModel tableModel = new DefaultTableModel();

    // Declare searchList as a class field
    private static JList<String> searchList;

    public static void addFunctionality(JPanel panel, String userId) {
        DBconnection dbConnector = new DBconnection();
        Connection connection = dbConnector.getConnection();

        String id = userId;
        String query = "SELECT s.NAME, l.COMPANY, l.BUYORSELL, l.DATE, l.PRICE, l.QTY, l.RRATIO, l.MEMO FROM stock s, log l WHERE s.CODE = l.CODE AND U_ID = '" + id + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            tableModel.addColumn("종목명");
            tableModel.addColumn("증권사");
            tableModel.addColumn("매도/매수");
            tableModel.addColumn("날짜");
            tableModel.addColumn("주식 단가");
            tableModel.addColumn("수량");
            tableModel.addColumn("매매비용(세금, 수수료)");
            tableModel.addColumn("메모");

            while (resultSet.next()) {
                row[0] = resultSet.getObject(1);
                row[1] = resultSet.getObject(2);
                row[2] = resultSet.getObject(3);
                row[3] = resultSet.getObject(4);
                row[4] = resultSet.getObject(5);
                row[5] = resultSet.getObject(6);
                row[6] = resultSet.getObject(7);
                row[7] = resultSet.getObject(8);
                tableModel.addRow(row);
            }


            // 전체 화면 size 가져오기
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            // width, height 설정
            int width = screenSize.width / 3;
            int height = (screenSize.height-145) / 2;
            int fullheight = screenSize.height-145;


            JButton searchButton = new JButton("매도/매수 기록 추가");
            searchButton.setFont(new Font("굴림", Font.PLAIN, 17));
            searchButton.setPreferredSize(new Dimension( screenSize.width,34));

            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SellBuyFrame();
                }
            });
            panel.add(searchButton, BorderLayout.SOUTH);

            JLabel label = new JLabel("주식 매매 일지", SwingConstants.CENTER);
            panel.add(label, BorderLayout.NORTH);

            JTable table = new JTable(tableModel);
            JTableHeader header = table.getTableHeader();
            header.setFont(header.getFont().deriveFont(Font.BOLD, 17));

            table.setFont(new Font("굴림", Font.PLAIN, 15));

            // 주식 클릭하면 Home2 화면으로 이동
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow();

                        // 여기서 선택된 행의 데이터
                        String stockName = (String) tableModel.getValueAt(row, 0);
                        new StockInfo_new(userId, stockName);
                    }
                }
            });

            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            JScrollPane scrollPane = new JScrollPane(table);

            Dimension panelSize = panel.getPreferredSize();
            int newScrollPaneHeight = (int) (panelSize.height * 0.66);
            scrollPane.setPreferredSize(new Dimension(0, newScrollPaneHeight));

            panel.add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dbConnector.closeConnection();
        }
    }

    public static void SellBuyFrame() {
        JFrame SellBuyFrame = new JFrame("매도/매수 기록 추가");
        SellBuyFrame.setLocation(800, 400);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel l1 = new JLabel();
        JTextField text = new JTextField(15);

        DefaultListModel<String> listModel = new DefaultListModel<>();

        searchList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(searchList);
        panel.add(scrollPane, BorderLayout.CENTER);
        searchList.setVisible(true);
        scrollPane.setVisible(true);

        JButton searchButton = new JButton("검색");
        inputPanel.add(l1);
        inputPanel.add(text);
        inputPanel.add(searchButton);

        panel.add(BorderLayout.NORTH, inputPanel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = text.getText();
                if (!searchTerm.isEmpty()) {
                    performSearch(searchTerm, listModel);
                    searchList.setModel(listModel);
                } else {
                    JOptionPane.showMessageDialog(SellBuyFrame, "Please enter a search term.");
                }
            }
        });

        searchList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = searchList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedStockName = searchList.getModel().getElementAt(index);
                        handleResultLabelClick(selectedStockName);
                    }
                }
            }
        });

        SellBuyFrame.getContentPane().add(panel);
        SellBuyFrame.setSize(400, 400);
        SellBuyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        SellBuyFrame.setVisible(true);
        SellBuyFrame.setLayout(new BoxLayout(SellBuyFrame.getContentPane(), BoxLayout.Y_AXIS));
    }

    private static void performSearch(String searchTerm, DefaultListModel<String> listModel) {
        try {
            String[] dateRange = getLastBusinessDayRange();
            String frdt = dateRange[0];
            String todt = dateRange[1];

            String urlStr = "https://api.odcloud.kr/api/GetStockSecuritiesInfoService/v1/getStockPriceInfo?";
            urlStr += "serviceKey=" + "1%2FWP%2BVc3M5kGU2bikqOuBl9hAtMQ7OeqB24EL0llGF9zC75kdgM1jbsTy90LiI9hmDwU7jeFjW8P%2B1VPFtc%2BDg%3D%3D";
            urlStr += "&beginBasDt=" + frdt;
            urlStr += "&endBasDt=" + todt;
            urlStr += "&likeItmsNm=" + URLEncoder.encode(searchTerm, "UTF-8");
            System.out.println(urlStr);

            HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            System.out.println("결과값: " + response.toString());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(response.toString().getBytes("UTF-8")));

            NodeList itemList = document.getElementsByTagName("item");

            if (itemList.getLength() == 0) {
                JOptionPane.showMessageDialog(null, "검색 결과가 없습니다.");
                return;
            }

            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    String itemName = itemElement.getElementsByTagName("itmsNm").item(0).getTextContent();
                    listModel.addElement(itemName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    private static String[] getLastBusinessDayRange() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -2);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todt = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.MONTH, -1);
        String frdt = dateFormat.format(calendar.getTime());

        return new String[]{frdt, todt};
    }

    private static void handleResultLabelClick(String selectedStockName) {
        SellBuy sb = new SellBuy();
        sb.setFrame(sb);
        sb.setSelectedStockName(selectedStockName);
        sb.openFrame(selectedStockName);
    }}

/* 1. stock table 검색 후 없으면 삽입

String id = userId;
String stockName = stock;
String stockCode = code;

// SQL 쿼리 실행
String query = "SELECT * FROM stock WHERE NAME = '" + stock + "'";
try {
    Statement selectStatement = connection.createStatement();
    ResultSet resultSet = selectStatement.executeQuery(query);

    // 결과가 비어 있는지 확인
    if (!resultSet.next()) {
        // 여기에 쿼리 결과가 비어있을 때 실행할 INSERT 쿼리를 작성하고 실행합니다.
        String insertQuery = "INSERT INTO stock (CODE, NAME) VALUES ('" + stock + "', '" + code + "')";
        Statement insertStatement = connection.createStatement();
        insertStatement.executeUpdate(insertQuery);
    }
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // 연결 닫기 등의 마무리 작업
    dbConnector.closeConnection();
}

1) 매수일 경우
2. buystock table에 insert

try {
    // PreparedStatement 생성
    String insertQuery = "INSERT INTO buystock (U_ID, CODE, COMPANY, B_DATE, B_PRICE, QTY, MEMO) VALUES (?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

    // 값을 설정합니다. 여기서 1, 2, 3은 각각 ?에 해당하는 순서입니다.
    preparedStatement.setString(1, id);
    preparedStatement.setString(2, code);
    preparedStatement.setString(3, "value3");
    preparedStatement.setString(4, "value3");
    preparedStatement.setString(5, "value3");
    preparedStatement.setString(6, "value3");
    preparedStatement.setString(7, "value3");

    // 쿼리 실행
    preparedStatement.executeUpdate();

    System.out.println("데이터가 성공적으로 삽입되었습니다.");
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // 연결 닫기 등의 마무리 작업
    dbConnector.closeConnection();
}

3. log table에 insert
try {
    // PreparedStatement 생성
    String insertQuery = "INSERT INTO log (U_ID, CODE, COMPANY, BUYORSELL, DATE, PRICE, QTY, MEMO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

    preparedStatement.setString(1, id);
    preparedStatement.setString(2, code);
    preparedStatement.setString(3, "value3");
    preparedStatement.setString(4, "매수");
    preparedStatement.setString(5, "value3");
    preparedStatement.setString(6, "value3");
    preparedStatement.setString(7, "value3");
    preparedStatement.setString(8, "value3");

    // 쿼리 실행
    preparedStatement.executeUpdate();

    System.out.println("데이터가 성공적으로 삽입되었습니다.");
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // 연결 닫기 등의 마무리 작업
    dbConnector.closeConnection();
}

4. have table에 insert(null일 경우에만, 아닐 때는 update)
// SQL 쿼리 실행
String query = "SELECT * FROM have WHERE U_ID = '" + stock + "' AND CODE = '" + code + "' AND COMPANY = '" + ? + "'";
try {
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, "company"); // company

    ResultSet resultSet = preparedStatement.executeQuery();

    while (resultSet.next()) {
        id = resultSet.getObject(1);
        stockName = resultSet.getObject(1).toString(); // Object를 String으로 변환하여 stockName에 저장
        code = resultSet.getObject(2);
        company = resultSet.getObject(3);
        Date = resultSet.getObject(4);
        e_price
        qty
    }

    // 결과가 비어 있는지 확인
    if (!resultSet.next()) {
        // 여기에 쿼리 결과가 비어있을 때 실행할 INSERT 쿼리를 작성하고 실행합니다.
        String insertQuery = "INSERT INTO have (U_ID, CODE, COMPANY, DATE, E_PRICE, QTY) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

        insertStatement.setString(1, "value1");
        insertStatement.setString(2, "value2");
        insertStatement.setString(3, "value3");
        insertStatement.setString(4, "value4");
        insertStatement.setString(5, "value4");
        insertStatement.setString(6, "value4");

        // 쿼리 실행
        insertStatement.executeUpdate();
    }
    else {
        // 결과가 있으면 UPDATE 실행
        String updateQuery = "UPDATE have SET column1 = ?, column2 = ?, column3 = ?, column4 = ? WHERE U_ID = ? AND CODE = ? AND COMPANY = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setString(1, "newValue1");
        updateStatement.setString(2, "newValue2");
        updateStatement.setString(3, "newValue3");
        updateStatement.setString(4, "newValue4");
        updateStatement.setString(5, id);
        updateStatement.setString(6, code);
        updateStatement.setString(7, "newValue5");
        updateStatement.executeUpdate();
    }
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // 연결 닫기 등의 마무리 작업
    dbConnector.closeConnection();
}
* */