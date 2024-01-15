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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

// 패널 6에 대한 동작을 처리하는 클래스
class SI_Panel4Action { // 특정 주식에 대한 매도, 매수 리스트
    static String[] columnNames = {"기준일자", "종목명", "종목단축코드", "시장구분", "종가", "전일대비등락", "전일대비등락비", "시가", "고가"};
    static DefaultTableModel tableModel = new DefaultTableModel(null, columnNames);
    public static void addFunctionality(JPanel panel, String stockName) {
        try {
            Object[] row = new Object[9];

            // 날짜 범위 설정
            String[] dateRange = getLastBusinessDayRange();
            String frdt = dateRange[0];
            String todt = dateRange[1];

            if (stockName != null) {
                // 종목명을 URL 인코딩하여 API 호출
                StringBuffer stockPriceData = getStockPrice(URLEncoder.encode(stockName, "UTF-8"), frdt, todt);

                if (stockPriceData.length() > 0) {
                    // 데이터 파싱 및 표로 정리하여 출력
                    printStockPriceTable(stockPriceData, row);
                } else {
                    System.out.println("No stock price data available for the specified parameters.");
                }
            } else {
            }

        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
        }

        // JLabel 생성 및 패널에 추가
        JLabel label = new JLabel("최근 주가 차트", SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);

        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 17));

        // 폰트 사이즈
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

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
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                String line;
                while ((line = in.readLine()) != null) {
                    strBuffer.append(line);
                }
            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
                // 사용자에게 502 에러가 발생했음을 알리는 메시지
                strBuffer.append("502 에러가 발생했습니다. 프로그램을 다시 실행하세요.");
            }

        } finally {
            // BufferedReader 리소스 닫기
            if (in != null) {
                in.close();
            }
        }
        return strBuffer;
    }

    private static void printStockPriceTable(StringBuffer xmlData, Object[] row) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(xmlData.toString().getBytes("UTF-8"));
        Document doc = dBuilder.parse(input);
        doc.getDocumentElement().normalize();

        NodeList itemList = doc.getElementsByTagName("item");

        // 출력 행 구성
        for (int i = 0; i < itemList.getLength(); i++) {
            Node itemNode = itemList.item(i);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;

                // 각 행 추가
                row[0] = getValue("basDt", itemElement);
                row[1] = getValue("itmsNm", itemElement);
                row[2] = getValue("srtnCd", itemElement);
                row[3] = getValue("mrktCtg", itemElement);
                row[4] = getValue("clpr", itemElement);
                row[5] = getValue("vs", itemElement);
                row[6] = getValue("fltRt", itemElement);
                row[7] = getValue("mkp", itemElement);
                row[8] = getValue("hipr", itemElement);
                tableModel.addRow(row);

            }
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
}