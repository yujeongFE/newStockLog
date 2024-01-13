package stocklogmanipulation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// 패널 3에 대한 동작을 처리하는 클래스
public class Panel3Action { // 관심주식
    static Object[] row = new Object[7];
    // 데이터를 담을 테이블 모델 생성
    static DefaultTableModel tableModel = new DefaultTableModel();



    public static void addFunctionality(JPanel panel, String userId) {
        // 데이터베이스 연결
        DBconnection dbConnector = new DBconnection();
        Connection connection = dbConnector.getConnection();

        String id = userId;
        // SQL 쿼리 실행
        String query = "SELECT s.NAME, s.CODE, i.CATEGORY, i.MEMO FROM stock s, interest i WHERE s.CODE = i.CODE AND U_ID = '" + id + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // 원하는 컬럼 순서와 이름을 추가
            tableModel.addColumn("종목명");
            tableModel.addColumn("종목코드");
            tableModel.addColumn("현재주가");
            tableModel.addColumn("시장 구분");
            tableModel.addColumn("전일대비등락");
            tableModel.addColumn("전일대비등락비");
            tableModel.addColumn("메모");



            // 결과셋의 데이터를 테이블 모델에 추가
            String stockName = null; // 변수를 루프 바깥에 선언하고 초기화
            while (resultSet.next()) {
                row[0] = resultSet.getObject(1);
                stockName = resultSet.getObject(1).toString(); // Object를 String으로 변환하여 stockName에 저장
                row[1] = resultSet.getObject(2);
                row[3] = resultSet.getObject(3);
                row[6] = resultSet.getObject(4);
                // tableModel.addRow(row);
            }

            // 날짜 범위 설정
            String[] dateRange = getLastBusinessDayRange();
            String frdt = dateRange[0];
            String todt = dateRange[1];

            if (stockName != null) {
                // 종목명을 URL 인코딩하여 API 호출
                StringBuffer stockPriceData = getStockPrice(URLEncoder.encode(stockName, "UTF-8"), frdt, todt);

                if (stockPriceData.length() > 0) {
                    // 데이터 파싱 및 표로 정리하여 출력
                    printStockPriceTable(stockPriceData);
                } else {
                    System.out.println("No stock price data available for the specified parameters.");
                }
            } else {
            }
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            // width, height 설정
            int width = screenSize.width / 3;
            int height = (screenSize.height-145) / 2;
            int fullheight = screenSize.height-145;


            // JButton 생성 및 패널에 추가
            JButton searchButton = new JButton("관심 주식 추가");
            searchButton.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
            searchButton.setPreferredSize(new Dimension( screenSize.width,34));

            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DefaultTableModel DefaultTableModel = new DefaultTableModel();
                    InterestStockFrame(tableModel);
                }
            });
            panel.add(searchButton, BorderLayout.SOUTH);  // Add the button to the SOUTH position of the panel


            // JLabel 생성 및 패널에 추가
            JLabel label = new JLabel("관심 주식", SwingConstants.CENTER); // SwingConstants.CENTER로 가운데 정렬
            panel.add(label, BorderLayout.NORTH); // BorderLayout의 NORTH 위치에 추가

            // 테이블 생성 및 패널에 추가
            JTable table = new JTable(tableModel);

            JTableHeader header = table.getTableHeader();
            header.setFont(header.getFont().deriveFont(Font.BOLD, 17));

            // Set the font size for cell content
            table.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

            // Set a custom cell renderer to control cell font size
            //table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());







            // 주식 클릭하면 Home2 화면으로 이동
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) { // 클릭 확인
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow();

                        // 여기서 선택된 행의 데이터를 얻을 수 있어요.
                        String stockName = (String) tableModel.getValueAt(row, 0); // 종목명은 첫 번째 열(인덱스 0)
                        // System.out.println(stockName);
                        new StockInfo_new(userId, stockName); // 종목명을 이용해 페이지를 열거나 처리하는 함수 호출
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

            System.out.println(newScrollPaneHeight);

            scrollPane.setPreferredSize(new Dimension(0, newScrollPaneHeight)); // 가로 크기는 자동으로 조정됨

            // 패널에 JScrollPane 추가
            panel.add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 연결 닫기
            dbConnector.closeConnection();
        }
    }
    private static String[] getLastBusinessDayRange() {
        Calendar calendar = Calendar.getInstance();

        // 현재 날짜가 토요일이면 금요일로, 일요일이면 금요일로 되돌림
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -2);
        }

        // 현재 날짜를 todt로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todt = dateFormat.format(calendar.getTime());

        // frdt를 todt 기준으로 설정
        calendar.add(Calendar.MONTH, -1);
        String frdt = dateFormat.format(calendar.getTime());

        return new String[]{frdt, todt};
    }
    private static StringBuffer getStockPriceWithDifferentParam(String encodedSearchTerm, String frdt, String todt) throws Exception {
        BufferedReader in = null;
        StringBuffer strBuffer = new StringBuffer();

        try {
            // API 요청 URL을 동적으로 생성
            String urlStr = "https://api.odcloud.kr/api/GetStockSecuritiesInfoService/v1/getStockPriceInfo?";
            urlStr += "serviceKey=" + "1%2FWP%2BVc3M5kGU2bikqOuBl9hAtMQ7OeqB24EL0llGF9zC75kdgM1jbsTy90LiI9hmDwU7jeFjW8P%2B1VPFtc%2BDg%3D%3D";  // API 키를 적절하게 설정
            urlStr += "&beginBasDt=" + frdt;
            urlStr += "&endBasDt=" + todt;
            urlStr += "&likeItmsNm=" + encodedSearchTerm;
            System.out.println(urlStr);

            URL obj = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // API 응답 읽기
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String line;
            while ((line = in.readLine()) != null) {
                strBuffer.append(line);
            }

        } finally {
            // BufferedReader 리소스 닫기
            if (in != null) {
                in.close();
            }
        }
        return strBuffer;
    }

    private static StringBuffer getStockPrice(String likeSrtnCd, String frdt, String todt) throws Exception {
        BufferedReader in = null;
        StringBuffer strBuffer = new StringBuffer();

        try {
            // 외부 API 호출을 위한 URL 설정
            String urlStr = "https://api.odcloud.kr/api/GetStockSecuritiesInfoService/v1/getStockPriceInfo?";
            urlStr += "serviceKey=" + "1%2FWP%2BVc3M5kGU2bikqOuBl9hAtMQ7OeqB24EL0llGF9zC75kdgM1jbsTy90LiI9hmDwU7jeFjW8P%2B1VPFtc%2BDg%3D%3D";  // API 키를 적절하게 설정
            urlStr += "&beginBasDt=" + frdt;
            urlStr += "&endBasDt=" + todt;
            urlStr += "&itmsNm=" + likeSrtnCd;

            URL obj = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // API 응답 읽기
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String line;
            while ((line = in.readLine()) != null) {
                strBuffer.append(line);
            }

        } finally {
            // BufferedReader 리소스 닫기
            if (in != null) {
                in.close();
            }
        }
        return strBuffer;
    }
    private static void printStockPriceTable(StringBuffer xmlData) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(xmlData.toString().getBytes("UTF-8"));
        Document doc = dBuilder.parse(input);
        doc.getDocumentElement().normalize();

        NodeList itemList = doc.getElementsByTagName("item");

        // 출력 행 구성
        if (itemList.getLength() > 0) {
            Node itemNode = itemList.item(0);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;
                row[0] = getValue("itmsNm", itemElement);
                row[1] = getValue("srtnCd", itemElement);
                row[2] = getValue("clpr", itemElement);
                row[3] = getValue("mrktCtg", itemElement);
                row[4] = getValue("vs", itemElement);
                row[5] = getValue("fltRt", itemElement);
                tableModel.addRow(row);
            }
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    private static void InterestStockFrame(DefaultTableModel tableModel) {
        JFrame interestFrame = new JFrame("관심 주식 추가");
        interestFrame.setLocation(200, 400);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel l1 = new JLabel();
        JTextField text = new JTextField(15);
        JButton searchButton = new JButton("검색");

        inputPanel.add(l1);
        inputPanel.add(text);
        inputPanel.add(searchButton);

        panel.add(BorderLayout.NORTH, inputPanel);

        interestFrame.getContentPane().add(panel);
        interestFrame.setSize(400, 400);
        interestFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        interestFrame.setVisible(true);
        interestFrame.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();

        JList<String> searchList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(searchList);
        panel.add(scrollPane, BorderLayout.CENTER); // Changed to CENTER
        searchList.setVisible(true);
        scrollPane.setVisible(true);
        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateList();
            }

            public void updateList() {
                String searchText = text.getText().toLowerCase();
                DefaultListModel<String> filteredModel = new DefaultListModel<>();
                for (int i = 0; i < listModel.size(); i++) {
                    String item = listModel.getElementAt(i);
                    if (item.toLowerCase().contains(searchText)) {
                        filteredModel.addElement(item);
                    }
                }
                searchList.setModel(filteredModel); // Set the model for searchList
                searchList.setVisible(!searchText.isEmpty());
                scrollPane.setVisible(!searchText.isEmpty());
            }
        });

        searchList.addListSelectionListener(e -> {
            String selectedValue = searchList.getSelectedValue();
            if (selectedValue != null) {
                // 테이블에 선택된 항목이 없으면 추가
                boolean alreadyExists = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (selectedValue.equals(tableModel.getValueAt(i, 0))) {
                        alreadyExists = true;
                        break;
                    }
                }

                // 테이블 모델에 추가되지 않은 경우에만 추가
                if (!alreadyExists) {
                    try {
                        // Get the user input from the text field

                        String searchKeyword = text.getText();

                        // Encode the search term
                        String encodedSearchTerm = URLEncoder.encode(searchKeyword, "UTF-8");

                        // Get the last business day range
                        String[] dateRange = getLastBusinessDayRange();
                        String frdt = dateRange[0];
                        String todt = dateRange[1];

                        System.out.println(encodedSearchTerm);

                        // Make the API request
                        StringBuffer stockPriceData = getStockPriceWithDifferentParam(encodedSearchTerm, frdt, todt);

                        if (stockPriceData.length() > 0) {
                            // Update the UI based on the response
                            System.out.println("Stock Price Data:\n" + stockPriceData.toString());
                        } else {
                            System.out.println("No stock price data available for the specified parameters.");
                        }
                    } catch (UnsupportedEncodingException ex) {

                    } catch (Exception ex) {

                    }
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get the user input from the text field
                    String searchKeyword = text.getText();

                    // Encode the search term
                    String encodedSearchTerm = URLEncoder.encode(searchKeyword, "UTF-8");

                    // Get the last business day range
                    String[] dateRange = getLastBusinessDayRange();
                    String frdt = dateRange[0];
                    String todt = dateRange[1];

                    // Make the API request
                    StringBuffer stockPriceData = getStockPriceWithDifferentParam(encodedSearchTerm, frdt, todt);

                    // 검색 버튼 클릭 시
                    if (stockPriceData.length() > 0) {

                        // XML 파싱
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        InputSource is = new InputSource(new StringReader(stockPriceData.toString()));
                        Document document = builder.parse(is);

                        // "itmsNm" 태그에서 데이터 추출
                        listModel.clear(); // 기존 항목 제거
                        NodeList itemList = document.getElementsByTagName("item");
                        for (int i = 0; i < itemList.getLength(); i++) {
                            Element item = (Element) itemList.item(i);
                            String itemName = item.getElementsByTagName("itmsNm").item(0).getTextContent();
                            listModel.addElement(itemName);
                        }
                        DefaultListModel<String> newModel = new DefaultListModel<>();
                        for (int i = 0; i < listModel.size(); i++) {
                            newModel.addElement(listModel.getElementAt(i));
                        }
                        searchList.setModel(newModel);  // 새로운 모델로 업데이트

                        // 변경된 부분: 검색 리스트를 다시 그리도록 강제로 repaint
                        searchList.repaint();
                        searchList.setVisible(true);
                        scrollPane.setVisible(true);
                    } else {
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                }
            }
        });

        searchList.addListSelectionListener(e -> {
            // 선택한 항목의 인덱스 가져오기
            int[] selectedIndices = searchList.getSelectedIndices();

            // 선택한 항목이 없으면 테이블 비우기
            if (selectedIndices.length == 0) {
                tableModel.setRowCount(0);
                return;
            }

            // 기존 테이블 데이터 유지
            int rowCount = tableModel.getRowCount();

            // 선택한 항목들에 대해 각각 API 호출 및 결과를 테이블에 추가
            for (int selectedIndex : selectedIndices) {
                // 기존 테이블에 추가할 행 인덱스 계산
                int rowIndex = rowCount + selectedIndex;

                // 기존 테이블에 이미 해당 항목이 있는지 확인
                boolean isDuplicate = false;
                String selectedValue = listModel.getElementAt(selectedIndex);
                for (int i = 0; i < rowCount; i++) {
                    String existingValue = (String) tableModel.getValueAt(i, 0);
                    if (selectedValue.equals(existingValue)) {
                        isDuplicate = true;
                        break;
                    }
                }

                // 중복이 아닌 경우에만 행 추가
                if (!isDuplicate && rowIndex >= tableModel.getRowCount()) {
                    try {
                        // 수정된 부분: 검색어 대신 사용자가 선택한 항목의 텍스트 사용
                        String selectedItemText = selectedValue;

                        // Encode the search term
                        String encodedSearchTerm = URLEncoder.encode(selectedItemText, "UTF-8");

                        // 마지막 영업일 범위 가져오기
                        String[] dateRange = getLastBusinessDayRange();
                        String frdt = dateRange[0];
                        String todt = dateRange[1];

                        // API 요청
                        StringBuffer stockPriceData = getStockPriceWithDifferentParam(encodedSearchTerm, frdt, todt);

                        if (stockPriceData.length() > 0) {
                            // 응답을 기반으로 UI 업데이트
                            // XML 파싱
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            InputSource is = new InputSource(new StringReader(stockPriceData.toString()));
                            Document document = builder.parse(is);

                            // "itmsNm" 태그에서 데이터 추출
                            NodeList itemList = document.getElementsByTagName("item");

                            // 수정된 부분: 첫 번째 결과만 가져와서 추가
                            if (itemList.getLength() > 0) {
                                Element item = (Element) itemList.item(0);
                                row[0] = item.getElementsByTagName("itmsNm").item(0).getTextContent();
                                row[1] = item.getElementsByTagName("srtnCd").item(0).getTextContent();
                                row[2] = item.getElementsByTagName("clpr").item(0).getTextContent();
                                row[3] = item.getElementsByTagName("clpr").item(0).getTextContent();
                                row[4] = item.getElementsByTagName("vs").item(0).getTextContent();
                                row[5] = item.getElementsByTagName("fltRt").item(0).getTextContent();

                                tableModel.addRow(row);
                            }
                        } else {
                            System.out.println("No stock price data available for the specified parameters.");
                        }
                    } catch (ParserConfigurationException | SAXException | IOException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        // 검색 리스트의 선택 이벤트 핸들러
        searchList.addListSelectionListener(e -> {
            String selectedValue = searchList.getSelectedValue();
            if (selectedValue != null) {
                // 테이블 모델에 추가되지 않은 경우에만 추가
                boolean alreadyExists = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (selectedValue.equals(tableModel.getValueAt(i, 0))) {
                        alreadyExists = true;
                        break;
                    }
                }

                if (!alreadyExists) {
                    try {
                        // Get the user input from the text field
                        String searchKeyword = text.getText();

                        // Encode the search term
                        String encodedSearchTerm = URLEncoder.encode(searchKeyword, "UTF-8");

                        // Get the last business day range
                        String[] dateRange = getLastBusinessDayRange();
                        String frdt = dateRange[0];
                        String todt = dateRange[1];

                        // Make the API request
                        String stockPriceData = getStockPrice(encodedSearchTerm, frdt, todt).toString();

                        if (stockPriceData.length() > 0) {
                            // Update the UI based on the response
                            System.out.println("Stock Price Data:\n" + stockPriceData.toString());

                            // XML 파싱
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            InputSource is = new InputSource(new StringReader(stockPriceData.toString()));
                            Document document = builder.parse(is);

                            // "itmsNm" 태그에서 데이터 추출
                            NodeList itemList = document.getElementsByTagName("item");

                            if (itemList.getLength() > 0) {
                                // Process only the first item (assuming it's the most recent)
                                Element item = (Element) itemList.item(0);
                                row[0] = item.getElementsByTagName("itmsNm").item(0).getTextContent();
                                row[2] = item.getElementsByTagName("clpr").item(0).getTextContent();
                                row[4] = item.getElementsByTagName("vs").item(0).getTextContent();
                                row[5] = item.getElementsByTagName("fltRt").item(0).getTextContent();

                                // Add a new row to the table model (기존 코드에서 주석 처리된 부분 해제)
                                tableModel.addRow(row);
                            } else {
                                System.out.println("No stock price data available for the specified parameters.");
                            }
                        } else {
                            System.out.println("No stock price data available for the specified parameters.");
                        }
                    } catch (ParserConfigurationException | SAXException | IOException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }


}